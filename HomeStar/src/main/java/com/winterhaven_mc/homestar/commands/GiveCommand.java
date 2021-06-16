package com.winterhaven_mc.homestar.commands;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.messages.Message;
import com.winterhaven_mc.homestar.messages.MessageId;
import com.winterhaven_mc.homestar.sounds.SoundId;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.winterhaven_mc.homestar.messages.Macro.*;
import static com.winterhaven_mc.homestar.messages.MessageId.*;


public class GiveCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	GiveCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("give");
		this.setUsage("/homestar give <player> [quantity]");
		this.setDescription(COMMAND_HELP_GIVE);
		this.setMinArgs(1);
		this.setMaxArgs(2);
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> returnList = new ArrayList<>();

		// return list of matching players players
		if (args.length == 2) {
			List<Player> matchedPlayers = plugin.getServer().matchPlayer(args[1]);
			for (Player player : matchedPlayers) {
				returnList.add(player.getName());
			}
		}

		// return some useful quantities
		else if (args.length == 3) {
			returnList.add("1");
			returnList.add("2");
			returnList.add("3");
			returnList.add("5");
			returnList.add("10");
		}

		return returnList;
	}


	@Override
	public boolean onCommand(CommandSender sender, List<String> args) {

		// if command sender does not have permission to give HomeStars, output error message and return true
		if (!sender.hasPermission("homestar.give")) {
			Message.create(sender, PERMISSION_DENIED_GIVE).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check min arguments
		if (args.size() < getMinArgs()) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// get passed player name
		String targetPlayerName = args.get(0);

		// try to match target player name to currently online player
		Player targetPlayer = matchPlayer(sender, targetPlayerName);

		// if no match, do nothing and return (message was output by matchPlayer method)
		if (targetPlayer == null) {
			return true;
		}

		int quantity = 1;

		if (args.size() > 1) {
			try {
				quantity = Integer.parseInt(args.get(1));
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_QUANTITY_INVALID).send(plugin.languageHandler);
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = plugin.getConfig().getInt("max-give-amount");
		if (maxQuantity < 0) {
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		// add specified quantity of homestar(s) to player inventory
		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(plugin.homeStarFactory.create(quantity));

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet()) {
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity) {
			Message.create(sender, COMMAND_FAIL_GIVE_INVENTORY_FULL).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// subtract noFitCount from quantity
		quantity = quantity - noFitCount;

		// don't display messages if giving item to self
		if (!sender.getName().equals(targetPlayer.getName())) {

			// send message and play sound to giver
			Message.create(sender, COMMAND_SUCCESS_GIVE)
					.setMacro(ITEM_QUANTITY, quantity)
					.setMacro(TARGET_PLAYER, targetPlayer)
					.send(plugin.languageHandler);

			// if giver is in game, play sound
			if (sender instanceof Player) {
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_GIVE_SENDER);
			}

			// send message to target player
			Message.create(targetPlayer, COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(ITEM_QUANTITY, quantity)
					.setMacro(TARGET_PLAYER, sender)
					.send(plugin.languageHandler);
		}
		// play sound to target player
		plugin.soundConfig.playSound(targetPlayer, SoundId.COMMAND_SUCCESS_GIVE_TARGET);
		return true;
	}


	/**
	 * Match online player; sends appropriate message for offline or unknown players
	 *
	 * @param sender the command sender
	 * @param targetPlayerName the player name to match
	 *
	 * @return Player - a matching player object, or null if no match
	 */
	private Player matchPlayer(final CommandSender sender, final String targetPlayerName) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(targetPlayerName);

		Player targetPlayer;

		// check exact match first
		targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

		// if no match, try substring match
		if (targetPlayer == null) {
			List<Player> playerList = plugin.getServer().matchPlayer(targetPlayerName);

			// if only one matching player, use it, otherwise send error message (no match or more than 1 match)
			if (playerList.size() == 1) {
				targetPlayer = playerList.get(0);
			}
		}

		// if match found, return target player object
		if (targetPlayer != null) {
			return targetPlayer;
		}

		// check if name matches known offline player
		HashSet<OfflinePlayer> matchedPlayers = new HashSet<>();
		for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
			if (targetPlayerName.equalsIgnoreCase(offlinePlayer.getName())) {
				matchedPlayers.add(offlinePlayer);
			}
		}
		if (matchedPlayers.isEmpty()) {
			Message.create(sender, MessageId.COMMAND_FAIL_PLAYER_NOT_FOUND).send(plugin.languageHandler);
		}
		else {
			Message.create(sender, MessageId.COMMAND_FAIL_PLAYER_NOT_ONLINE).send(plugin.languageHandler);
		}
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return null;
	}

}
