package com.winterhaven_mc.homestar;

import com.winterhaven_mc.homestar.messages.Macro;
import com.winterhaven_mc.homestar.messages.MessageId;
import com.winterhaven_mc.homestar.util.HomeStarFactory;
import com.winterhaven_mc.util.*;

import com.winterhaven_mc.homestar.commands.CommandManager;
import com.winterhaven_mc.homestar.teleport.TeleportManager;
import com.winterhaven_mc.homestar.listeners.PlayerEventListener;

import com.winterhavenmc.util.messagebuilder.MessageBuilder;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;


/**
 * Bukkit plugin to create items that return player to bed spawn when clicked.<br>
 * An alternative to the /home command.
 *
 * @author Tim Savage
 */
public final class PluginMain extends JavaPlugin {

	public MessageBuilder<MessageId, Macro> messageBuilder;
	public SoundConfiguration soundConfig;
	public TeleportManager teleportManager;
	public WorldManager worldManager;
	public CommandManager commandManager;
	public PlayerEventListener playerEventListener;
	public HomeStarFactory homeStarFactory;


	/**
	 * Constructor for testing
	 */
	@SuppressWarnings("unused")
	public PluginMain() {
		super();
	}


	/**
	 * Constructor for testing
	 */
	@SuppressWarnings("unused")
	PluginMain(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
		super(loader, descriptionFile, dataFolder, file);
	}


	@Override
	public void onEnable() {

		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate message builder
		messageBuilder = new MessageBuilder<>(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate player event listener
		playerEventListener = new PlayerEventListener(this);
		
		homeStarFactory = new HomeStarFactory(this);
	}

}
