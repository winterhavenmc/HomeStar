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
import com.winterhavenmc.library.messagebuilder.ItemForge;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;


final class DestroySubcommand extends AbstractSubcommand
{

	private final PluginMain plugin;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to plugin main class instance
	 */
	DestroySubcommand(final PluginMain plugin)
	{
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "destroy";
		this.permissionNode = "homestar.destroy";
		this.usageString = "/homestar destroy";
		this.description = MessageId.COMMAND_HELP_DESTROY;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// sender must be in game player
		if (!(sender instanceof Player player))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_CONSOLE).send();
			return true;
		}

		// if command sender does not have permission to destroy HomeStars, output error message and return true
		if (!sender.hasPermission(permissionNode))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player is holding a homestar stack
		if (!ItemForge.isCustomItem(playerItem))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		ItemStack originalItem = playerItem.clone();
		playerItem.setAmount(0);
		player.getInventory().setItemInMainHand(playerItem);

		plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_DESTROY)
				.setMacro(Macro.ITEM, originalItem)
				.send();
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_DESTROY);
		return true;
	}

}
