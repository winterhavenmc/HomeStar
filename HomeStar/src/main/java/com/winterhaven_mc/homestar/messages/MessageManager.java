package com.winterhaven_mc.homestar.messages;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Implements message manager for HomeStar
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public final class MessageManager extends AbstractMessageManager {

	private PluginMain plugin;

	/**
	 * Constructor method for class
	 *
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);

		this.plugin = plugin;
	}


	@Override
	protected Map<String,String> getDefaultReplacements(CommandSender recipient) {

		Map<String,String> replacements = new HashMap<>();

		// strip color codes
		replacements.put("%PLAYER_NAME%",ChatColor.stripColor(recipient.getName()));
		replacements.put("%WORLD_NAME%",ChatColor.stripColor(getWorldName(recipient)));
		replacements.put("%ITEM_NAME%", ChatColor.stripColor(getItemName()));
		replacements.put("%QUANTITY%","1");
		replacements.put("%MATERIAL%","unknown");
		replacements.put("%DESTINATION_NAME%",ChatColor.stripColor(getSpawnDisplayName()));
		replacements.put("%TARGET_PLAYER%","target player");
		replacements.put("%WARMUP_TIME%",
				getTimeString(TimeUnit.SECONDS.toMillis(plugin.getConfig().getInt("teleport-warmup"))));

		// leave color codes intact
		replacements.put("%player_name%",recipient.getName());
		replacements.put("%world_name%",getWorldName(recipient));
		replacements.put("%item_name%",getItemName());
		replacements.put("%destination_name%",getSpawnDisplayName());
		replacements.put("%target_player%","target player");

		// if recipient is player, get remaining cooldown time from teleport manager
		if (recipient instanceof Player) {
			replacements.put("%COOLDOWN_TIME%",
					getTimeString(plugin.teleportManager.getCooldownTimeRemaining((Player)recipient)));
		}
		else {
			replacements.put("%COOLDOWN_TIME%",getTimeString(0L));
		}

		return replacements;
	}

	/**
	 *  Send message to player
	 * 
	 * @param recipient		player receiving message
	 * @param messageId		message identifier in messages file
	 */
	public final void sendMessage(final CommandSender recipient, final MessageId messageId) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		//noinspection unchecked
		sendMessage(recipient,messageId,replacements);
	}

	
	/**
	 *  Send message to player
	 * 
	 * @param recipient			player receiving message
	 * @param messageId			message identifier in messages file
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final String destinationName) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		replacements.put("%DESTINATION_NAME%",destinationName);

		//noinspection unchecked
		sendMessage(recipient,messageId,replacements);
	}

	
	/**
	 * Send message to player
	 * 
	 * @param recipient			player receiving message
	 * @param messageId			message identifier in messages file
	 * @param quantity			number of items
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final Integer quantity) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		replacements.put("%QUANTITY%",quantity.toString());

		//noinspection unchecked
		sendMessage(recipient,messageId,replacements);
	}

	
	/**
	 * Send message to player
	 * 
	 * @param recipient			player recieving message
	 * @param messageId			message identifier in messages file
	 */
	@SuppressWarnings("unused")
	final void sendMessage(final CommandSender recipient,
						   final MessageId messageId,
						   final Integer quantity,
						   final Player targetPlayer) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		replacements.put("%QUANTITY%",quantity.toString());
		replacements.put("%TARGET_PLAYER%",targetPlayer.getName());

		//noinspection unchecked
		sendMessage(recipient,messageId,replacements);
	}

}
