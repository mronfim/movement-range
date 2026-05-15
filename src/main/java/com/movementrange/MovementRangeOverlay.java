package com.movementrange;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MovementRangeOverlay extends Overlay {

    public enum TileSide { NORTH, EAST, SOUTH, WEST }

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
        Player player = client.getLocalPlayer();
        if (player == null) return null;

        int maxDepth = 2;
        Map<WorldPoint, Integer> reachable = computeReachable(maxDepth);
        Set<WorldPoint> reachableSet = reachable.keySet();

        for (Map.Entry<WorldPoint, Integer> entry : reachable.entrySet()) {
            int depth = entry.getValue();
            if (depth == 0) continue;

            LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
            if (lp == null) continue;

            Color color = config.highlightCurrentColor();
            Color fillColor = config.currentTileFillColor();
            Stroke stroke = new BasicStroke((float) config.currentTileBorderWidth());

            Set<TileSide> perimeter = perimeterSides(entry.getKey(), reachableSet);
            renderTilePartial(graphics, lp, fillColor, color, stroke, perimeter);
        }

        return null;
    }

    private Map<WorldPoint, Integer> computeReachable(int maxDepth) {
        Player player = client.getLocalPlayer();
        if (player == null) return Collections.emptyMap();

        WorldView wv = client.getTopLevelWorldView();
        WorldPoint start = player.getWorldLocation();

        Map<WorldPoint, Integer> dist = new HashMap<>();
        dist.put(start, 0);

        List<WorldPoint> frontier = new ArrayList<>();
        frontier.add(start);

        for (int depth = 1; depth <= maxDepth; depth++) {
            List<WorldPoint> next = new ArrayList<>();
            for (WorldPoint node : frontier) {
                WorldArea area = new WorldArea(node, 1, 1);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;

                        if (config.onlyWalkableTiles() && !area.canTravelInDirection(wv, dx, dy))
                            continue;

                        WorldPoint neighbor = new WorldPoint(node.getX() + dx, node.getY() + dy, node.getPlane());

                        if (dist.containsKey(neighbor)) continue;
                        dist.put(neighbor, depth);
                        next.add(neighbor);
                    }
                }
            }
            frontier = next;
        }

        return dist;
    }

    private Set<TileSide> perimeterSides(WorldPoint tile, Set<WorldPoint> reachable) {
        int plane = tile.getPlane();
        EnumSet<TileSide> sides = EnumSet.noneOf(TileSide.class);

        // OSRS local/world coords: +y is north, +x is east
        WorldPoint north = new WorldPoint(tile.getX(), tile.getY() + 1, plane);
        WorldPoint south = new WorldPoint(tile.getX(), tile.getY() - 1, plane);
        WorldPoint east = new WorldPoint(tile.getX() + 1, tile.getY(), plane);
        WorldPoint west = new WorldPoint(tile.getX() - 1, tile.getY(), plane);

        if (!reachable.contains(north)) sides.add(TileSide.NORTH);
        if (!reachable.contains(south)) sides.add(TileSide.SOUTH);
        if (!reachable.contains(east)) sides.add(TileSide.EAST);
        if (!reachable.contains(west)) sides.add(TileSide.WEST);

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
}
