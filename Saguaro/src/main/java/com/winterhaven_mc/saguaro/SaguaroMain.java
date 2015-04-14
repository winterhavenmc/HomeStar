package com.winterhaven_mc.saguaro;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class SaguaroMain extends JavaPlugin {

	final Boolean debug = getConfig().getBoolean("debug");

	CommandHandler commandHandler;
	TelnetServer telnetServer;
	DataCache dataCache;

	BukkitTask tpsMeterTask;
	BukkitTask fileWriterTask;
	int telnetPort;

	// get tps sample period from config file, or default to 60 (seconds)
	int tpsSamplePeriod = getConfig().getInt("tps-sample-period", 60);

	// get file update period from config file, or default to 60 (seconds)
	int fileUpdatePeriod = getConfig().getInt("file-update-period", 60);

	public void onEnable() {

		// copy default config from jar if it doesn't exist
		saveDefaultConfig();

		// register command handler
		commandHandler = new CommandHandler(this);

		// instantiate TPS Meter
		tpsMeterTask = new TpsMeter(this).runTaskTimer(this,
				tpsSamplePeriod * 20, tpsSamplePeriod * 20);

		// instantiate data cache
		dataCache = new DataCache(this);

		// if configured, start file writer task
		if (getConfig().getBoolean("file-output-enabled", false)) {
			fileWriterTask = new FileWriter(this).runTaskTimer(this, 0,
					fileUpdatePeriod * 20);
		}

		// if configured, start telnet server
		if (getConfig().getBoolean("telnet-enabled", true)) {
			telnetPort = getConfig().getInt("telnet-port", 25550);
			try {
				telnetServer = new TelnetServer(this, telnetPort);
				getLogger().info(
						"Telnet server listening on port " + telnetPort);
			} catch (IOException e) {
				getLogger().severe("Could not start telnet server!");
				if (e.getLocalizedMessage() != null) {
					getLogger().severe(e.getLocalizedMessage());
				}
				if (debug) {
					e.printStackTrace();
				}
			}
		}

	}

	public void onDisable() {

		// stop telnet server
		if (telnetServer != null) {
			telnetServer.stop();
		}

		// stop file writer
		if (fileWriterTask != null) {
			fileWriterTask.cancel();
		}

		// stop tps meter
		if (tpsMeterTask != null) {
			tpsMeterTask.cancel();
		}

		// cancel all remaining repeating tasks for this plugin
		getServer().getScheduler().cancelTasks(this);

	}

}
