package com.winterhaven_mc.homestar.teleport;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.messages.Message;
import com.winterhaven_mc.homestar.sounds.SoundId;
import com.winterhaven_mc.homestar.messages.MessageId;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

import static com.winterhaven_mc.homestar.messages.MessageId.*;
import static com.winterhaven_mc.homestar.messages.Macro.*;


/**
 * Class that extends BukkitRunnable to teleport a player to their home location
 * after a configured warmup period.
 */
final class DelayedTeleportTask extends BukkitRunnable {

	// reference to main class
	private final PluginMain plugin;

	// player being teleported
	private final Player player;

	// teleport destination
	private final Location destination;

	// teleport destination display name
	private final String destinationName;

	// particle task
	private BukkitTask particleTask;

	// HomeStar item used by player
	private final ItemStack playerItem;


	/**
	 * Class constructor method
	 *
	 * @param player the player to be teleported
	 * @param destination location where player will be teleported
	 * @param destinationName the configured name of the teleport destination
	 * @param playerItem the item used to initiate teleport
	 */
	DelayedTeleportTask(final PluginMain plugin,
						final Player player,
						final Location destination,
						final String destinationName,
						final ItemStack playerItem) {

		// check for null parameters
		this.plugin = Objects.requireNonNull(plugin);
		this.player = Objects.requireNonNull(player);
		this.destination = Objects.requireNonNull(destination);
		this.destinationName = Objects.requireNonNull(destinationName);
		this.playerItem = Objects.requireNonNull(playerItem);

		// start repeating task for generating particles at player location
		if (plugin.getConfig().getBoolean("particle-effects")) {

			// start particle task, with 2 tick delay so it doesn't self cancel on first run
			particleTask = new ParticleTask(plugin, player).runTaskTimer(plugin, 2L, 10);
		}
	}


	@Override
	public final void run() {

		// cancel particles task
		particleTask.cancel();

		// if player is in warmup map
		if (plugin.teleportManager.isWarmingUp(player)) {

			// remove player from warmup map
			plugin.teleportManager.removeWarmup(player);

			// get remove-from-inventory config setting
			String removeFromInventory = plugin.getConfig().getString("remove-from-inventory");

			// check for null
			if (removeFromInventory == null) {
				removeFromInventory = "on-success";
			}

			// if remove-from-inventory is configured on-success, take one spawn star item from inventory now
			if (removeFromInventory.equalsIgnoreCase("on-success")) {

				// try to remove one HomeStar item from player inventory
				boolean notRemoved = true;
				for (ItemStack itemStack : player.getInventory()) {
					if (playerItem.isSimilar(itemStack)) {
						ItemStack removeItem = itemStack.clone();
						removeItem.setAmount(1);
						player.getInventory().removeItem(removeItem);
						notRemoved = false;
						break;
					}
				}

				// if one HomeStar item could not be removed from inventory, send message, set cooldown and return
				if (notRemoved) {
					Message.create(player, MessageId.TELEPORT_CANCELLED_NO_ITEM).send();
					plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED_NO_ITEM);
					plugin.teleportManager.startCooldown(player);
					return;
				}
			}

			// play pre-teleport sound if sound effects are enabled
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);

			// teleport player to destination
			player.teleport(destination);

			// send player respawn message
			Message.create(player, TELEPORT_SUCCESS).setMacro(DESTINATION, destinationName).send();

			// play post-teleport sound if sound effects are enabled
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning")) {
				player.getWorld().strikeLightningEffect(destination);
			}

			// set player cooldown
			plugin.teleportManager.startCooldown(player);
		}
	}

}
