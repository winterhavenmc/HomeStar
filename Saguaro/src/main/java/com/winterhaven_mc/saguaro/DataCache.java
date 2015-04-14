package com.winterhaven_mc.saguaro;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class DataCache {
	
	final SaguaroMain plugin;
	
	private long serverStartTime = 0;
	
	private double tps = 0;
	private long uptime = 0;
	private int playerCount = 0;
	private int playerMax = 0;
	private int pluginCount = 0;
	private int entityCount = 0;
	private int chunkCount = 0;
	private long totalWorldSize = 0;
	private long memoryMax = 0;
	private long memoryTotal = 0;
	private long memoryFree = 0;
	
	/**
	 * Class constructor method
	 * @param plugin
	 */
	DataCache(final SaguaroMain plugin) {
		this.plugin = plugin;
		this.serverStartTime = System.currentTimeMillis();
		
        // Create the task anonymously and schedule to run every 30 seconds (600 ticks)
        new BukkitRunnable() {
 
            @Override
            public void run() {
            	
        		int entityCount = 0;
        		int chunkCount = 0;
        		long totalWorldSize = 0;
        		
        		for (World world : plugin.getServer().getWorlds()) {
        			entityCount += world.getEntities().size();
        			chunkCount += world.getLoadedChunks().length;
        			totalWorldSize += FileUtils.sizeOfDirectory(new File(world.getWorldFolder().getAbsolutePath()));
        		}
        		setEntityCount(entityCount);
        		setChunkCount(chunkCount);
        		setTotalWorldSize(totalWorldSize);
        		setPluginCount(plugin.getServer().getPluginManager().getPlugins().length);
        		setPlayerCount(plugin.getServer().getOnlinePlayers().length);
        		setPlayerMax(plugin.getServer().getMaxPlayers());
        		setMemoryMax(Runtime.getRuntime().maxMemory());
        		setMemoryTotal(Runtime.getRuntime().totalMemory());
        		setMemoryFree(Runtime.getRuntime().freeMemory());
        		setUptime(System.currentTimeMillis() - serverStartTime);
        		setTps(TpsMeter.tps);
            } 
        }.runTaskTimer(plugin, 0, 600);
	}

	
	public synchronized String getDataString() {
		
		String dataString = "";
		dataString = dataString + "uptime:" + getUptime() + " ";
		dataString = dataString + "tps:";
		if (getTps() < 0) {
			dataString = dataString + "U" + " ";
		}
		else {
			dataString = dataString + getTps() + " ";
		}
		dataString = dataString + "playerCount:" + getPlayerCount() + " ";
		dataString = dataString + "playerMax:" + getPlayerMax() + " ";
		dataString = dataString + "memMax:" + getMemoryMax() + " ";
		dataString = dataString + "memTotal:" + getMemoryTotal() + " ";
		dataString = dataString + "memFree:" + getMemoryFree() + " ";
		dataString = dataString + "pluginCount:" + getPluginCount() + " ";
		dataString = dataString + "chunkCount:" + getChunkCount() + " ";
		dataString = dataString + "entityCount:" + getEntityCount() + " ";
		dataString = dataString + "worldSize:" + getTotalWorldSize() + " ";

		return dataString.trim();
	}

	public double getTps() {
		return tps;
	}

	public void setTps(double tps) {
		this.tps = tps;
	}

	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getPlayerMax() {
		return playerMax;
	}

	public void setPlayerMax(int playerMax) {
		this.playerMax = playerMax;
	}

	public int getPluginCount() {
		return pluginCount;
	}

	public void setPluginCount(int pluginCount) {
		this.pluginCount = pluginCount;
	}

	public int getEntityCount() {
		return entityCount;
	}

	public void setEntityCount(int entityCount) {
		this.entityCount = entityCount;
	}

	public int getChunkCount() {
		return chunkCount;
	}

	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	public long getTotalWorldSize() {
		return totalWorldSize;
	}

	public void setTotalWorldSize(long totalWorldSize) {
		this.totalWorldSize = totalWorldSize;
	}

	public long getMemoryMax() {
		return memoryMax;
	}

	public void setMemoryMax(long memoryMax) {
		this.memoryMax = memoryMax;
	}

	public long getMemoryTotal() {
		return memoryTotal;
	}

	public void setMemoryTotal(long memoryTotal) {
		this.memoryTotal = memoryTotal;
	}

	public long getMemoryFree() {
		return memoryFree;
	}

	public void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}

}
