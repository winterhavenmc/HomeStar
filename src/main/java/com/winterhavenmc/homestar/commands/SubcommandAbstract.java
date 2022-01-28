package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;


abstract class SubcommandAbstract implements Subcommand {

	protected String name;
	protected String usageString = "";
	protected MessageId description;
	protected int minArgs;
	protected int maxArgs;


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUsage() {
		return usageString;
	}

	@Override
	public void displayUsage(final CommandSender sender) {
		sender.sendMessage(usageString);
	}

	@Override
	public MessageId getDescription() {
		return description;
	}

	@Override
	public int getMinArgs() { return minArgs; }

	@Override
	public int getMaxArgs() { return maxArgs; }

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		return Collections.emptyList();
	}

}
