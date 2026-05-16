package com.movementrange;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("movementrangeindicators")
public interface MovementRangeConfig extends Config
{
	@ConfigItem(
			keyName = "highlightIndividualTiles",
			name = "Highlight individual tiles",
			description = "Highlights individual tiles within the reachable movement grid"
	)
	default boolean highlightIndividualTiles() { return false; }

	@ConfigItem(
			keyName = "onlyWalkableTiles",
			name = "Only walkable tiles",
			description = "Only highlight tiles that are walkable"
	)
	default boolean onlyWalkableTiles() { return false; }

	@Range(min = 1, max = 3)
	@ConfigItem(
			keyName = "maxTicks",
			name = "Max Ticks",
			description = "Number of run-mode ticks to highlight reachable tiles for"
	)
	default int maxTicks() { return 1; }

	@ConfigItem(
			keyName = "tier1BorderWidth",
			name = "Tier 1 border width",
			description = "Width of the tile marker border within 1 tick"
	)
	default double tier1BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier1FillColor",
			name = "Tier 1 fill color",
			description = "Color for tiles within 1 tick"
	)
	default Color tier1FillColor()
	{
		return new Color(0, 0, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier1PerimeterColor",
			name = "Tier 1 perimeter color",
			description = "Color for tile perimeter around tiles within 1 tick"
	)
	default Color tier1PerimeterColor()
	{
		return Color.CYAN;
	}


	@ConfigItem(
			keyName = "tier2BorderWidth",
			name = "Tier 2 border width",
			description = "Width of the tile marker border within 2 ticks"
	)
	default double tier2BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier2FillColor",
			name = "Tier 2 fill color",
			description = "Color for tiles within 2 ticks"
	)
	default Color tier2FillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier2PerimeterColor",
			name = "Tier 2 perimeter color",
			description = "Color for tile perimeter around tiles within 2 ticks"
	)
	default Color tier2PerimeterColor()
	{
		return Color.CYAN;
	}


	@ConfigItem(
			keyName = "tier3BorderWidth",
			name = "Tier 3 border width",
			description = "Width of the tile marker border within 3 ticks"
	)
	default double tier3BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier3FillColor",
			name = "Tier 3 fill color",
			description = "Color for tiles within 3 ticks"
	)
	default Color tier3FillColor()
	{
		return new Color(0, 0, 0, 25);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier3PerimeterColor",
			name = "Tier 3 perimeter color",
			description = "Color for tile perimeter around tiles within 3 ticks"
	)
	default Color tier3PerimeterColor()
	{
		return Color.CYAN;
	}
}
