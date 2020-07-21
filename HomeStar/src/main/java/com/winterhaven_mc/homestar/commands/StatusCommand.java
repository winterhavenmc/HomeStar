package com.winterhaven_mc.homestar.commands;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.messages.Message;
import com.winterhaven_mc.homestar.sounds.SoundId;
import com.winterhaven_mc.util.LanguageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.winterhaven_mc.homestar.messages.MessageId.*;


public class StatusCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	StatusCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("status");
		this.setUsage("/homestar status");
		this.setDescription(COMMAND_HELP_STATUS);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("homestar.reload")) {
			Message.create(sender, PERMISSION_DENIED_STATUS).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.size() > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// output config settings
		String versionString = this.plugin.getDescription().getVersion();
		sender.sendMessage(ChatColor.DARK_AQUA + "[HomeStar] "
				+ ChatColor.AQUA + "Version: " + ChatColor.RESET + versionString);

		if (plugin.getConfig().getBoolean("debug")) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}

		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));

		sender.sendMessage(ChatColor.GREEN + "Default material: "
				+ ChatColor.RESET + plugin.getConfig().getString("item-material"));

		sender.sendMessage(ChatColor.GREEN + "Minimum distance: "
				+ ChatColor.RESET + plugin.getConfig().getInt("minimum-distance"));

		sender.sendMessage(ChatColor.GREEN + "Warmup: "
				+ ChatColor.RESET
				+ LanguageManager.getInstance().getTimeString(TimeUnit.SECONDS.toMillis(
				plugin.getConfig().getInt("teleport-warmup"))));

		sender.sendMessage(ChatColor.GREEN + "Cooldown: "
				+ ChatColor.RESET
				+ LanguageManager.getInstance().getTimeString(TimeUnit.SECONDS.toMillis(
				plugin.getConfig().getInt("teleport-cooldown"))));

		sender.sendMessage(ChatColor.GREEN + "Left-click allowed: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("left-click"));

		sender.sendMessage(ChatColor.GREEN + "Shift-click required: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("shift-click"));

		sender.sendMessage(ChatColor.GREEN
				+ "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");

		sender.sendMessage(ChatColor.GREEN + "Remove from inventory: "
				+ ChatColor.RESET + plugin.getConfig().getString("remove-from-inventory"));

		sender.sendMessage(ChatColor.GREEN + "Allow in recipes: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("allow-in-recipes"));

		sender.sendMessage(ChatColor.GREEN + "Lightning: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("lightning"));

		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());

		return true;
	}

}
