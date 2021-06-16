package com.winterhaven_mc.homestar;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


/**
 * A simple static API for HomeStar
 *
 * @author Tim Savage
 * @version 1.0
 */
@SuppressWarnings("unused")
public final class SimpleAPI {

	private final static PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);


	/**
	 * Private class constructor to prevent instantiation
	 */
	private SimpleAPI() {
		throw new AssertionError();
	}


	/**
	 * Create a HomeStar item stack of given quantity, with custom display name and lore
	 *
	 * @param quantity number of HomeStar items in newly created stack
	 * @return ItemStack of HomeStar items
	 * @deprecated use HomeStar.create(quantity) method
	 */
	public static ItemStack createItem(final int quantity) {
		return plugin.homeStarFactory.create(quantity);
	}


	/**
	 * Check if itemStack is a HomeStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a HomeStar item, {@code false} if not
	 * @deprecated use HomeStar.isItem(itemStack) method
	 */
	public static boolean isHomeStar(final ItemStack itemStack) {
		return plugin.homeStarFactory.isItem(itemStack);
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static Boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static Boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static Boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}


	/**
	 * Test if player is warming up for pending teleport
	 * @param player the player to check if warming up
	 * @return boolean {@code true} if player is currently warming up, {@code false} if not
	 */
	public static Boolean isWarmingUp(final Player player) {
		return plugin.teleportManager.isWarmingUp(player);
	}


	/**
	 * Test if player is currently cooling down for item use
	 * @param player the player to check if cooling down
	 * @return boolean {@code true} if player is currently cooling down, {@code false} if not
	 */
	public static boolean isCoolingDown(final Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}


	/**
	 * Get item use cooldown time remaining
	 *
	 * @param player the player to check cooldown time remaining
	 * @return remaining time
	 */
	public static long cooldownTimeRemaining(final Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player);
	}


	/**
	 * Get list of worlds in which plugin is enabled
	 * @return List of world names
	 */
	public static List<String> getEnabledWorldNames() {
		return plugin.worldManager.getEnabledWorldNames();
	}


	/**
	 * Cancel player teleport
	 * @param player the player to cancel teleporting
	 */
	public static void cancelTeleport(final Player player) {
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Create an itemStack with default material and data from config
	 *
	 * @return ItemStack
	 * @deprecated use HomeStar.getDefaultItem()
	 */
	public static ItemStack getDefaultItem() {
		return plugin.homeStarFactory.getDefaultItemStack();
	}


	/**
	 * Get configured item display name
	 *
	 * @return String - configured item display name
	 * @deprecated use HomeStar.getItemName()
	 */
	public static String getItemName() {
		return plugin.homeStarFactory.getItemName();
	}


	/**
	 * Set MetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a HomeStar item.
	 *
	 * @param itemStack the ItemStack on which to set HomeStar MetaData
	 * @deprecated use HomeStar.setMetaData()
	 */
	private static void setMetaData(final ItemStack itemStack) {
		plugin.homeStarFactory.setMetaData(itemStack);
	}

}
