/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.homestar;

import com.winterhavenmc.homestar.commands.CommandManager;
import com.winterhavenmc.homestar.listeners.PlayerEventListener;
import com.winterhavenmc.homestar.listeners.PlayerInteractEventListener;
import com.winterhavenmc.homestar.teleport.TeleportHandler;
import com.winterhavenmc.homestar.util.HomeStarUtility;
import com.winterhavenmc.homestar.util.MetricsHandler;
import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.library.soundconfig.SoundConfiguration;
import com.winterhavenmc.library.soundconfig.YamlSoundConfiguration;
import com.winterhavenmc.library.worldmanager.WorldManager;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Bukkit plugin to create items that return player to bed spawn when clicked.<br>
 * An alternative to the /home command.
 *
 * @author Tim Savage
 */
public final class PluginMain extends JavaPlugin
{
	public MessageBuilder messageBuilder;
	public SoundConfiguration soundConfig;
	public TeleportHandler teleportHandler;
	public WorldManager worldManager;
	public CommandManager commandManager;
	public HomeStarUtility homeStarUtility;


	@Override
	public void onEnable()
	{
		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate message builder
		messageBuilder = MessageBuilder.create(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate teleport manager
		teleportHandler = new TeleportHandler(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate player event listeners
		new PlayerEventListener(this);
		new PlayerInteractEventListener(this);

		// instantiate homestar factory
		homeStarUtility = new HomeStarUtility(this);

		// instantiate metrics handler
		new MetricsHandler(this);
	}

}
