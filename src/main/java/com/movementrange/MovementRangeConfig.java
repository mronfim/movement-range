package com.movementrange;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("movementrangeindicators")
public interface MovementRangeConfig extends Config
{
	@Alpha
	@ConfigItem(
			keyName = "highlightCurrentColor",
			name = "Highlight color",
			description = "Configures the highlight color of the tiles in movable range"
	)
	default Color highlightCurrentColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
			keyName = "currentTileFillColor",
			name = "Fill color",
			description = "Configures the fill color of the tiles in movable range"
	)
	default Color currentTileFillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@ConfigItem(
			keyName = "currentTileBorderWidth",
			name = "Border width",
			description = "Width of the tile marker border"
	)
	default double currentTileBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "highlightIndividualTiles",
			name = "Highlight individual tiles",
			description = "Highlights individual tiles within the reachable movement grid"
	)
	default boolean highlightIndividualTiles()
	{
		return false;
	}

	@ConfigItem(
			keyName = "onlyWalkableTiles",
			name = "Only walkable tiles",
			description = "Only highlight tiles that are walkable"
	)
	default boolean onlyWalkableTiles()
	{
		return false;
	}
}
