package com.winterhaven_mc.homestar;

import com.winterhaven_mc.util.WorldManager;
import com.winterhaven_mc.util.SoundConfiguration;
import com.winterhaven_mc.util.YamlSoundConfiguration;
import com.winterhaven_mc.homestar.commands.CommandManager;
import com.winterhaven_mc.homestar.teleport.TeleportManager;
import com.winterhaven_mc.homestar.messages.MessageManager;
import com.winterhaven_mc.homestar.listeners.PlayerEventListener;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Bukkit plugin to create items that return player to
 * bed spawn when clicked.<br>
 * An alternative to the /home command.
 *
 * @author Tim Savage
 * @version 1.0
 */
public final class PluginMain extends JavaPlugin {

	// static reference to main class
	public static PluginMain instance;

	public Boolean debug = getConfig().getBoolean("debug");

	@SuppressWarnings("WeakerAccess")
	public CommandManager commandManager;
	public MessageManager messageManager;
	public SoundConfiguration soundConfig;
	public TeleportManager teleportManager;
	public WorldManager worldManager;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;

		// install default config.yml if not present  
		saveDefaultConfig();

		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);
	}

}
