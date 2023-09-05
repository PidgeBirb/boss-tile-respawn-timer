package com.bosstilerespawntimer;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BossTileRespawnTimerTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BossTileRespawnTimerPlugin.class);
		RuneLite.main(args);
	}
}