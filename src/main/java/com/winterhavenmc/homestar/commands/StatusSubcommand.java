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
import com.winterhavenmc.homestar.util.Macro;
import com.winterhavenmc.homestar.util.MessageId;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;


final class StatusSubcommand extends AbstractSubcommand
{
	private final PluginMain plugin;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to plugin main class instance
	 */
	StatusSubcommand(final PluginMain plugin)
	{
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "status";
		this.permissionNode = "homestar.status";
		this.usageString = "/homestar status";
		this.description = MessageId.COMMAND_HELP_STATUS;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission(permissionNode))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_STATUS_PERMISSION).send();
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			return true;
		}

		// output config settings
		displayStatusHeader(sender);
		displayPluginVersion(sender);
		displayDebugSetting(sender);
		displayLanguageSetting(sender);
		displayLocaleSetting(sender);
		displayTimezoneSetting(sender);
		displayDefaultMaterialSetting(sender);
		displayMinimumDistanceSetting(sender);
		displayTeleportWarmupSetting(sender);
		displayTeleportCooldownSetting(sender);
		displayShiftClickSetting(sender);
		displayCancelOnDamageSetting(sender);
		displayCancelOnMovementSetting(sender);
		displayCancelOnInteractionSetting(sender);
		displayRemoveFromInventorySetting(sender);
		displayAllowInRecipesSetting(sender);
		displayLightningSetting(sender);
		displayEnabledWorlds(sender);
		displayStatusFooter(sender);

		return true;
	}


	private void displayStatusHeader(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_HEADER).send();
	}


	private void displayPluginVersion(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_PLUGIN_VERSION)
				.setMacro(Macro.SETTING, plugin.getDescription().getVersion())
				.send();
	}


	private void displayDebugSetting(final CommandSender sender)
	{
		if (plugin.getConfig().getBoolean("debug"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void displayLanguageSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_LANGUAGE)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("language"))
				.send();
	}


	@SuppressWarnings("UnusedReturnValue")
	private boolean displayLocaleSetting(final CommandSender sender)
	{
		return allEqual()
				? displaySimpleLocaleSetting(sender)
				: displayDetailedLocaleSetting(sender);
	}


	private boolean displaySimpleLocaleSetting(final CommandSender sender)
	{
		return plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_LOCALE)
				.setMacro(Macro.SETTING, plugin.messageBuilder.config().locale().toLanguageTag())
				.send();
	}


	private boolean displayDetailedLocaleSetting(final CommandSender sender)
	{
		return plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_LOCALE_DETAIL)
				.setMacro(Macro.NUMBER_LOCALE, plugin.messageBuilder.config().numberLocale().toLanguageTag())
				.setMacro(Macro.DATE_LOCALE, plugin.messageBuilder.config().dateLocale().toLanguageTag())
				.setMacro(Macro.TIME_LOCALE, plugin.messageBuilder.config().timeLocale().toLanguageTag())
				.setMacro(Macro.LOG_LOCALE, plugin.messageBuilder.config().logLocale().toLanguageTag())
				.send();
	}

	private boolean allEqual()
	{
		return (plugin.messageBuilder.config().numberLocale().equals(plugin.messageBuilder.config().dateLocale())
				&& plugin.messageBuilder.config().numberLocale().equals(plugin.messageBuilder.config().timeLocale())
				&& plugin.messageBuilder.config().numberLocale().equals(plugin.messageBuilder.config().logLocale()));
	}


	private void displayTimezoneSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TIMEZONE)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("timezone", ZoneId.systemDefault().toString()))
				.send();
	}


	private void displayDefaultMaterialSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_DEFAULT_MATERIAL)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("item-material"))
				.send();
	}


	private void displayMinimumDistanceSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_MINIMUM_DISTANCE)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("minimum-distance"))
				.send();
	}


	private void displayTeleportWarmupSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TELEPORT_WARMUP)
				.setMacro(Macro.SETTING, Duration.ofSeconds(plugin.getConfig().getInt("teleport-warmup")))
				.send();
	}


	private void displayTeleportCooldownSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TELEPORT_COOLDOWN)
				.setMacro(Macro.SETTING, Duration.ofSeconds(plugin.getConfig().getInt("teleport-cooldown")))
				.send();
	}


	private void displayShiftClickSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_SHIFT_CLICK)
				.setMacro(Macro.SETTING, plugin.getConfig().getBoolean("shift-click"))
				.send();
	}


	private void displayCancelOnDamageSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_DAMAGE)
				.setMacro(Macro.SETTING, plugin.getConfig().getBoolean("cancel-on-damage"))
				.send();
	}

	private void displayCancelOnMovementSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_MOVEMENT)
				.setMacro(Macro.SETTING, plugin.getConfig().getBoolean("cancel-on-movement"))
				.send();
	}

	private void displayCancelOnInteractionSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_INTERACTION)
				.setMacro(Macro.SETTING, plugin.getConfig().getBoolean("cancel-on-interaction"))
				.send();
	}


	private void displayRemoveFromInventorySetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_INVENTORY_REMOVAL)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("remove-from-inventory"))
				.send();
	}


	private void displayAllowInRecipesSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_ALLOW_IN_RECIPES)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("allow-in-recipes"))
				.send();
	}


	private void displayLightningSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_DISPLAY_LIGHTNING)
				.setMacro(Macro.SETTING, plugin.getConfig().getString("lightning"))
				.send();
	}


	private void displayEnabledWorlds(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_ENABLED_WORLDS)
				.setMacro(Macro.SETTING, plugin.messageBuilder.worlds().enabledNames().toString())
				.send();
	}


	private void displayStatusFooter(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_FOOTER)
				.setMacro(Macro.PLUGIN, plugin.getDescription().getName())
				.setMacro(Macro.URL, "https://github.com/winterhavenmc/MessageBuilderLib")
				.send();
	}

}
