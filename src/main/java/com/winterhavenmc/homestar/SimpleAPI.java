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

package com.winterhavenmc.homestar;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
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
	 */
	public static ItemStack createItem(final int quantity) {
		return plugin.homeStarFactory.create(quantity);
	}


	/**
	 * Check if itemStack is a HomeStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a HomeStar item, {@code false} if not
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
	public static boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	/**
	 * Get configuration setting
	 *
	 * @return boolean configuration setting
	 * @deprecated configuration settings can be accessed through plugin manager
	 */
	public static boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}


	/**
	 * Test if player is warming up for pending teleport
	 *
	 * @param player the player to check if warming up
	 * @return boolean {@code true} if player is currently warming up, {@code false} if not
	 */
	public static boolean isWarmingUp(final Player player) {
		return plugin.teleportHandler.isWarmingUp(player);
	}


	/**
	 * Test if player is currently cooling down for item use
	 *
	 * @param player the player to check if cooling down
	 * @return boolean {@code true} if player is currently cooling down, {@code false} if not
	 */
	public static boolean isCoolingDown(final Player player) {
		return plugin.teleportHandler.getCooldownTimeRemaining(player) > 0;
	}


	/**
	 * Get item use cooldown time remaining
	 *
	 * @param player the player to check cooldown time remaining
	 * @return remaining time
	 */
	public static long cooldownTimeRemaining(final Player player) {
		return plugin.teleportHandler.getCooldownTimeRemaining(player);
	}


	/**
	 * Get list of world name strings in which plugin is enabled
	 *
	 * @return List of world names
	 */
	public static List<String> getEnabledWorldNames() {
		return getEnabledWorldNamesList();
	}


	/**
	 * Get list of world name strings in which plugin is enabled
	 *
	 * @return List of world names
	 */
	public static List<String> getEnabledWorldNamesList() {
		return new ArrayList<>(plugin.worldManager.getEnabledWorldNames());
	}


	/**
	 * Get collection of world name strings in which plugin is enabled
	 *
	 * @return List of world names
	 */
	public static Collection<String> getEnabledWorldNamesCollection() {
		return plugin.worldManager.getEnabledWorldNames();
	}


	/**
	 * Cancel player teleport
	 *
	 * @param player the player to cancel teleporting
	 */
	public static void cancelTeleport(final Player player) {
		plugin.teleportHandler.cancelTeleport(player);
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
		return plugin.messageBuilder.getItemName().orElse("HomeStar");
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
