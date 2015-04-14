package com.winterhaven_mc.saguaro;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class FileWriter extends BukkitRunnable {

	SaguaroMain plugin;
	String fileName;

	/**
	 * Class constructor method
	 * @param plugin
	 */
	FileWriter(SaguaroMain plugin) {
		this.plugin = plugin;
		this.fileName = plugin.getDataFolder() + File.separator + "cacti_data.txt";
	}

	@Override
	public void run() {
		
		File dataFile = new File(fileName);
		try {
			FileUtils.writeStringToFile(dataFile,plugin.dataCache.getDataString());
		} catch (IOException e) {
			plugin.getLogger().severe("Could not write to file!");
			if (e.getLocalizedMessage() != null) {
				plugin.getLogger().severe(e.getLocalizedMessage());
			}
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}

}
