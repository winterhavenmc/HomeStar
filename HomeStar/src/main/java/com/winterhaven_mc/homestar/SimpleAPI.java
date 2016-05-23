package com.winterhaven_mc.homestar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple static API for HomeStar
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public final class SimpleAPI {

	private final static PluginMain plugin = PluginMain.instance;
	private final static String itemTag = hiddenString("HomeStarV1");

	
	/**
	 * Private class constructor to prevent instantiation
	 * @param plugin
	 */
	private SimpleAPI(PluginMain plugin) {
		throw new AssertionError();
	}

	
	/**
	 * Create a HomeStar item stack of given quantity, with custom display name and lore
	 * @param quantity
	 * @return
	 */
	public final static ItemStack createItem(int quantity) {

		// validate quantity
		quantity = Math.max(quantity, 1);

		// create item stack with configured material and data
		final ItemStack newItem = getDefaultItem();

		// set quantity
		newItem.setAmount(quantity);
		
		// set item display name and lore
		setMetaData(newItem);
		
		// return new item
		return newItem;
	}
	
	
	/**
	 * Check if itemStack is a HomeStar item
	 * @param itemStack
	 * @return boolean
	 */
	public final static boolean isHomeStar(ItemStack itemStack) {
		
		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return false;
		}
				
		// if item stack does not have display name return false
		if (! itemStack.getItemMeta().hasDisplayName()) {
			return false;
		}
		
		// get item display name
		String itemDisplayName = itemStack.getItemMeta().getDisplayName();
		
		// check that lore contains hidden token
		if (! itemDisplayName.isEmpty() && itemDisplayName.startsWith(itemTag)) {
			return true;
		}
		return false;
	}
	
	public final static Boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}
	
	public final static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}

	public final static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	public final static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	public final static Boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	public final static Boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	public final static Boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}
	
	public final static Boolean isWarmingUp(Player player) {
		return plugin.teleportManager.isWarmingUp(player);
	}
	
	public final static Boolean isCoolingDown(Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}
	
	public final static long cooldownTimeRemaining(Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player);
	}
	
	public final static List<String> getEnabledWorldNames() {
		return plugin.worldManager.getEnabledWorldNames();
	}
	
	public final static void cancelTeleport(Player player) {
		plugin.teleportManager.cancelTeleport(player);
	}

	
	/**
	 * Create an itemStack with default material and data from config
	 * @return ItemStack
	 */
	public final static ItemStack getDefaultItem() {
		
		// get material type and data from config file
		String materialString = plugin.getConfig().getString("item-material");
		byte configMaterialDataByte = 0;

		// if material string is null or empty, set to NETHER_STAR
		if (materialString == null || materialString.isEmpty()) {
			materialString = "NETHER_STAR";
		}
		
		// split material and data into elements
		String[] configMaterialElements = plugin.getConfig().getString("item-material").split("\\s*:\\s*");
		
		// try to match material
		Material configMaterial = Material.matchMaterial(configMaterialElements[0]);
		
		// if no match default to nether star
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}
		else {
			// if data set in config try to parse as byte; set to zero if it doesn't parse
			if (configMaterialElements.length > 1) {
				try {
					configMaterialDataByte = Byte.parseByte(configMaterialElements[1]);
				}
				catch (NumberFormatException e) {
					configMaterialDataByte = (byte) 0;
				}
			}
			// if no data set in config default to zero
			else {
				configMaterialDataByte = (byte) 0;
			}
		}
		
		// create item stack with configured material and data
		final ItemStack newItem = new ItemStack(configMaterial,1,configMaterialDataByte);
		
		return newItem;
	}

	
	public final static String getItemName() {
		return plugin.messageManager.getItemName();
	}
	
	
	public final static Location getBlockCenteredLocation(final Location location) {
		
		// if location is null, return null
		if (location == null) {
			return null;
		}
		
		final World world = location.getWorld();
		int x = location.getBlockX();
		int y = (int)Math.round(location.getY());
		int z = location.getBlockZ();
		return new Location(world, x + 0.5, y, z + 0.5, location.getYaw(), location.getPitch());
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a HomeStar item.
	 * @param itemStack
	 */
	private final static void setMetaData(ItemStack itemStack) {
		
		// retrieve item name and lore from language file file
		String displayName = plugin.messageManager.getItemName();
		List<String> configLore = plugin.messageManager.getItemLore();
	
		// allow for '&' character for color codes in name and lore
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
	
		ArrayList<String> coloredLore = new ArrayList<String>();
		
		for (String line : configLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		// get item metadata object
		final ItemMeta itemMeta = itemStack.getItemMeta();
		
		// set item metadata display name to value from config file
		itemMeta.setDisplayName(itemTag + displayName);
		
		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);
		
		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}


	private static String hiddenString(String s) {
		String hidden = "";
		for (char c : s.toCharArray())
			hidden += ChatColor.COLOR_CHAR + "" + c;
		return hidden;
	}

}

