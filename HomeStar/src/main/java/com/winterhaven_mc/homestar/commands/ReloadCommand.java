package com.winterhaven_mc.homestar.commands;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.messages.Message;
import com.winterhaven_mc.homestar.sounds.SoundId;
import com.winterhaven_mc.util.LanguageManager;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.homestar.messages.MessageId.*;
import static com.winterhaven_mc.homestar.messages.MessageId.COMMAND_SUCCESS_RELOAD;


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
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("homestar.reload")) {
			Message.create(sender, PERMISSION_DENIED_RELOAD).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.size() > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		LanguageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send();
		return true;
	}

}
