package com.bosstilerespawntimer;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("Boss Tile Respawn Timer")
public interface BossTileRespawnTimerConfig extends Config
{
	@Alpha
	@ConfigItem(
			position = 10,
			keyName = "borderColor",
			name = "Border Color",
			description = "Border color of the boss tile"
	)
	default Color highlightColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
			position = 11,
			keyName = "fillColor",
			name = "Fill Color",
			description = "Fill color of the boss tile"
	)
	default Color fillColor()
	{
		return new Color(0, 255, 255, 20);
	}
}
