package com.winterhaven_mc.homestar.messages;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
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

	/**
	 * Constructor method for class
	 *
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);
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
						   final Player targetPlayer) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		replacements.put("%TARGET_PLAYER%",targetPlayer.getName());

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


	/**
	 * Get item name from language file
	 * @return the formatted display name of the HomeStar item
	 */
	public final String getItemName() {
		return messages.getString("item-name");
	}


	/**
	 * Get configured plural item name from language file
	 * @return the formatted plural display name of the HomeStar item
	 */
	@SuppressWarnings({"WeakerAccess", "unused"})
	public final String getItemNamePlural() {
		return messages.getString("item-name-plural");
	}


	/**
	 * Get configured item lore from language file
	 * @return List of Strings containing the lines of item lore
	 */
	public final List<String> getItemLore() {
		return messages.getStringList("item-lore");
	}


	/**
	 * Get spawn display name from language file
	 * @return the formatted display name for the world spawn
	 */
	public final String getSpawnDisplayName() {
		return messages.getString("spawn-display-name");
	}


	/**
	 * Get home display name from language file
	 * @return the formatted display name for a bedspawn
	 */
	public final String getHomeDisplayName() {
		return messages.getString("home-display-name");
	}

}
