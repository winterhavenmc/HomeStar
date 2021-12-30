package com.winterhaven_mc.homestar.commands;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.sounds.SoundId;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.homestar.messages.MessageId.*;


public class ReloadCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ReloadCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("reload");
		this.setUsage("/homestar reload");
		this.setDescription(COMMAND_HELP_RELOAD);
		this.setMaxArgs(0);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("homestar.reload")) {
			plugin.messageBuilder.build(sender, PERMISSION_DENIED_RELOAD).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		plugin.languageHandler.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// send reloaded message
		plugin.messageBuilder.build(sender, COMMAND_SUCCESS_RELOAD).send(plugin.languageHandler);
		return true;
	}

}
