package com.winterhaven_mc.homestar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.winterhaven_mc.homestar.PluginMain;


public final class WorldManager {

	// reference to main class
	private final PluginMain plugin;
	
	// list of enabled world names
	private final List<UUID> enabledWorldUIDs = new ArrayList<UUID>();
	
	// reference to MultiverseCore
	private final MultiverseCore mvCore;

	
	/**
	 * Class constructor
	 * @param plugin
	 */
	public WorldManager(PluginMain plugin) {
		
		// set reference to main class
		this.plugin = plugin;
		
		// get reference to Multiverse-Core if installed
		mvCore = (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
		if (mvCore != null && mvCore.isEnabled()) {
			plugin.getLogger().info("Multiverse-Core detected.");
		}
		
		// populate enabled world UID list field
		this.reload();
	}

	
	/**
	 * update enabledWorlds ArrayList field from config file settings
	 */
	public final void reload() {
		
		// clear enabledWorldUIDs field
		this.enabledWorldUIDs.clear();
		
		// if config list of enabled worlds is empty, add all server worlds
		if (plugin.getConfig().getStringList("enabled-worlds").isEmpty()) {
			
			// iterate through all server worlds
			for (World world : plugin.getServer().getWorlds()) {
				
				// add world UID to field if it is not already in list
				if (!this.enabledWorldUIDs.contains(world.getUID())) {
					this.enabledWorldUIDs.add(world.getUID());
				}
			}
		}
		// otherwise, add only the worlds in the config enabled worlds list
		else {
			// iterate through config list of enabled worlds, and add valid world UIDs to field
			for (String worldName : plugin.getConfig().getStringList("enabled-worlds")) {

				// get world by name
				World world = plugin.getServer().getWorld(worldName);

				// add world UID to field if it is not already in list and world exists
				if (world != null && !this.enabledWorldUIDs.contains(world.getUID())) {
					this.enabledWorldUIDs.add(world.getUID());
				}
			}
		}
		
		// remove config list of disabled worlds from enabledWorldUIDs field
		for (String worldName : plugin.getConfig().getStringList("disabled-worlds")) {
			
			// get world by name
			World world = plugin.getServer().getWorld(worldName);
			
			// if world is not null remove UID from list
			if (world != null) {
				this.enabledWorldUIDs.remove(world.getUID());
			}
		}
	}
	
	
	/**
	 * get list of enabled world names
	 * @return ArrayList of String enabledWorlds
	 */
	public final List<String> getEnabledWorldNames() {
		
		// create empty list of string for return
		List<String> resultList = new ArrayList<String>();
		
		// iterate through list of enabled world UIDs
		for (UUID worldUID : this.enabledWorldUIDs) {
			
			// get world by UID
			World world = plugin.getServer().getWorld(worldUID);
			
			// if world is not null, add name to return list
			if (world != null) {
				resultList.add(world.getName());
			}
		}

		// return result list
		return resultList;
	}

	
	/**
	 * Check if a world is enabled by UID
	 * @param worldUID
	 * @return
	 */
	public final boolean isEnabled(final UUID worldUID) {
		
		// if worldUID is null return false
		if (worldUID == null) {
			return false;
		}
		
		return this.enabledWorldUIDs.contains(worldUID);
	}
	

	/**
	 * Check if a world is enabled by world object
	 * @param world
	 * @return
	 */
	public final boolean isEnabled(final World world) {
		
		// if world is null return false
		if (world == null) {
			return false;
		}
		
		return this.enabledWorldUIDs.contains(world.getUID());		
	}

	
	/**
	 * Check if a world is enabled by name
	 * @param worldName
	 * @return
	 */
	public final boolean isEnabled(final String worldName) {
		
		// if worldName is null or empty, return false
		if (worldName == null || worldName.isEmpty()) {
			return false;
		}
		
		// get world by name
		World world = plugin.getServer().getWorld(worldName);
		
		// if world is null, return false
		if (world == null) {
			return false;
		}

		return (this.enabledWorldUIDs.contains(world.getUID()));
	}
	
	
	/**
	 * Get world name from world UID, using Multiverse alias if available
	 * @param world
	 * @return world name or multiverse alias as String
	 */
	public final String getWorldName(final UUID worldUID) {
		
		// if worldUID is null, return null
		if (worldUID == null) {
			return null;
		}
		
		// get world
		World world = plugin.getServer().getWorld(worldUID);
		
		// if world is null, return null
		if (world == null) {
			return null;
		}
		
		// get bukkit world name
		String worldName = world.getName();
		
		// if Multiverse is enabled, get MultiverseWorld object
		if (mvCore != null && mvCore.isEnabled()) {
			
			MultiverseWorld mvWorld = mvCore.getMVWorldManager().getMVWorld(world);
	
			// if Multiverse alias is not null or empty, set world name to alias
			if (mvWorld != null  && mvWorld.getAlias() != null && !mvWorld.getAlias().isEmpty()) {
				worldName = mvCore.getMVWorldManager().getMVWorld(worldName).getAlias();
			}
		}
	
		// return the bukkit world name or Multiverse world alias
		return worldName;
	}

	/**
	 * Get world name from world object, using Multiverse alias if available
	 * @param world
	 * @return world name or multiverse alias as String
	 */
	public final String getWorldName(final World world) {
		
		// if world is null, return null
		if (world == null) {
			return null;
		}
		
		// get bukkit world name
		String worldName = world.getName();
		
		// if Multiverse is enabled, get MultiverseWorld object
		if (mvCore != null && mvCore.isEnabled()) {
			
			MultiverseWorld mvWorld = mvCore.getMVWorldManager().getMVWorld(world);

			// if Multiverse alias is not null or empty, set world name to alias
			if (mvWorld != null  && mvWorld.getAlias() != null && !mvWorld.getAlias().isEmpty()) {
				worldName = mvCore.getMVWorldManager().getMVWorld(worldName).getAlias();
			}
		}

		// return the bukkit world name or Multiverse world alias
		return worldName;
	}

	
	/**
	 * Get world name from world name, using Multiverse alias if available
	 * @param world
	 * @return world name or multiverse alias as String
	 */
	public final String getWorldName(final String passedName) {
		
		// if passedName is null or empty, return null
		if (passedName == null || passedName.isEmpty()) {
			return null;
		}
		
		// get world
		World world = plugin.getServer().getWorld(passedName);
		
		// if world is null, return null
		if (world == null) {
			return null;
		}
		
		// get bukkit world name
		String worldName = world.getName();
		
		// if Multiverse is enabled, get MultiverseWorld object
		if (mvCore != null && mvCore.isEnabled()) {
			
			MultiverseWorld mvWorld = mvCore.getMVWorldManager().getMVWorld(world);

			// if Multiverse alias is not null or empty, set world name to alias
			if (mvWorld != null  && mvWorld.getAlias() != null && !mvWorld.getAlias().isEmpty()) {
				worldName = mvCore.getMVWorldManager().getMVWorld(worldName).getAlias();
			}
		}

		// return the bukkit world name or Multiverse world alias
		return worldName;
	}

	
	public final Location getSpawnLocation(final World world) {
		
		// if Multiverse is enabled, return Multiverse spawn location
		if (mvCore != null && mvCore.isEnabled()) {
			return mvCore.getMVWorldManager().getMVWorld(world).getSpawnLocation();
		}
		
		// return bukkit world spawn location
		return world.getSpawnLocation();
	}
	
}
