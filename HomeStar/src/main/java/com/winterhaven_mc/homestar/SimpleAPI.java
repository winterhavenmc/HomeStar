package com.winterhaven_mc.homestar;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple static API for HomeStar
 *
 * @author Tim Savage
 * @version 1.0
 */
@SuppressWarnings("unused")
public final class SimpleAPI {

	private final static PluginMain plugin = PluginMain.instance;
	private final static NamespacedKey itemKey = new NamespacedKey(plugin, "isItem");


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

		// create item stack with configured material and data
		final ItemStack newItem = getDefaultItem();

		// validate min,max quantity
		int newQuantity = Math.max(quantity, 1);
		newQuantity = Math.min(newQuantity, newItem.getMaxStackSize());

		// set quantity
		newItem.setAmount(newQuantity);

		// set item display name and lore
		setMetaData(newItem);

		// return new item
		return newItem;
	}


	/**
	 * Check if itemStack is a HomeStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a HomeStar item, {@code false} if not
	 */
	public static boolean isHomeStar(final ItemStack itemStack) {

		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return false;
		}

		// if item stack does not have metadata return false
		if (!itemStack.hasItemMeta()) {
			return false;
		}

		// if item stack does not have persistent data tag, return false
		//noinspection ConstantConditions
		return itemStack.getItemMeta().getPersistentDataContainer().has(itemKey, PersistentDataType.BYTE);

	}

	public static Boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}

	public static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}

	public static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	public static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	public static Boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	public static Boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	public static Boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}

	public static Boolean isWarmingUp(final Player player) {
		return plugin.teleportManager.isWarmingUp(player);
	}

	public static Boolean isCoolingDown(final Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}

	public static long cooldownTimeRemaining(final Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player);
	}

	public static List<String> getEnabledWorldNames() {
		return plugin.worldManager.getEnabledWorldNames();
	}

	public static void cancelTeleport(final Player player) {
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Create an itemStack with default material and data from config
	 *
	 * @return ItemStack
	 */
	@SuppressWarnings("WeakerAccess")
	public static ItemStack getDefaultItem() {

		// try to match material
		Material configMaterial = Material.matchMaterial(
				Objects.requireNonNull(plugin.getConfig().getString("item-material")));

		// if no match default to nether star
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}

		// return item stack with configured material and data
		return new ItemStack(configMaterial, 1);
	}


	public static String getItemName() {
		return plugin.messageManager.getItemName();
	}


	public static Location getBlockCenteredLocation(final Location location) {

		// if location is null, return null
		if (location == null) {
			return null;
		}

		final World world = location.getWorld();
		int x = location.getBlockX();
		int y = (int) Math.round(location.getY());
		int z = location.getBlockZ();
		return new Location(world, x + 0.5, y, z + 0.5, location.getYaw(), location.getPitch());
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a HomeStar item.
	 *
	 * @param itemStack the ItemStack on which to set HomeStar MetaData
	 */
	private static void setMetaData(final ItemStack itemStack) {

		// retrieve item name and lore from language file file
		String displayName = plugin.messageManager.getItemName();
		List<String> configLore = plugin.messageManager.getItemLore();

		// allow for '&' character for color codes in name and lore
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);

		ArrayList<String> coloredLore = new ArrayList<>();

		for (String line : configLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}

		// get item metadata object
		final ItemMeta itemMeta = itemStack.getItemMeta();

		// set item metadata display name to value from config file
		//noinspection ConstantConditions
		itemMeta.setDisplayName(ChatColor.RESET + displayName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// set persistent data in item metadata
		itemMeta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}

}
