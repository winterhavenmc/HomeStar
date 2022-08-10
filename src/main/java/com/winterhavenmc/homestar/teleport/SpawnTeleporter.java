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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


final class SpawnTeleporter extends AbstractTeleporter implements Teleporter {

	private final TeleportExecutor teleportExecutor;


	SpawnTeleporter(final PluginMain plugin, final TeleportExecutor teleportExecutor) {
		super(plugin);
		this.teleportExecutor = teleportExecutor;
	}


	/**
	 * Begin teleport to world spawn destination
	 *
	 * @param player the player to teleport
	 */
	@Override
	public void initiate(final Player player) {
		getSpawnDestination(player).ifPresentOrElse(
				location -> execute(player, location, plugin.messageBuilder.getSpawnDisplayName().orElse("Spawn"), player.getInventory().getItemInMainHand()),
				() -> sendInvalidDestinationMessage(player, plugin.messageBuilder.getSpawnDisplayName().orElse("Spawn"))
		);
	}


	@Override
		public void execute(final Player player, final Location location, final String destinationName, final ItemStack playerItem) {

		Location finalDestination = location;

		// if from-nether is enabled in config and player is in nether, try to get overworld spawn location
		if (plugin.getConfig().getBoolean("from-nether") && isInNetherWorld(player)) {
			finalDestination = getOverworldSpawnLocation(player).orElse(finalDestination);
		}

		// if from-end is enabled in config and player is in end, try to get overworld spawn location
		else if (plugin.getConfig().getBoolean("from-end") && isInEndWorld(player)) {
			finalDestination = getOverworldSpawnLocation(player).orElse(finalDestination);
		}

		teleportExecutor.execute(player, finalDestination, destinationName, playerItem);
	}


}
