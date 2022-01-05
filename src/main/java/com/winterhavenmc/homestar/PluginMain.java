package com.winterhavenmc.homestar;

import com.winterhavenmc.homestar.messages.Macro;
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.commands.CommandManager;
import com.winterhavenmc.homestar.teleport.TeleportManager;
import com.winterhavenmc.homestar.listeners.PlayerEventListener;
import com.winterhavenmc.homestar.util.HomeStarFactory;

import com.winterhavenmc.util.messagebuilder.MessageBuilder;
import com.winterhavenmc.util.soundconfig.SoundConfiguration;
import com.winterhavenmc.util.soundconfig.YamlSoundConfiguration;
import com.winterhavenmc.util.worldmanager.WorldManager;

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
	PluginMain(final JavaPluginLoader loader,
	           final PluginDescriptionFile descriptionFile,
	           final File dataFolder,
	           final File file) {
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
