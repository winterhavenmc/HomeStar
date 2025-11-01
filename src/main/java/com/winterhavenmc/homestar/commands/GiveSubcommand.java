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
import com.winterhavenmc.homestar.messages.Macro;
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


final class GiveSubcommand extends AbstractSubcommand
{
	private final PluginMain plugin;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to plugin main class instance
	 */
	GiveSubcommand(final PluginMain plugin)
	{
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "give";
		this.permissionNode = "homestar.give";
		this.usageString = "/homestar give <player> [quantity]";
		this.description = MessageId.COMMAND_HELP_GIVE;
		this.minArgs = 1;
		this.maxArgs = 2;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
	                                  final String alias, final String[] args)
	{
		return switch (args.length)
		{
			case 2 -> null; // return null for list of matching online players
			case 3 -> List.of("1", "2", "3", "5", "10"); // return some useful quantities
			default -> List.of(); // return empty list
		};
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if command sender does not have permission to give HomeStars, output error message and return true
		if (!sender.hasPermission(permissionNode))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_PERMISSION).send();
			return true;
		}

		// check min arguments
		if (args.size() < getMinArgs())
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			return true;
		}

		// get passed player name
		String targetPlayerName = args.getFirst();

		// try to match target player name to currently online player
		Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

		// if no match, send player not found message and return
		if (targetPlayer == null)
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_PLAYER_NOT_FOUND).send();
			return true;
		}

		int quantity = 1;

		if (args.size() > 1)
		{
			try
			{
				quantity = Integer.parseInt(args.get(1));
			}
			catch (NumberFormatException exception)
			{
				plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_QUANTITY_INVALID).send();
				return true;
			}
		}

		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = plugin.getConfig().getInt("max-give-amount");
		if (maxQuantity < 0)
		{
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		// add specified quantity of homestar(s) to player inventory
		ItemStack item = plugin.homeStarUtility.create(quantity);

		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(item);

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet())
		{
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity)
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_INVENTORY_FULL).send();
			return true;
		}

		// subtract noFitCount from quantity
		quantity = quantity - noFitCount;


		if (sender.getName().equals(targetPlayer.getName()))
		{
			// send message when giving to self
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_GIVE_SELF)
					.setMacro(Macro.QUANTITY, quantity)
					.setMacro(Macro.ITEM, item)
					.send();
		}
		else
		{
			// send message and play sound to giver
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_GIVE)
					.setMacro(Macro.PLAYER, targetPlayer)
					.setMacro(Macro.QUANTITY, quantity)
					.setMacro(Macro.ITEM, item)
					.send();

			// if giver is in game, play sound
			if (sender instanceof Player)
			{
				plugin.messageBuilder.sounds().play(sender, SoundId.COMMAND_SUCCESS_GIVE_SENDER);
			}

			// send message to target player
			plugin.messageBuilder.compose(targetPlayer, MessageId.COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(Macro.PLAYER, sender)
					.setMacro(Macro.QUANTITY, quantity)
					.setMacro(Macro.ITEM, item)
					.send();
		}

		// play sound to target player
		plugin.messageBuilder.sounds().play(targetPlayer, SoundId.COMMAND_SUCCESS_GIVE_TARGET);
		return true;
	}

}
