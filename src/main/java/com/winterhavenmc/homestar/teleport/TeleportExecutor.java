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

import com.winterhavenmc.homestar.messages.Macro;
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.sounds.SoundId;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import static com.winterhavenmc.util.TimeUnit.SECONDS;


class TeleportExecutor {

	protected final PluginMain plugin;
	protected final WarmupMap warmupMap;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class
	 * @param warmupMap player warmup map
	 */
	TeleportExecutor(final PluginMain plugin, final WarmupMap warmupMap) {
		this.plugin = plugin;
		this.warmupMap = warmupMap;
	}


	/**
	 * Execute the teleport to destination
	 *
	 * @param player      the player to teleport
	 * @param location    the destination location
	 * @param destinationName the destination name
	 * @param playerItem  the LodeStar item used to initiate teleport
	 */
	void execute(final Player player, final Location location, final String destinationName, final ItemStack playerItem) {

		// if destination location is null, send invalid destination message and return
		if (location == null) {
			plugin.messageBuilder.compose(player, MessageId.TELEPORT_FAIL_NO_BEDSPAWN)
					.setMacro(Macro.DESTINATION, destinationName)
					.send();
			return;
		}

		// if player is less than configured minimum distance from destination, send player proximity message and return
		if (isUnderMinimumDistance(player, location)) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_MIN_DISTANCE)
					.setMacro(Macro.DESTINATION, destinationName)
					.send();
			return;
		}

		// if remove-from-inventory is configured on-use, take one LodeStar item from inventory now
		removeFromInventoryOnUse(player, playerItem);

		// initiate delayed teleport for player to final destination
		BukkitTask teleportTask = new DelayedTeleportTask(plugin, player, location, destinationName, playerItem.clone())
				.runTaskLater(plugin, SECONDS.toTicks(plugin.getConfig().getLong("teleport-warmup")));

		// if configured warmup time is greater than zero, send warmup message
		sendWarmupMessage(player, destinationName);

		// insert player and taskId into warmup hashmap
		warmupMap.startPlayerWarmUp(player, teleportTask.getTaskId());

		// load destination chunk if not already loaded
		loadDestinationChunk(location);

		// if log-use is enabled in config, write log entry
		logUsage(player);
	}


	/**
	 * Send teleport warmup message if warmup time is greater than zero
	 *
	 * @param player the teleporting player
	 * @param destinationName string containing the destination name
	 */
	private void sendWarmupMessage(final Player player, final String destinationName) {

		// get configured warmup time
		long warmupTime = plugin.getConfig().getLong("teleport-warmup");

		// if warmup time is greater than zero, send player warmup message
		if (warmupTime > 0) {
			plugin.messageBuilder.compose(player, MessageId.TELEPORT_WARMUP)
					.setMacro(Macro.DESTINATION, destinationName)
					.setMacro(Macro.DURATION, SECONDS.toMillis(warmupTime))
					.send();

			// if enabled, play teleport warmup sound effect
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_WARMUP);
		}
	}


	/**
	 * Preload chunk at teleport destination if not already loaded
	 *
	 * @param location the destination location
	 */
	private void loadDestinationChunk(final Location location) {

		if (location != null && location.getWorld() != null) {
			if (!location.getWorld().getChunkAt(location).isLoaded()) {
				location.getWorld().getChunkAt(location).load();
			}
		}
	}


	/**
	 * Check if player is within configured minimum distance from destination location
	 *
	 * @param player   the player
	 * @param location the destination location
	 * @return true if under minimum distance, false if not
	 */
	private boolean isUnderMinimumDistance(final Player player, final Location location) {
		return location != null
				&& location.getWorld() != null
				&& player.getWorld().equals(location.getWorld())
				&& player.getLocation().distanceSquared(location) < Math.pow(plugin.getConfig().getInt("minimum-distance"), 2);
	}


	/**
	 * Log player usage of homestar item
	 *
	 * @param player the player being logged
	 */
	private void logUsage(final Player player) {

		// if log-use is enabled in config, write log entry
		if (plugin.getConfig().getBoolean("log-use")) {

			// send message to console
			plugin.messageBuilder.compose(plugin.getServer().getConsoleSender(), MessageId.LOG_USAGE)
					.setMacro(Macro.TARGET_PLAYER, player)
					.send();
		}
	}


	/**
	 * remove one lode star item from player inventory
	 * @param player     the player
	 * @param playerItem the item
	 */
	final void removeFromInventoryOnUse(final Player player, final ItemStack playerItem) {
		// if remove-from-inventory is configured on-use, take one LodeStar item from inventory now
		String removeItem = plugin.getConfig().getString("remove-from-inventory");
		if (removeItem != null && removeItem.equalsIgnoreCase("on-use")) {
			playerItem.setAmount(playerItem.getAmount() - 1);
			player.getInventory().setItemInMainHand(playerItem);
		}
	}

}
