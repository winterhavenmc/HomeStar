package com.winterhaven_mc.homestar;

import org.bukkit.plugin.java.JavaPlugin;

import com.winterhaven_mc.homestar.commands.CommandManager;
import com.winterhaven_mc.homestar.listeners.PlayerEventListener;
import com.winterhaven_mc.homestar.teleport.TeleportManager;
import com.winterhaven_mc.homestar.util.MessageManager;
import com.winterhaven_mc.util.WorldManager;


/**
 * Bukkit plugin to create items that return player to
 * bed spawn when clicked.<br>
 * An alternative to the /home command.
 * 
 * @author      Tim Savage
 * @version		1.0
 */
public final class PluginMain extends JavaPlugin {
	
	// static reference to main class
	public static PluginMain instance;

	public Boolean debug = getConfig().getBoolean("debug");
	
	public CommandManager commandManager;
	public MessageManager messageManager;
	public TeleportManager teleportManager;
	public WorldManager worldManager;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;
		
		// install default config.yml if not present  
		saveDefaultConfig();
		
		// instantiate world manager
		worldManager = new WorldManager(this);
		
		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);
		
		// instantiate player event listener
		new PlayerEventListener(this);
	}

}
