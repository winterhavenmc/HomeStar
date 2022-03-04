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
import com.winterhavenmc.homestar.sounds.SoundId;

import com.winterhavenmc.homestar.messages.Macro;
import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.winterhavenmc.homestar.util.BukkitTime.SECONDS;


/**
 * Class that manages player teleportation, including warmup and cooldown.
 */
public final class TeleportManager {

	// reference to main class
	private final PluginMain plugin;

	// Map containing player UUID as key and cooldown expire time in milliseconds as value
	private final Map<UUID, Long> cooldownMap;

	// Map containing player UUID as key and warmup task id as value
	private final Map<UUID, Integer> warmupMap;

	// Map containing player uuid for teleport initiated
	private final Set<UUID> teleportInitiated;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public TeleportManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// initialize cooldown map
		cooldownMap = new ConcurrentHashMap<>();

		// initialize warmup map
		warmupMap = new ConcurrentHashMap<>();

		// initialize teleport initiated set
		teleportInitiated = ConcurrentHashMap.newKeySet();
	}


	/**
	 * Start the player teleport
	 *
	 * @param player the player being teleported
	 */
	public void initiateTeleport(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get item in player hand
		final ItemStack playerItem = player.getInventory().getItemInMainHand();

		// if player cooldown has not expired, send player cooldown message and return
		if (getCooldownTimeRemaining(player) > 0) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_COOLDOWN)
					.setMacro(Macro.DURATION, getCooldownTimeRemaining(player))
					.send();
			return;
		}

		// if player is warming up, do nothing and return
		if (plugin.teleportManager.isWarmingUp(player)) {
			return;
		}

		// get player world
		World playerWorld = player.getWorld();

		// get home display name from language file
		String destinationName = plugin.messageBuilder.getHomeDisplayName();

		// if player has bukkit bed spawn, try to get safe bed spawn location
		Location destination = player.getBedSpawnLocation();

		// if center-on-block is configured true, get block centered location
		if (plugin.getConfig().getBoolean("center-on-block")) {
			//noinspection ConstantConditions
			destination.add( 0.5, 0.0, 0.5);
		}

		// if bed spawn location is null, check if bed spawn-fallback is configured true
		if (destination == null) {

			// send missing or obstructed message
			plugin.messageBuilder.build(player, MessageId.TELEPORT_FAIL_NO_BEDSPAWN).send();

			// if bedspawn-fallback is configured false, play teleport fail sound and return
			if (!plugin.getConfig().getBoolean("bedspawn-fallback")) {
				plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
				return;
			}
			// else set destination to spawn location
			else {

				// set destinationName string to spawn name from language file
				destinationName = plugin.messageBuilder.getSpawnDisplayName();

				// if multiverse is enabled, get spawn location from it, so we have pitch and yaw
				destination = plugin.worldManager.getSpawnLocation(playerWorld);
			}
		}

		// if player is less than config min-distance from destination, send player min-distance message and return
		if (player.getWorld().equals(destination.getWorld())
				&& destination.distance(player.getLocation()) < plugin.getConfig().getInt("minimum-distance")) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_MIN_DISTANCE).setMacro(Macro.DESTINATION, destinationName).send();
			return;
		}

		// get remove-from-inventory config setting
		String removeFromInventory = plugin.getConfig().getString("remove-from-inventory");

		// check for null
		if (removeFromInventory == null) {
			removeFromInventory = "on-success";
		}

		// if remove-from-inventory is configured on-use, take one spawn star item from inventory now
		if (removeFromInventory.equalsIgnoreCase("on-use")) {
			playerItem.setAmount(playerItem.getAmount() - 1);
			player.getInventory().setItemInMainHand(playerItem);
		}

		// get teleport warm-up time in seconds from config
		long warmupSeconds = plugin.getConfig().getLong("teleport-warmup");

		// if warmup setting is greater than zero, send warmup message
		if (warmupSeconds > 0) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_WARMUP)
					.setMacro(Macro.DESTINATION, destinationName)
					.setMacro(Macro.DURATION, SECONDS.toMillis(warmupSeconds))
					.send();

			// if enabled, play sound effect
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_WARMUP);
		}

		// initiate delayed teleport for player to destination
		BukkitTask teleportTask = new DelayedTeleportTask(plugin, player, destination, destinationName,	playerItem)
				.runTaskLater(plugin, SECONDS.toTicks(warmupSeconds));

		// insert player and taskId into warmup hashmap
		putWarmup(player, teleportTask.getTaskId());

		// write log entry if configured
		logUsage(player);
	}


	/**
	 * Insert player uuid and taskId into warmup hashmap.
	 *
	 * @param player the player whose uuid will be used as the key in the warmup map
	 * @param taskId the warmup task Id to be placed in the warmup map
	 */
	void putWarmup(final Player player, final int taskId) {

		// check for null parameter
		Objects.requireNonNull(player);

		warmupMap.put(player.getUniqueId(), taskId);

		// insert player uuid into teleport initiated set
		teleportInitiated.add(player.getUniqueId());

		// create task to remove player uuid from tpi set after set amount of ticks (default: 2)
		new BukkitRunnable() {
			@Override
			public void run() {
				teleportInitiated.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, plugin.getConfig().getInt("interact-delay", 2));

	}


	/**
	 * Remove player uuid from warmup hashmap
	 *
	 * @param player the player whose uuid will be removed from the warmup map
	 */
	void removeWarmup(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// remove player uuid from warmup map
		warmupMap.remove(player.getUniqueId());
	}


	/**
	 * Test if player uuid is in warmup hashmap
	 *
	 * @param player the player whose uuid is to be checked for existence in the warmup map
	 * @return {@code true} if player uuid is in the warmup map, {@code false} if it is not
	 */
	public boolean isWarmingUp(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		return warmupMap.containsKey(player.getUniqueId());
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
			Integer taskId = warmupMap.get(player.getUniqueId());

			// cancel delayed teleport task
			if (taskId != null) {
				plugin.getServer().getScheduler().cancelTask(taskId);
			}

			// remove player from warmup hashmap
			removeWarmup(player);
		}
	}


	/**
	 * Insert player uuid into cooldown hashmap with expireTime as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 *
	 * @param player the player whose uuid will be added to the cooldown map
	 */
	void startCooldown(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get cooldown time in seconds from config
		final int cooldownSeconds = plugin.getConfig().getInt("teleport-cooldown");

		// set expireTime to current time + configured cooldown period, in milliseconds
		final Long expireTime = System.currentTimeMillis() + SECONDS.toMillis(cooldownSeconds);

		// put in cooldown map with player UUID as key and expireTime as value
		cooldownMap.put(player.getUniqueId(), expireTime);

		// schedule task to remove player from cooldown map
		new BukkitRunnable() {
			public void run() {
				cooldownMap.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, SECONDS.toTicks(cooldownSeconds));
	}


	/**
	 * Get time remaining for player cooldown
	 *
	 * @param player the player whose cooldown time remaining to retrieve
	 * @return long remainingTime
	 */
	public long getCooldownTimeRemaining(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// initialize remainingTime
		long remainingTime = 0;

		// if player is in cooldown map, set remainingTime to map value
		if (cooldownMap.containsKey(player.getUniqueId())) {
			remainingTime = (cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis());
		}
		return remainingTime;
	}


	/**
	 * Check if player is in teleport initiated set
	 *
	 * @param player the player to check if teleport is initiated
	 * @return {@code true} if teleport been initiated, {@code false} if it has not
	 */
	public boolean isInitiated(final Player player) {

		// check for null parameter
		if (player == null) {
			return false;
		}

		return !teleportInitiated.contains(player.getUniqueId());
	}


	/**
	 * Log player usage of homestar item
	 *
	 * @param player the player being logged
	 */
	private void logUsage(final Player player) {
		// if log-use is enabled in config, write log entry
		if (plugin.getConfig().getBoolean("log-use")) {

			// write message to log
			plugin.getLogger().info(player.getName() + ChatColor.RESET + " used a "
					+ plugin.messageBuilder.getItemName() + ChatColor.RESET + " in "
					+ plugin.worldManager.getWorldName(player) + ChatColor.RESET + ".");
		}
	}

}
