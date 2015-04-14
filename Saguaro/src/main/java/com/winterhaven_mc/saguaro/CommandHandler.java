package com.winterhaven_mc.saguaro;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;


public class CommandHandler implements CommandExecutor {

	// reference to main class
	SaguaroMain plugin;

	public CommandHandler (SaguaroMain plugin) {
		this.plugin = plugin;
		plugin.getCommand("saguaro").setExecutor(this);
	}

	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		int maxArgs = 1;
		if (args.length > maxArgs) {
			sender.sendMessage("Too many arguments!");
			return false;
		}

		// if called with no arguments, output config settings
		if (args.length < 1) {
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Version " + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Telnet enabled: " + plugin.getConfig().getString("telnet-enabled","true"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] Listening Port: " + plugin.telnetPort);
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] File Output Enabled: " + plugin.getConfig().getString("file-output-enabled","false"));
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] File Update Period: " + plugin.fileUpdatePeriod);
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] TPS Sample Period: " + plugin.tpsSamplePeriod);
			return true;
		}
		
		String subcmd = args[0];
		
		// reload command
		if (subcmd.equalsIgnoreCase("reload") && sender.hasPermission("saguaro.reload")) {

			// reload config.yml
			plugin.reloadConfig();
			
			// send reloaded message to command sender
			sender.sendMessage(ChatColor.AQUA + "[Saguaro] config reloaded.");
			
			// stop telnet server if it is running
			if (plugin.telnetServer != null) {
				plugin.telnetServer.stop();
			}
			
			// restart telnet server if configured
			if (plugin.getConfig().getBoolean("telnet-enabled",true)) {

				// read port from config file
				final int telnetPort = plugin.getConfig().getInt("telnet-port",25550);

				// delay start of telnet server 2 seconds (40 ticks) to allow old server instance to close
				new BukkitRunnable() {

					@Override
					public void run() {

						// try to start telnet server on configured port
						try {
							TelnetServer telnetServer = new TelnetServer(plugin,telnetPort);
							plugin.telnetServer = telnetServer;
							sender.sendMessage(ChatColor.AQUA + "Telnet server listening on port " 
									+ ChatColor.DARK_AQUA + telnetPort + ChatColor.AQUA + ".");
						} catch (IOException e) {
							sender.sendMessage(ChatColor.DARK_RED + "Could not start telnet server!");
							if (e.getLocalizedMessage() != null) {
								sender.sendMessage(e.getLocalizedMessage());
							}
							if (plugin.debug) {
								e.printStackTrace();
							}
						}
					}
				}.runTaskLater(plugin, 40);
			}
			
			// cancel file writer task if it is running
			if (plugin.fileWriterTask != null) {
				plugin.fileWriterTask.cancel();
			}
			
			// start new file writer task if configured
			if (plugin.getConfig().getBoolean("file-output-enabled")) {
				plugin.fileUpdatePeriod = plugin.getConfig().getInt("file-update-period",60);
				plugin.fileWriterTask = new FileWriter(plugin).runTaskTimer(plugin, 0, plugin.fileUpdatePeriod*20);
			}
			
			// restart TpsMeter
			if (plugin.tpsMeterTask != null) {
				plugin.tpsMeterTask.cancel();
			}
			plugin.tpsSamplePeriod = plugin.getConfig().getInt("tps-sample-period",60);
			plugin.tpsMeterTask = new TpsMeter(plugin).runTaskTimer(plugin, plugin.tpsSamplePeriod*20, plugin.tpsSamplePeriod*20);
			
			return true;
		}
		return false;
	}
	
}
