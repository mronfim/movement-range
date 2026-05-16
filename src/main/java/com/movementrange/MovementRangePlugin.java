package com.movementrange;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Movement Range",
	description = "Show the tiles you can immediately move to.",
	tags = {"tile", "marker", "ground", "highlight", "overlay"}
)
public class MovementRangePlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MovementRangeOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	MovementRangeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MovementRangeConfig.class);
	}
}
