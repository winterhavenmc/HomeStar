package com.winterhaven_mc.homestar;

import com.winterhaven_mc.util.LanguageManager;
import com.winterhaven_mc.util.SoundConfiguration;
import com.winterhaven_mc.util.YamlSoundConfiguration;
import com.winterhaven_mc.util.WorldManager;
import com.winterhaven_mc.homestar.commands.CommandManager;
import com.winterhaven_mc.homestar.teleport.TeleportManager;
import com.winterhaven_mc.homestar.listeners.PlayerEventListener;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Bukkit plugin to create items that return player to bed spawn when clicked.<br>
 * An alternative to the /home command.
 *
 * @author Tim Savage
 */
public final class PluginMain extends JavaPlugin {

	public SoundConfiguration soundConfig;
	public TeleportManager teleportManager;
	public WorldManager worldManager;

	@Override
	public void onEnable() {

		// install default configuration file if not already present
		saveDefaultConfig();

		// initialize language manager
		LanguageManager.init();

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate command manager
		new CommandManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);
	}

}
