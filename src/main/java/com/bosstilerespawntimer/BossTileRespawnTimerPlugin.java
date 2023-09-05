package com.bosstilerespawntimer;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
        name = "Boss Tile Respawn Timer"
)
public class BossTileRespawnTimerPlugin extends Plugin {
    private static final int MAX_ACTOR_VIEW_RANGE = 15;

    @Inject
    private BossTileRespawnTimerConfig config;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private NpcUtil npcUtil;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NpcRespawnOverlay npcRespawnOverlay;

    @Getter(AccessLevel.PACKAGE)
    private Instant lastTickUpdate;

    private final Map<String, MemorizedNpc> memorizedNpcs = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, MemorizedNpc> deadNpcsToDisplay = new HashMap<>();

    @Provides
    BossTileRespawnTimerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BossTileRespawnTimerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(npcRespawnOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(npcRespawnOverlay);
        clientThread.invoke(() ->
        {
            deadNpcsToDisplay.clear();
            memorizedNpcs.clear();
        });
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        final NPC npc = npcSpawned.getNpc();

        if (npc.getName() == null) {
            return;
        }

        final Boss boss;
        if (npc.getId() == NpcID.WHIRLPOOL_496) {
            boss = Boss.KRAKEN;
        } else {
            boss = Boss.find(npc.getId());
        }

        if (boss == null) {
            return;
        }

        memorizedNpcs.putIfAbsent(boss.name(), new MemorizedNpc(npc, boss.getSpawnTime()));

        if (isInViewRange(client.getLocalPlayer().getWorldLocation(), npc.getWorldLocation())) {
            final MemorizedNpc mn = memorizedNpcs.get(boss.name());

            if (mn.getDiedOnTick() != -1) {
                mn.setDiedOnTick(-1);
            }

            final WorldPoint npcLocation = npc.getWorldLocation();

            // An NPC can move in the same tick as it spawns, so we also have
            // to consider whatever tile is behind the npc
            final WorldPoint possibleOtherNpcLocation = getWorldLocationBehind(npc);

            mn.getPossibleRespawnLocations().removeIf(x ->
                    !x.equals(npcLocation) && !x.equals(possibleOtherNpcLocation));

            if (mn.getPossibleRespawnLocations().isEmpty()) {
                mn.getPossibleRespawnLocations().add(npcLocation);
                mn.getPossibleRespawnLocations().add(possibleOtherNpcLocation);
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        final NPC npc = npcDespawned.getNpc();

        if (!npcUtil.isDying(npc)) {
            return;
        }

        Boss boss = Boss.find(npc.getId());
        if (boss == null) {
            return;
        }

        if (isInViewRange(client.getLocalPlayer().getWorldLocation(), npc.getWorldLocation())) {
            final MemorizedNpc mn = memorizedNpcs.get(boss.name());

            if (mn != null) {
                mn.setDiedOnTick(client.getTickCount() + 1); // This runs before tickCounter updates, so we add 1

                if (!mn.getPossibleRespawnLocations().isEmpty()) {
                    deadNpcsToDisplay.put(mn.getNpcName(), mn);
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        removeOldHighlightedRespawns();
        lastTickUpdate = Instant.now();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN ||
                event.getGameState() == GameState.HOPPING) {
            deadNpcsToDisplay.clear();
        }
    }

    private void removeOldHighlightedRespawns() {
        deadNpcsToDisplay.values().removeIf(x -> x.getDiedOnTick() + x.getRespawnTime() <= client.getTickCount() + 1);
    }

    private static boolean isInViewRange(WorldPoint wp1, WorldPoint wp2) {
        int distance = wp1.distanceTo(wp2);
        return distance < MAX_ACTOR_VIEW_RANGE;
    }

    private static WorldPoint getWorldLocationBehind(NPC npc) {
        final int orientation = npc.getOrientation() / 256;
        int dx = 0, dy = 0;

        switch (orientation) {
            case 0: // South
                dy = -1;
                break;
            case 1: // Southwest
                dx = -1;
                dy = -1;
                break;
            case 2: // West
                dx = -1;
                break;
            case 3: // Northwest
                dx = -1;
                dy = 1;
                break;
            case 4: // North
                dy = 1;
                break;
            case 5: // Northeast
                dx = 1;
                dy = 1;
                break;
            case 6: // East
                dx = 1;
                break;
            case 7: // Southeast
                dx = 1;
                dy = -1;
                break;
        }

        final WorldPoint currWP = npc.getWorldLocation();
        return new WorldPoint(currWP.getX() - dx, currWP.getY() - dy, currWP.getPlane());
    }
}
