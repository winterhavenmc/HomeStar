package com.winterhaven_mc.homestar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * Implements SpawnStarAPI.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class HomeStarUtilities implements HomeStarAPI {

	private final PluginMain plugin;
	private final String itemTag = hiddenString("HomeStarV1");
	
	
	/**
	 * Class constructor
	 * @param plugin
	 */
	HomeStarUtilities(PluginMain plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Create an item stack with encoded destination and quantity
	 * @param destinationName
	 * @param quantity
	 * @return
	 */
	ItemStack createItem(int quantity) {

		// validate quantity
		quantity = Math.max(quantity, 1);

		// create item stack with configured material and data
		ItemStack newItem = getDefaultItem();

		// set quantity
		newItem.setAmount(quantity);
		
		// set item display name and lore
		setMetaData(newItem);
		
		// return new item
		return newItem;
	}
	
	
	/**
	 * Encode hidden destination key in item lore
	 * @param itemStack
	 * @param destinationName
	 */
	void setMetaData(ItemStack itemStack) {
		
		// retrieve item name and lore from language file file
		String displayName = plugin.messageManager.getItemName();
		List<String> configLore = plugin.messageManager.getItemLore();

		// allow for '&' character for color codes in name and lore
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);

		ArrayList<String> coloredLore = new ArrayList<String>();
		
		for (String line : configLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		// set invisible tag in first line of lore
		coloredLore.set(0, itemTag + coloredLore.get(0));

		// get item metadata object
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		// set item metadata display name to value from config file
		itemMeta.setDisplayName(displayName);
		
		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);
		
		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}
	
	
	/**
	 * Check if itemStack is a HomeStar item
	 * @param itemStack
	 * @return boolean
	 */
	boolean isHomeStar(ItemStack itemStack) {
		
		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			if (plugin.debug) {
				plugin.getLogger().info("");
			}
			return false;
		}
				
		// if item stack does not have lore return false
		if (! itemStack.getItemMeta().hasLore()) {
			if (plugin.debug) {
				plugin.getLogger().info("Item does not have lore.");
			}
			return false;
		}
		
		// get item lore
		List<String> itemLore = itemStack.getItemMeta().getLore();
		if (plugin.debug) {
			for (String string : itemLore) {
				plugin.getLogger().info(string);				
			}
		}
		
		// check that lore contains hidden token
		if (! itemLore.isEmpty() && itemLore.get(0).startsWith(itemTag)) {
			return true;
		}
		else if (plugin.debug) {
			plugin.getLogger().info("Item lore does not start with itemTag.");
		}
		return false;
	}
	
	String hiddenString(String s) {
		String hidden = "";
		for (char c : s.toCharArray())
			hidden += ChatColor.COLOR_CHAR + "" + c;
		return hidden;
	}
	
	@Override
	public Boolean isValidIngredient() {
		return PluginMain.instance.getConfig().getBoolean("allow-in-recipes");
	}
	
	@Override
	public int getCooldownTime() {
		return PluginMain.instance.getConfig().getInt("cooldown-time");
	}

	@Override
	public int getWarmupTime() {
		return PluginMain.instance.getConfig().getInt("warmup-time");
	}


	@Override
	public int getMinSpawnDistance() {
		return PluginMain.instance.getConfig().getInt("minimum-distance");
	}


	@Override
	public Boolean isCancelledOnDamage() {
		return PluginMain.instance.getConfig().getBoolean("cancel-on-damage");
	}


	@Override
	public Boolean isCancelledOnMovement() {
		return PluginMain.instance.getConfig().getBoolean("cancel-on-movement");
	}


	@Override
	public Boolean isCancelledOnInteraction() {
		return PluginMain.instance.getConfig().getBoolean("cancel-on-interaction");
	}
	
	@Override
	public Boolean isWarmingUp(Player player) {
		return PluginMain.instance.warmupManager.isWarmingUp(player);
	}
	
	@Override
	public Boolean isCoolingDown(Player player) {
		return PluginMain.instance.cooldownManager.getTimeRemaining(player) > 0;
	}
	
	@Override
	public long cooldownTimeRemaining(Player player) {
		return PluginMain.instance.cooldownManager.getTimeRemaining(player);
	}
	
	@Override
	public List<String> getEnabledWorlds() {
		return PluginMain.instance.commandManager.getEnabledWorlds();
	}
	
	@Override
	public void cancelTeleport(Player player) {
		PluginMain.instance.warmupManager.cancelTeleport(player);
	}








	/**
	 * Create an itemStack with default material and data from config
	 * @return ItemStack
	 */
	@Override
	public ItemStack getDefaultItem() {
		
		// get material type and data from config file
		String materialString = plugin.getConfig().getString("item-material");

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
		
		// parse material data from config file if present
		byte configMaterialDataByte;
		
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
		
		// create item stack with configured material and data
		ItemStack newItem = new ItemStack(configMaterial,1,configMaterialDataByte);
		
		return newItem;
	}

	@Override
	public String getItemName() {
		return plugin.messageManager.getItemName();
	}

}

