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

package com.winterhavenmc.homestar.teleport;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.sounds.SoundId;

import com.winterhavenmc.homestar.util.HomeStarUtility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


final class HomeTeleporter extends AbstractTeleporter implements Teleporter
{
	private final TeleportExecutor teleportExecutor;


	HomeTeleporter(final PluginMain plugin, final TeleportExecutor teleportExecutor)
	{
		super(plugin);
		this.teleportExecutor = teleportExecutor;
	}


	/**
	 * Begin teleport to players bedspawn destination
	 *
	 * @param player the player to teleport
	 */
	@Override
	public void initiate(final Player player)
	{
		plugin.messageBuilder.constants().getString(HomeStarUtility.HOME_KEY)
				.ifPresent(homeName -> getHomeDestination(player).ifPresentOrElse(
				destination -> execute(player, destination, homeName, player.getInventory().getItemInMainHand()),
				() -> fallbackToSpawn(player)
		));
	}


	@Override
	public void execute(final Player player, final Location location, final String destinationName, final ItemStack playerItem)
	{
		teleportExecutor.execute(player, location, destinationName, playerItem);
	}


	/**
	 * Initiate fallback teleport to spawn if configured
	 *
	 * @param player the player to teleport
	 */
	void fallbackToSpawn(final Player player)
	{
		if (plugin.getConfig().getBoolean("bedspawn-fallback"))
		{
			plugin.messageBuilder.constants().getString(HomeStarUtility.SPAWN_KEY)
					.ifPresent(homeName -> getSpawnDestination(player)
							.ifPresentOrElse(location -> new SpawnTeleporter(plugin, teleportExecutor).initiate(player),
									() -> sendInvalidDestinationMessage(player, homeName)
			));
		}
		else
		{
			plugin.messageBuilder.compose(player, MessageId.TELEPORT_FAIL_NO_BEDSPAWN).send();
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
		}
	}

}
