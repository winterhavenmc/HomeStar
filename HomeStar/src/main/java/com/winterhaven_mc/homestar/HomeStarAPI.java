package com.winterhaven_mc.homestar;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Public API to access SpawnStar attributes 
 * @author Tim Savage
 *
 */
public interface HomeStarAPI {

	/**
	 * Create an itemStack with default material and data from config
	 * @return ItemStack
	 */
	ItemStack getDefaultItem();
	
	/**
	 * Get display name of SpawnStar with formatting intact
	 * @return String
	 */
	String getItemName();
	
	/**
	 * check if SpawnStar items are allowed in crafting recipes
	 * @return Boolean
	 */
	Boolean isValidIngredient();
	
	/**
	 * SpawnStar teleport cooldown time in seconds
	 * @return int
	 */
	int getCooldownTime();
	
	/**
	 * SpawnStar teleport warmup time in seconds
	 * @return int
	 */
	int getWarmupTime();
	
	/**
	 * Minimum distance from spawn to use a SpawnStar item
	 * @return int
	 */
	int getMinSpawnDistance();
	
	/**
	 * Is teleport cancellation on player damage during warmup configured
	 * @return Boolean
	 */
	Boolean isCancelledOnDamage();

	/**
	 * Is teleport cancellation on player movement during warmup configured
	 * @return Boolean
	 */
	Boolean isCancelledOnMovement();
	
	/**
	 * Is teleport cancellation on player interaction with blocks during warmup configured
	 * @return Boolean
	 */
	Boolean isCancelledOnInteraction();

	/**
	 * Is player teleportation pending
	 * @param player
	 * @return Boolean
	 */
	Boolean isWarmingUp(Player player);

	/**
	 * Is player teleport cooldown in effect for this player
	 * @param player
	 * @return Boolean
	 */
	Boolean isCoolingDown(Player player);

	/**
	 * Time remaining for player cooldown in seconds for this player
	 * @param player
	 * @return long
	 */
	long cooldownTimeRemaining(final Player player);
	
	/**
	 * Get list of worlds in which the SpawnStar plugin is enabled
	 * @return List of Strings
	 */
	List<String> getEnabledWorlds();
	
	/**
	 * Cancel pending teleport for this player
	 * @param player
	 */
	void cancelTeleport(Player player);

}
