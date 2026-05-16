/*
 * Copyright (c) 2026, mronfim
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.movementrange;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup(MovementRangeConfig.GROUP)
public interface MovementRangeConfig extends Config
{
	String GROUP = "movementrangeindicators";


	@ConfigSection(
			name = "General",
			description = "General settings",
			position = 0
	)
	String generalSection = "generalSection";

	@ConfigSection(
			name = "Tier 1 (1 tick)",
			description = "Appearance for tiles reachable in 1 tick",
			position = 1
	)
	String tier1Section = "tier1Section";

	@ConfigSection(
			name = "Tier 2 (2 ticks)",
			description = "Appearance for tiles reachable in 2 ticks",
			position = 2,
			closedByDefault = true
	)
	String tier2Section = "tier2Section";

	@ConfigSection(
			name = "Tier 3 (3 ticks)",
			description = "Appearance for tiles reachable in 3 ticks",
			position = 3,
			closedByDefault = true
	)
	String tier3Section = "tier3Section";

	@ConfigItem(
			keyName = "highlightIndividualTiles",
			name = "Highlight individual tiles",
			description = "Highlights individual tiles within the reachable movement grid",
			position = 0,
			section = generalSection
	)
	default boolean highlightIndividualTiles() { return false; }

	@ConfigItem(
			keyName = "onlyWalkableTiles",
			name = "Only walkable tiles",
			description = "Only highlight tiles that are walkable",
			position = 1,
			section = generalSection
	)
	default boolean onlyWalkableTiles() { return true; }

	@Range(min = 1, max = 3)
	@ConfigItem(
			keyName = "maxTicks",
			name = "Max Ticks",
			description = "Number of run-mode ticks to highlight reachable tiles for",
			position = 2,
			section = generalSection
	)
	default int maxTicks() { return 1; }

	@Range(min = 0, max = 10)
	@ConfigItem(
			keyName = "tier1BorderWidth",
			name = "Border width",
			description = "Width of the tile marker border within 1 tick",
			position = 0,
			section = tier1Section
	)
	default double tier1BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier1FillColor",
			name = "Fill color",
			description = "Color for tiles within 1 tick",
			position = 1,
			section = tier1Section
	)
	default Color tier1FillColor()
	{
		return new Color(0, 0, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier1PerimeterColor",
			name = "Perimeter color",
			description = "Color for tile perimeter around tiles within 1 tick",
			position = 2,
			section = tier1Section
	)
	default Color tier1PerimeterColor()
	{
		return Color.CYAN;
	}

	@Range(min = 0, max = 10)
	@ConfigItem(
			keyName = "tier2BorderWidth",
			name = "Border width",
			description = "Width of the tile marker border within 2 ticks",
			position = 0,
			section = tier2Section
	)
	default double tier2BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier2FillColor",
			name = "Fill color",
			description = "Color for tiles within 2 ticks",
			position = 1,
			section = tier2Section
	)
	default Color tier2FillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier2PerimeterColor",
			name = "Perimeter color",
			description = "Color for tile perimeter around tiles within 2 ticks",
			position = 2,
			section = tier2Section
	)
	default Color tier2PerimeterColor()
	{
		return Color.YELLOW;
	}

	@Range(min = 0, max = 10)
	@ConfigItem(
			keyName = "tier3BorderWidth",
			name = "Border width",
			description = "Width of the tile marker border within 3 ticks",
			position = 0,
			section = tier3Section
	)
	default double tier3BorderWidth() { return 2; }

	@Alpha
	@ConfigItem(
			keyName = "tier3FillColor",
			name = "Fill color",
			description = "Color for tiles within 3 ticks",
			position = 1,
			section = tier3Section
	)
	default Color tier3FillColor()
	{
		return new Color(0, 0, 0, 25);
	}

	@Alpha
	@ConfigItem(
			keyName = "tier3PerimeterColor",
			name = "Perimeter color",
			description = "Color for tile perimeter around tiles within 3 ticks",
			position = 2,
			section = tier3Section
	)
	default Color tier3PerimeterColor()
	{
		return Color.ORANGE;
	}
}
