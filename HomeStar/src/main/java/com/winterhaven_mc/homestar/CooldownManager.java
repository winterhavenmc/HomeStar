package com.winterhaven_mc.homestar;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Implements cooldown tasks for <code>HomeStar</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
class CooldownManager {
	
	// reference to main class
	private final PluginMain plugin;
	
	// hashmap to store player uuids and cooldown expire times
	private ConcurrentHashMap<UUID, Long> cooldown;

	
	/**
	 * constructor method for <code>CooldownManager</code> class
	 * 
	 * @param	plugin		A reference to this plugin's main class
	 */
	CooldownManager(final PluginMain plugin) {
		this.plugin = plugin;
		cooldown = new ConcurrentHashMap<UUID, Long>();
	}

	
	/**
	 * Insert player uuid into cooldown hashmap with <code>expiretime</code> as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 * @param player
	 */
	void setPlayerCooldown(final Player player) {

		// get cooldown time in seconds from config
		int cooldownSeconds = plugin.getConfig().getInt("teleport-cooldown");

		Long expiretime = System.currentTimeMillis() + (cooldownSeconds * 1000);
		cooldown.put(player.getUniqueId(), expiretime);
		new BukkitRunnable(){

			public void run() {
				cooldown.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, (cooldownSeconds * 20));
	}
	
	
	/**
	 * Get time remaining for player cooldown
	 * @param player
	 * @return long remainingtime
	 */
	long getTimeRemaining(final Player player) {
		long remainingTime = 0;
		if (cooldown.containsKey(player.getUniqueId())) {
			remainingTime = (cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
		}
		return remainingTime;
	}
	
}

