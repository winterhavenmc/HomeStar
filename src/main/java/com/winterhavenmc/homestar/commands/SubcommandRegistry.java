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

import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.Predicate;


final class SubcommandRegistry
{
	// instantiate empty subcommand map
	final Map<String, Subcommand> subcommandMap = new LinkedHashMap<>();


	/**
	 * Register a subcommand in the map by name.
	 *
	 * @param subcommand an instance of the command
	 */
	void register(final Subcommand subcommand)
	{
		subcommandMap.put(subcommand.getName().toLowerCase(), subcommand);
	}


	/**
	 * Get command instance from map by name
	 *
	 * @param name the command to retrieve from the map
	 * @return Subcommand - the subcommand instance, or null if no matching name
	 */
	Optional<Subcommand> getSubcommand(final String name)
	{
		return Optional.ofNullable(subcommandMap.get(name.toLowerCase()));
	}


	/**
	 * Get list of keys (subcommand names) from the subcommand map
	 *
	 * @return List of String - keys of the subcommand map
	 */
	Collection<String> getKeys()
	{
		return new LinkedHashSet<>(subcommandMap.keySet());
	}


	/**
	 * Get matching list of subcommand names for which sender has permission
	 *
	 * @param sender the command sender
	 * @param matchString the string prefix to match against command names
	 * @return List of String - command names that match prefix and sender permission
	 */
	List<String> matchingNames(final CommandSender sender, final String matchString)
	{
		return this.getSubcommandNames().stream()
				.filter(hasPermission(sender))
				.filter(matchesPrefix(matchString))
				.toList();
	}


	private Predicate<String> hasPermission(final CommandSender sender)
	{
		return subcommandName -> this.getSubcommand(subcommandName)
				.map(subcommand -> sender.hasPermission(subcommand.getPermissionNode()))
				.orElse(false);
	}


	private Predicate<String> matchesPrefix(final String prefix)
	{
		return subcommandName -> subcommandName.startsWith(prefix.toLowerCase());
	}

}
