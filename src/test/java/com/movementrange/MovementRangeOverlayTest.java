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

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.movementrange.MovementRangeOverlay.TileSide;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MovementRangeOverlayTest
{
	private static WorldPoint wp(int x, int y)
	{
		return new WorldPoint(x, y, 0);
	}

	// --- effectiveTier ---

	@Test
	public void effectiveTier_promotesPlayerTileToOne()
	{
		assertEquals(1, MovementRangeOverlay.effectiveTier(0));
	}

	@Test
	public void effectiveTier_preservesPositiveValues()
	{
		assertEquals(1, MovementRangeOverlay.effectiveTier(1));
		assertEquals(2, MovementRangeOverlay.effectiveTier(2));
		assertEquals(3, MovementRangeOverlay.effectiveTier(3));
	}

	// --- isOutward ---

	@Test
	public void isOutward_returnsTrueForMissingNeighbor()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		assertTrue(MovementRangeOverlay.isOutward(tierOf, wp(1, 0), 1));
	}

	@Test
	public void isOutward_returnsFalseForSameTier()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		tierOf.put(wp(1, 0), 1);
		assertFalse(MovementRangeOverlay.isOutward(tierOf, wp(1, 0), 1));
	}

	@Test
	public void isOutward_returnsTrueWhenNeighborIsHigherTier()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		tierOf.put(wp(1, 0), 2);
		// myTier=1 facing tier 2 -> lower tier draws the boundary
		assertTrue(MovementRangeOverlay.isOutward(tierOf, wp(1, 0), 1));
	}

	@Test
	public void isOutward_returnsFalseWhenNeighborIsLowerTier()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		tierOf.put(wp(1, 0), 1);
		// myTier=2 facing tier 1 -> already drawn by tier 1, we don't
		assertFalse(MovementRangeOverlay.isOutward(tierOf, wp(1, 0), 2));
	}

	// --- perimeterSides ---

	@Test
	public void perimeter_isolatedTile_drawsAllFourSides()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		WorldPoint center = wp(0, 0);
		tierOf.put(center, 1);

		Set<TileSide> sides = MovementRangeOverlay.perimeterSides(center, tierOf);
		assertEquals(EnumSet.allOf(TileSide.class), sides);
	}

	@Test
	public void perimeter_sameTierOnAllSides_drawsNothing()
	{
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		WorldPoint center = wp(0, 0);
		tierOf.put(center, 1);
		tierOf.put(wp(0, 1), 1);   // north
		tierOf.put(wp(0, -1), 1);  // south
		tierOf.put(wp(1, 0), 1);   // east
		tierOf.put(wp(-1, 0), 1);  // west

		Set<TileSide> sides = MovementRangeOverlay.perimeterSides(center, tierOf);
		assertTrue("expected no perimeter sides but got " + sides, sides.isEmpty());
	}

	@Test
	public void perimeter_playerTileAsNeighbor_doesNotDrawAgainstIt()
	{
		// tier 1 tile north of player (tier 0) should NOT stroke its south edge,
		// because effectiveTier promotes the player tile to tier 1 for boundary checks.
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		WorldPoint player = wp(0, 0);
		WorldPoint north = wp(0, 1);
		tierOf.put(player, 0);
		tierOf.put(north, 1);

		Set<TileSide> sides = MovementRangeOverlay.perimeterSides(north, tierOf);
		assertFalse("south edge faces player tile, should not draw", sides.contains(TileSide.SOUTH));
		// Other three sides have no neighbor in the map, so they should draw.
		assertTrue(sides.contains(TileSide.NORTH));
		assertTrue(sides.contains(TileSide.EAST));
		assertTrue(sides.contains(TileSide.WEST));
	}

	@Test
	public void perimeter_interTierBoundary_ownedByLowerTier()
	{
		// tier 1 at (0,0), tier 2 at (1,0). The boundary between them is the
		// east edge of tier 1 and the west edge of tier 2.
		// Convention: the lower tier owns the boundary.
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		WorldPoint t1 = wp(0, 0);
		WorldPoint t2 = wp(1, 0);
		tierOf.put(t1, 1);
		tierOf.put(t2, 2);

		Set<TileSide> tier1Sides = MovementRangeOverlay.perimeterSides(t1, tierOf);
		Set<TileSide> tier2Sides = MovementRangeOverlay.perimeterSides(t2, tierOf);

		assertTrue("tier 1 should stroke its east edge (facing higher tier)",
			tier1Sides.contains(TileSide.EAST));
		assertFalse("tier 2 should NOT stroke its west edge (facing lower tier)",
			tier2Sides.contains(TileSide.WEST));
	}

	@Test
	public void perimeter_outerEdgeOfHighestTier_stillDrawn()
	{
		// tier 2 tile with no higher-tier neighbors anywhere -> all four edges draw.
		Map<WorldPoint, Integer> tierOf = new HashMap<>();
		WorldPoint t2 = wp(0, 0);
		tierOf.put(t2, 2);

		Set<TileSide> sides = MovementRangeOverlay.perimeterSides(t2, tierOf);
		assertEquals(EnumSet.allOf(TileSide.class), sides);
	}
}
