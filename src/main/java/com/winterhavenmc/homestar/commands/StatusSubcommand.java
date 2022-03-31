/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.homestar.sounds.SoundId;

import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhavenmc.util.TimeUnit.SECONDS;


final class StatusSubcommand extends AbstractSubcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	StatusSubcommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "status";
		this.usageString = "/homestar status";
		this.description = MessageId.COMMAND_HELP_STATUS;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("homestar.reload")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_STATUS).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// output config settings
		showPluginVersion(sender);
		showDebugSetting(sender);
		showLanguageSetting(sender);
		showDefaultMaterialSetting(sender);
		showMinimumDistanceSetting(sender);
		showTeleportWarmupSetting(sender);
		showTeleportCooldownSetting(sender);
		showLeftClickAllowedSetting(sender);
		showShiftClickRequiredSetting(sender);
		showCancelOnMovementSetting(sender);
		showRemoveFromInventorySetting(sender);
		showAllowInRecipesSetting(sender);
		showLightningSetting(sender);
		showEnabledWorlds(sender);

		return true;
	}


	private void showPluginVersion(final CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "[HomeStar] "
				+ ChatColor.AQUA + "Version: " + ChatColor.RESET + this.plugin.getDescription().getVersion());
	}


	private void showDebugSetting(final CommandSender sender) {
		if (plugin.getConfig().getBoolean("debug")) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void showLanguageSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));
	}


	private void showDefaultMaterialSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Default material: "
				+ ChatColor.RESET + plugin.getConfig().getString("item-material"));
	}


	private void showMinimumDistanceSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Minimum distance: "
				+ ChatColor.RESET + plugin.getConfig().getInt("minimum-distance"));
	}


	private void showTeleportWarmupSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Warmup: " + ChatColor.RESET
				+ plugin.messageBuilder.getTimeString(SECONDS.toMillis(plugin.getConfig().getInt("teleport-warmup"))));
	}


	private void showTeleportCooldownSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Cooldown: " + ChatColor.RESET
				+ plugin.messageBuilder.getTimeString(SECONDS.toMillis(plugin.getConfig().getInt("teleport-cooldown"))));
	}


	private void showLeftClickAllowedSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Left-click allowed: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("left-click"));
	}


	private void showShiftClickRequiredSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Shift-click required: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("shift-click"));
	}


	private void showCancelOnMovementSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN
				+ "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");
	}


	private void showRemoveFromInventorySetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Remove from inventory: "
				+ ChatColor.RESET + plugin.getConfig().getString("remove-from-inventory"));
	}


	private void showAllowInRecipesSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Allow in recipes: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("allow-in-recipes"));
	}


	private void showLightningSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Lightning: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("lightning"));
	}


	private void showEnabledWorlds(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());
	}

}
