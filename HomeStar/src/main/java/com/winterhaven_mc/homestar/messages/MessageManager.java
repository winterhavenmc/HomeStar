package com.winterhaven_mc.homestar.messages;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.util.SoundManager;
import com.winterhaven_mc.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements message manager for HomeStar
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public final class MessageManager {

	// reference to main class
	private final PluginMain plugin;

	// hashmap for per player message cooldown
	private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> messageCooldownMap;

	// configuration file manager for messages
	private YamlConfiguration messages;

	// message file helper
	private MessageFileHelper messageFileHelper;

	// sound manager
	private SoundManager soundManager;


	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// instantiate messageFileHelper
		this.messageFileHelper = new MessageFileHelper(plugin);

		// load messages from file
		this.messages = messageFileHelper.loadMessages();

		// initialize messageCooldownMap
		this.messageCooldownMap = new ConcurrentHashMap<>();

		// instantiate sound manager
		this.soundManager = new SoundManager(plugin);
	}


	/**
	 *  Send message to player
	 * 
	 * @param sender			player receiving message
	 * @param messageId			message identifier in messages file
	 */
	public final void sendPlayerMessage(final CommandSender sender, final String messageId) {
		this.sendPlayerMessage(sender, messageId, 1, "", null);
	}

	
	/**
	 *  Send message to player
	 * 
	 * @param sender			player receiving message
	 * @param messageId			message identifier in messages file
	 */
	public final void sendPlayerMessage(final CommandSender sender, final String messageId, final String destinationName) {
		this.sendPlayerMessage(sender, messageId, 1, destinationName, null);
	}

	
	/**
	 * Send message to player
	 * 
	 * @param sender			player receiving message
	 * @param messageId			message identifier in messages file
	 * @param quantity			number of items
	 */
	public final void sendPlayerMessage(final CommandSender sender, final String messageId, final Integer quantity) {
		this.sendPlayerMessage(sender, messageId, quantity, "", null);
	}

	
	/**
	 * Send message to player
	 * 
	 * @param sender			player recieving message
	 * @param messageId			message identifier in messages file
	 */
	@SuppressWarnings("unused")
	final void sendPlayerMessage(final CommandSender sender, final String messageId, final Player targetPlayer) {
		this.sendPlayerMessage(sender, messageId, 1, "", targetPlayer);
	}

	
	/**
	 * Send message to player
	 * 
	 * @param sender			player recieving message
	 * @param messageId			message identifier in messages file
	 */
	@SuppressWarnings("unused")
	final void sendPlayerMessage(final CommandSender sender, final String messageId,
								 final Integer quantity, final Player targetPlayer) {
		this.sendPlayerMessage(sender, messageId, quantity, "", targetPlayer);
	}

	
	/** Send message to player
	 * 
	 * @param sender			Player receiving message
	 * @param messageId			message identifier in messages file
	 * @param quantity			number of items
	 * @param targetPlayer		player targeted
	 */	
	@SuppressWarnings("WeakerAccess")
	final void sendPlayerMessage(final CommandSender sender,
								 final String messageId,
								 final Integer quantity,
								 final String destinationName,
								 final Player targetPlayer) {

		// if message is not enabled in messages file, do nothing and return
		if (!messages.getBoolean("messages." + messageId + ".enabled")) {
			return;
		}

		// set substitution variable defaults
		String playerName = "console";
		String targetPlayerName = "player";
		String worldName = "unknown";
		String cooldownString = "";
		String warmupString;

		if (targetPlayer != null) {
			targetPlayerName = targetPlayer.getName();
		}

		// if sender is a player...
		if (sender instanceof Player) {

			Player player = (Player) sender;

			// get message cooldown time remaining
			long lastDisplayed = getMessageCooldown(player,messageId);

			// get message repeat delay
			int messageRepeatDelay = messages.getInt("messages." + messageId + ".repeat-delay");

			// if message has repeat delay value and was displayed to player more recently, do nothing and return
			if (lastDisplayed > System.currentTimeMillis() - messageRepeatDelay * 1000) {
				return;
			}

			// if repeat delay value is greater than zero, add entry to messageCooldownMap
			if (messageRepeatDelay > 0) {
				putMessageCooldown(player,messageId);
			}

			// set player dependent variables
			playerName = player.getName();
			worldName = plugin.worldManager.getWorldName(player.getWorld());
			cooldownString = getTimeString(plugin.teleportManager.getCooldownTimeRemaining(player));
		}

		// get message from file
		String message = messages.getString("messages." + messageId + ".string");

		// get item name and strip color codes
		String itemName = getItemName();

		// get warmup value from config file
		warmupString = getTimeString(plugin.getConfig().getInt("teleport-warmup"));

		// if quantity is greater than one, use plural item name
		if (quantity > 1) {
			// get plural item name
			itemName = getItemNamePlural();
		}

		// do variable substitutions
		if (message.contains("%")) {
			message = StringUtil.replace(message,"%itemname%",itemName);
			message = StringUtil.replace(message,"%playername%",playerName);
			message = StringUtil.replace(message,"%worldname%",worldName);
			message = StringUtil.replace(message,"%timeremaining%",cooldownString);
			message = StringUtil.replace(message,"%warmuptime%",warmupString);
			message = StringUtil.replace(message,"%quantity%",quantity.toString());
			message = StringUtil.replace(message,"%destination%",destinationName);
			message = StringUtil.replace(message,"%targetplayer%",targetPlayerName);

			// do variable substitutions, stripping color codes from all caps variables
			message = StringUtil.replace(message,"%ITEMNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',itemName)));
			message = StringUtil.replace(message,"%PLAYERNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',playerName)));
			message = StringUtil.replace(message,"%WORLDNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',worldName)));
			message = StringUtil.replace(message,"%TARGETPLAYER%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',targetPlayerName)));
			message = StringUtil.replace(message,"%DESTINATION%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',destinationName)));

			// no stripping of color codes necessary, but do variable substitutions anyhow
			// in case all caps variables were used
			message = StringUtil.replace(message,"%TIMEREMAINING%", cooldownString);
			message = StringUtil.replace(message,"%WARMUPTIME%", warmupString);
			message = StringUtil.replace(message,"%QUANTITY%", quantity.toString());
		}
		
		// send message to player
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
	}


	/**
	 * Play sound
	 * @param sender command sender (player) to play sound
	 * @param soundId unique identifier that refers to sound in sounds.yml
	 */
	public final void sendPlayerSound(final CommandSender sender, final SoundId soundId) {
		this.soundManager.playerSound(sender,soundId.toString());
	}


	/**
	 * Add entry to message cooldown map
	 * @param player the player to insert in the message cooldown map
	 * @param messageId the message identifier to insert in the cooldown map
	 */
	private void putMessageCooldown(final Player player, final String messageId) {

		final ConcurrentHashMap<String, Long> tempMap = new ConcurrentHashMap<>();
		tempMap.put(messageId, System.currentTimeMillis());
		this.messageCooldownMap.put(player.getUniqueId(), tempMap);
	}


	/**
	 * get entry from message cooldown map
	 * @param player the player for whom to retrieve cooldown time
	 * @param messageId the message identifier for which retrieve cooldown time
	 * @return cooldown expire time
	 */
	private long getMessageCooldown(final Player player, final String messageId) {

		// check if player is in message cooldown hashmap
		if (messageCooldownMap.containsKey(player.getUniqueId())) {

			// check if messageID is in player's cooldown hashmap
			if (messageCooldownMap.get(player.getUniqueId()).containsKey(messageId)) {

				// return cooldown time
				return messageCooldownMap.get(player.getUniqueId()).get(messageId);
			}
		}
		return 0L;
	}


	/**
	 * Remove player from message cooldown map
	 * @param player the player to be removed from the message cooldown map
	 */
	public final void removePlayerCooldown(final Player player) {
		messageCooldownMap.remove(player.getUniqueId());
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
	@SuppressWarnings("WeakerAccess")
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


	/**
	 * Reload messages
	 */
	public final void reload() {

		// reload messages
		this.messages = messageFileHelper.loadMessages();

		// reload sounds
		soundManager.reload();
	}


	/**
	 * Format the time string with hours, minutes, seconds
	 * @return time string
	 */
	private String getTimeString(long duration) {

		StringBuilder timeString = new StringBuilder();

		int hours =   (int)duration / 3600;
		int minutes = (int)(duration % 3600) / 60;
		int seconds = (int)duration % 60;

		String hour_string = this.messages.getString("hour");
		String hour_plural_string = this.messages.getString("hour_plural");
		String minute_string = this.messages.getString("minute");
		String minute_plural_string = this.messages.getString("minute_plural");
		String second_string = this.messages.getString("second");
		String second_plural_string = this.messages.getString("second_plural");

		if (hours > 1) {
			timeString.append(hours);
			timeString.append(' ');
			timeString.append(hour_plural_string);
			timeString.append(' ');
		}
		else if (hours == 1) {
			timeString.append(hours);
			timeString.append(' ');
			timeString.append(hour_string);
			timeString.append(' ');
		}

		if (minutes > 1) {
			timeString.append(minutes);
			timeString.append(' ');
			timeString.append(minute_plural_string);
			timeString.append(' ');
		}
		else if (minutes == 1) {
			timeString.append(minutes);
			timeString.append(' ');
			timeString.append(minute_string);
			timeString.append(' ');
		}

		if (seconds > 1) {
			timeString.append(seconds);
			timeString.append(' ');
			timeString.append(second_plural_string);
		}
		else if (seconds == 1) {
			timeString.append(seconds);
			timeString.append(' ');
			timeString.append(second_string);
		}

		return timeString.toString().trim();
	}

}