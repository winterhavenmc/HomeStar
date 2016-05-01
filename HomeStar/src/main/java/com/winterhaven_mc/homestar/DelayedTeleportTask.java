package com.winterhaven_mc.homestar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

class DelayedTeleportTask extends BukkitRunnable {

	private final PluginMain plugin;
	Player player;
	Location destination;
	String destinationName;
	BukkitTask particleTask;
	ItemStack playerItem;

	/**
	 * Class constructor method
	 */
	DelayedTeleportTask(final Player player, final Location destination,
			final String destinationName, final ItemStack playerItem) {
		
		this.plugin = PluginMain.instance;
		this.player = player;
		this.playerItem = playerItem;
		this.destination = destination;
		this.destinationName = destinationName;
		
		// start repeating task for generating particles at player location
		if (plugin.getConfig().getBoolean("particle-effects")) {

			// start particle task, with 2 tick delay so it doesn't self cancel on first run
			particleTask = new ParticleTask(player).runTaskTimer(plugin, 2L, 10);
		
		}
	}

	@Override
	public void run() {

		// cancel particles task
		particleTask.cancel();
		
		// if player is in warmup hashmap
		if (plugin.warmupManager.isWarmingUp(player)) {

			// remove player from warmup hashmap
			plugin.warmupManager.removePlayer(player);
		
			// if multiverse is not enabled, copy pitch and yaw from player
			if (!plugin.mvEnabled) {
				destination.setPitch(player.getLocation().getPitch());
				destination.setYaw(player.getLocation().getYaw());
			}
			
			// if remove-from-inventory is configured on-success, take one spawn star item from inventory now
			if (plugin.getConfig().getString("remove-from-inventory").equalsIgnoreCase("on-success")) {
				
				// try to remove one spawn star item from player inventory
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
				
				// if one LodeStar item could not be removed from inventory, send message, set cooldown and return
				if (notRemoved) {
					plugin.messageManager.sendPlayerMessage(player, "teleport-cancelled-no-item");
					plugin.messageManager.playerSound(player, "teleport-cancelled-no-item");
					plugin.cooldownManager.setPlayerCooldown(player);
					return;
				}
			}

			// play pre-teleport sound if sound effects are enabled
			plugin.messageManager.playerSound(player, "teleport-success-departure");

			// teleport player to bed spawn location
			player.teleport(destination);

			// send player respawn message
			plugin.messageManager.sendPlayerMessage(player, "teleport-success", destinationName);

			// play post-teleport sound if sound effects are enabled
			plugin.messageManager.playerSound(player, "teleport-success-arrival");

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning")) {
				player.getWorld().strikeLightningEffect(destination);
			}
			
			// set player cooldown
			plugin.cooldownManager.setPlayerCooldown(player);
		}
	}
	
}
