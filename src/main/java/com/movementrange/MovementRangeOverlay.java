package com.movementrange;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MovementRangeOverlay extends Overlay {

    private enum TileSide { NORTH, EAST, SOUTH, WEST }
    private static final int MAX_TICKS = 3;

    private final Client client;
    private final MovementRangeConfig config;

    @Inject
    private MovementRangeOverlay(Client client, MovementRangeConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getGameState() != GameState.LOGGED_IN) return null;

        Player player = client.getLocalPlayer();
        if (player == null) return null;

        Map<WorldPoint, Integer> tiles = computeReachableByTier(config.maxTicks());

        for (Map.Entry<WorldPoint, Integer> entry : tiles.entrySet()) {
            int tier = entry.getValue();
            if (tier == 0) continue;

            LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
            if (lp == null) continue;

            Color fillColor = fillForTier(tier);
            Color strokeColor = strokeForTier(tier);
            Stroke stroke = new BasicStroke((float) borderWidthForTier(tier));

            Set<TileSide> perimeter = perimeterSides(entry.getKey(), tiles);
            renderTilePartial(graphics, lp, fillColor, strokeColor, stroke, perimeter);
        }

        return null;
    }

    private Map<WorldPoint, Integer> computeReachableByTier(int maxTicks) {
        Player player = client.getLocalPlayer();
        if (player == null) return Collections.emptyMap();

        int ticks = Math.min(Math.max(maxTicks, 1), MAX_TICKS);
        int maxSteps = 2 * ticks;

        WorldView wv = client.getTopLevelWorldView();
        WorldPoint start = player.getWorldLocation();

        Map<WorldPoint, Integer> tierOf = new HashMap<>();
        tierOf.put(start, 0);

        List<WorldPoint> frontier = new ArrayList<>();
        frontier.add(start);

        for (int step = 1; step <= maxSteps; step++) {
            // step 1,2 -> tier 1   step 3,4 -> tier 2    step 5,6 -> tier 3
            int tier = (step + 1) / 2;

            List<WorldPoint> next = new ArrayList<>();
            for (WorldPoint node : frontier) {
                WorldArea area = new WorldArea(node, 1, 1);

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        if (config.onlyWalkableTiles() && !area.canTravelInDirection(wv, dx, dy)) continue;

                        WorldPoint neighbor = new WorldPoint(
                                node.getX() + dx,
                                node.getY() + dy,
                                node.getPlane());

                        if (tierOf.containsKey(neighbor)) continue;

                        tierOf.put(neighbor, tier);
                        next.add(neighbor);
                    }
                }
            }
            frontier = next;
        }

        return tierOf;
    }

    private Set<TileSide> perimeterSides(WorldPoint tile, Map<WorldPoint, Integer> tierOf) {
        int myTier = tierOf.get(tile);
        int plane = tile.getPlane();
        EnumSet<TileSide> sides = EnumSet.noneOf(TileSide.class);

        // OSRS local/world coords: +y is north, +x is east
        WorldPoint north = new WorldPoint(tile.getX(), tile.getY() + 1, plane);
        WorldPoint south = new WorldPoint(tile.getX(), tile.getY() - 1, plane);
        WorldPoint east = new WorldPoint(tile.getX() + 1, tile.getY(), plane);
        WorldPoint west = new WorldPoint(tile.getX() - 1, tile.getY(), plane);

        if (isOutward(tierOf, north, myTier)) sides.add(TileSide.NORTH);
        if (isOutward(tierOf, south, myTier)) sides.add(TileSide.SOUTH);
        if (isOutward(tierOf, east, myTier)) sides.add(TileSide.EAST);
        if (isOutward(tierOf, west, myTier)) sides.add(TileSide.WEST);

        return sides;
    }

    private void renderTilePartial(
            Graphics2D graphics,
            LocalPoint tile,
            Color fillColor,
            Color strokeColor,
            Stroke stroke,
            Set<TileSide> sides) {

        if (tile == null) return;

        Polygon poly = Perspective.getCanvasTilePoly(client, tile);
        if (poly == null) return;

        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fill(poly);
        }

        if (strokeColor != null && sides != null && (!sides.isEmpty() || config.highlightIndividualTiles())) {
            graphics.setColor(strokeColor);
            graphics.setStroke(stroke);

            int[] x = poly.xpoints;
            int[] y = poly.ypoints;

            if (sides.contains(TileSide.SOUTH) || config.highlightIndividualTiles()) graphics.drawLine(x[0], y[0], x[1], y[1]);
            if (sides.contains(TileSide.EAST) || config.highlightIndividualTiles()) graphics.drawLine(x[1], y[1], x[2], y[2]);
            if (sides.contains(TileSide.NORTH) || config.highlightIndividualTiles()) graphics.drawLine(x[2], y[2], x[3], y[3]);
            if (sides.contains(TileSide.WEST) || config.highlightIndividualTiles()) graphics.drawLine(x[3], y[3], x[0], y[0]);
        }
    }


    private boolean isOutward(Map<WorldPoint, Integer> tierOf, WorldPoint wp, int myTier) {
        Integer t = tierOf.get(wp);
        if (t == null) return true;
        return effectiveTier(t) > myTier;
    }

    private static int effectiveTier(int tier) {
        return Math.max(tier, 1);   // player tile (tier 0) treated as tier 1 for boundaries
    }

    private double borderWidthForTier(int tier) {
        double width;
        switch (tier) {
            case 2:  width = config.tier2BorderWidth(); break;
            case 3:  width = config.tier3BorderWidth(); break;
            case 1:
            default: width = config.tier1BorderWidth(); break;
        }
        return Math.max(0, width);  // BasicStroke throws on negative width
    }

    private Color fillForTier(int tier) {
        switch (tier) {
            case 1: return config.tier1FillColor();
            case 2: return config.tier2FillColor();
            case 3: return config.tier3FillColor();
            default: return config.tier1FillColor();
        }
    }

    private Color strokeForTier(int tier) {
        switch (tier) {
            case 1: return config.tier1PerimeterColor();
            case 2: return config.tier2PerimeterColor();
            case 3: return config.tier3PerimeterColor();
            default: return config.tier1PerimeterColor();
        }
    }
}
