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
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * Class that implements command executor and tab completer for HomeStar commands
 */
public final class CommandManager implements TabExecutor
{
	// reference to main class
	private final PluginMain plugin;

	// instantiate subcommand map
	private final SubcommandRegistry subcommandRegistry = new SubcommandRegistry();


	/**
	 * Class constructor for CommandManager
	 *
	 * @param plugin reference to main class
	 */
	public CommandManager(final PluginMain plugin)
	{
		this.plugin = plugin;

		// register this class as command executor
		Objects.requireNonNull(plugin.getCommand("homestar")).setExecutor(this);

		// register subcommands
		for (SubcommandType subcommandType : SubcommandType.values())
		{
			subcommandRegistry.register(subcommandType.create(plugin));
		}

		// register help command
		subcommandRegistry.register(new HelpSubcommand(plugin, subcommandRegistry));
	}


	/**
	 * Tab completer for HomeStar
	 */
	@Override
	public List<String> onTabComplete(final @Nonnull CommandSender sender, final @Nonnull Command command,
	                                  final @Nonnull String alias, final String[] args)
	{
		// if more than one argument, use tab completer of subcommand
		if (args.length > 1)
		{
			// get subcommand from map
			Optional<Subcommand> subcommand = subcommandRegistry.getSubcommand(args[0]);

			// if subcommand returned from map, return subcommand tab completer list, else empty list
			return (subcommand.isPresent())
					? subcommand.get().onTabComplete(sender, command, alias, args)
					: List.of();
		}

		// return list of subcommands for which sender has permission
		return subcommandRegistry.matchingNames(sender, args[0]);
	}


	/**
	 * command executor method for HomeStar
	 */
	@Override
	public boolean onCommand(final @Nonnull CommandSender sender, final @Nonnull Command cmd,
	                         final @Nonnull String label, final String[] args)
	{
		// convert args array to list
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		String subcommandName = (!argsList.isEmpty())
				? argsList.removeFirst()
				: "help";

		// get subcommand from map by name
		Optional<Subcommand> optionalSubcommand = subcommandRegistry.getSubcommand(subcommandName);

		// if subcommand is empty, get help command
		if (optionalSubcommand.isEmpty())
		{
			optionalSubcommand = subcommandRegistry.getSubcommand("help");
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_INVALID);
		}

		// execute subcommand
		optionalSubcommand.ifPresent(subcommand -> subcommand.onCommand(sender, argsList));

		return true;
	}

}
