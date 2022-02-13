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

package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.homestar.sounds.SoundId;

import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;


final class ReloadCommand extends SubcommandAbstract {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ReloadCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "reload";
		this.usageString = "/homestar reload";
		this.description = MessageId.COMMAND_HELP_RELOAD;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("homestar.reload")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_RELOAD).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		plugin.messageBuilder.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload homestar factory
		plugin.homeStarFactory.reload();

		// send reloaded message
		plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_RELOAD).send();
		return true;
	}

}
