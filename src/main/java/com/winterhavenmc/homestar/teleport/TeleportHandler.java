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

import org.bukkit.entity.Player;


/**
 * Class that manages player teleportation, including warmup and cooldown.
 */
public final class TeleportHandler {

	// reference to main class
	private final PluginMain plugin;

	// Map containing player UUID as key and cooldown expire time in milliseconds as value
	private final CooldownMap cooldownMap;

	// Map containing player UUID as key and warmup task id as value
	private final WarmupMap warmupMap;

	// teleport executor instance that serves all teleporters
	private final TeleportExecutor teleportExecutor;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public TeleportHandler(final PluginMain plugin) {
		this.plugin = plugin;
		this.cooldownMap = new CooldownMap(plugin);
		this.warmupMap = new WarmupMap(plugin);
		this.teleportExecutor = new TeleportExecutor(plugin, warmupMap);
	}


	/**
	 * Start the player teleport
	 *
	 * @param player the player being teleported
	 */
	public void initiateTeleport(final Player player) {

		// if player is warming up, do nothing and return
		if (plugin.teleportHandler.isWarmingUp(player)) {
			return;
		}

		// if player cooldown has not expired, send player cooldown message and return
		if (cooldownMap.isCoolingDown(player)) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_COOLDOWN)
					.setMacro(Macro.DURATION, cooldownMap.getCooldownTimeRemaining(player))
					.send();
			return;
		}

		new HomeTeleporter(plugin, teleportExecutor).initiate(player);
	}


	/**
	 * Cancel pending player teleport
	 *
	 * @param player the player whose teleport will be cancelled
	 */
	public void cancelTeleport(final Player player) {

		// check for null parameter
		if (player == null) {
			return;
		}

		// if player is in warmup hashmap, cancel delayed teleport task and remove player from warmup hashmap
		if (isWarmingUp(player)) {

			// get delayed teleport task id
			int taskId = warmupMap.getTaskId(player);

			// cancel delayed teleport task
			plugin.getServer().getScheduler().cancelTask(taskId);

			// remove player from warmup hashmap
			removeWarmingUpPlayer(player);
		}
	}


	/**
	 * Insert player into cooldown map
	 *
	 * @param player the player being inserted into the cooldown map
	 */
	void startPlayerCooldown(final Player player) {
		cooldownMap.startPlayerCooldown(player);
	}


	/**
	 * Get time remaining for player cooldown
	 *
	 * @param player the player whose cooldown time remaining to retrieve
	 * @return long remainingTime
	 */
	public long getCooldownTimeRemaining(final Player player) {
		return cooldownMap.getCooldownTimeRemaining(player);
	}


	/**
	 * Check if player is in teleport initiated set. Public pass through method.
	 *
	 * @param player the player to check if teleport is initiated
	 * @return {@code true} if teleport been initiated, {@code false} if it has not
	 */
	public boolean isInitiated(final Player player) {
		return warmupMap.isInitiated(player);
	}


	/**
	 * Test if player uuid is in warmup hashmap. Public pass through method.
	 *
	 * @param player the player to test if in warmup map
	 * @return {@code true} if player is in warmup map, {@code false} if not
	 */
	public boolean isWarmingUp(final Player player) {
		return warmupMap.isWarmingUp(player);
	}


	/**
	 * Remove player uuid from warmup hashmap. Public pass through method.
	 *
	 * @param player the player to remove from the warmup map
	 */
	public void removeWarmingUpPlayer(final Player player) {
		warmupMap.removePlayer(player);
	}

}
