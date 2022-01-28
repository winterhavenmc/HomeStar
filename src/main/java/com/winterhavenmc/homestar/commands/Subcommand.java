package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;


interface Subcommand {

	boolean onCommand(final CommandSender sender, final List<String> argsList);

	List<String> onTabComplete(final CommandSender sender, final Command command,
							   final String alias, final String[] args);

	String getName();

	@SuppressWarnings("unused")
	String getUsage();

	void displayUsage(final CommandSender sender);

	MessageId getDescription();

	int getMinArgs();

	int getMaxArgs();

}
