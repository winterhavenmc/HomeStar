package com.winterhaven_mc.homestar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
	private	HashSet<Material> safeMaterials = new HashSet<Material>();

	
	/**
	 * Class constructor
	 * @param plugin
	 */
	HomeStarUtilities(PluginMain plugin) {
		this.plugin = plugin;
		safeMaterials = getSafeMaterials();
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
		
		// get item metadata object
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		// set item metadata display name to value from config file
		itemMeta.setDisplayName(itemTag + displayName);
		
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
		ItemStack newItem = new ItemStack(configMaterial,1,configMaterialDataByte);
		
		return newItem;
	}

	
	@Override
	public String getItemName() {
		return plugin.messageManager.getItemName();
	}
	
	
	/**
	 * Check if bedspawn location is missing or obstructed
	 * @param bedSpawnLocation
	 * @return safe location or null if none found
	 */
	Location getSafeBedSpawn(Location bedSpawnLocation) {
		
		if (bedSpawnLocation == null) {
			return null;
		}
		
		bedSpawnLocation = getRoundedDestination(bedSpawnLocation);
		
		Block bedSpawnBlock = null;
		
		if (bedSpawnLocation.getBlock() != null) {
			bedSpawnBlock = bedSpawnLocation.getBlock();
		}
		else {
			return null;
		}
		
		// test if actual bedspawn location is safe
		if (safeMaterials.contains(bedSpawnBlock.getType())
				&& safeMaterials.contains(bedSpawnBlock.getRelative(0,1,0).getType())) {
			return bedSpawnLocation;
		}

//			// test if location one block to north is safe
//			if (bedSpawnBlock.getRelative(1,0,0).getType() == null
//					|| bedSpawnBlock.getRelative(1,1,0).getType() == null
//					|| (safeMaterials.contains(bedSpawnBlock.getRelative(1,0,0).getType())
//					&& safeMaterials.contains(bedSpawnBlock.getRelative(1,1,0).getType()))) {
//				return bedSpawnLocation.add(1,0,0);
//			}
//			
//			// test if location one block to south is safe
//			if (bedSpawnBlock.getRelative(-1,0,0).getType() == null
//					|| bedSpawnBlock.getRelative(-1,1,0).getType() == null
//					|| (safeMaterials.contains(bedSpawnBlock.getRelative(-1,0,0).getType())
//					&& safeMaterials.contains(bedSpawnBlock.getRelative(-1,1,0).getType()))) {
//				return bedSpawnLocation.add(-1,0,0);
//			}
//
//			// test if location one block to east is safe
//			if (bedSpawnBlock.getRelative(0,0,1).getType() == null
//					|| bedSpawnBlock.getRelative(0,1,1).getType() == null
//					|| (safeMaterials.contains(bedSpawnBlock.getRelative(0,0,1).getType())
//					&& safeMaterials.contains(bedSpawnBlock.getRelative(0,1,1).getType()))) {
//				return bedSpawnLocation.add(0,0,1);
//			}
//			
//			// test if location one block to west is safe
//			if (bedSpawnBlock.getRelative(0,0,-1).getType() == null
//					|| bedSpawnBlock.getRelative(0,1,-1).getType() == null
//					|| (safeMaterials.contains(bedSpawnBlock.getRelative(0,0,-1).getType())
//					&& safeMaterials.contains(bedSpawnBlock.getRelative(0,1,-1).getType()))) {
//				return bedSpawnLocation.add(0,0,-1);
//			}
//		}
		return null;
	}

	
	// Not needed if using getSafeDestination(loc)
	Location getRoundedDestination(final Location loc)
	{
		final World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int)Math.round(loc.getY());
		int z = loc.getBlockZ();
		return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
	}

	
	HashSet<Material> getSafeMaterials() {
		
		HashSet<Material> safeMaterials = new HashSet<Material>();
		
		safeMaterials.add(Material.AIR);
		safeMaterials.add(Material.CARPET);
		safeMaterials.add(Material.CROPS);
		safeMaterials.add(Material.DEAD_BUSH);
		safeMaterials.add(Material.DOUBLE_PLANT);
		safeMaterials.add(Material.LADDER);
		safeMaterials.add(Material.LEAVES);
		safeMaterials.add(Material.LEAVES_2);
		safeMaterials.add(Material.LEVER);
		safeMaterials.add(Material.LONG_GRASS);
		safeMaterials.add(Material.MELON_STEM);
		safeMaterials.add(Material.PUMPKIN_STEM);
		safeMaterials.add(Material.RED_ROSE);
		safeMaterials.add(Material.SIGN_POST);
		safeMaterials.add(Material.SUGAR_CANE_BLOCK);
		safeMaterials.add(Material.TORCH);
		safeMaterials.add(Material.TRIPWIRE);
		safeMaterials.add(Material.TRIPWIRE_HOOK);
		safeMaterials.add(Material.VINE);
		safeMaterials.add(Material.WALL_SIGN);
		safeMaterials.add(Material.WEB);
		safeMaterials.add(Material.WHEAT);
		safeMaterials.add(Material.YELLOW_FLOWER);
		
		return safeMaterials;
	}
}

