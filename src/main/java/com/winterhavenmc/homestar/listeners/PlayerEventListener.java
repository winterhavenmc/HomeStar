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

package com.winterhavenmc.homestar.listeners;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.homestar.messages.MessageId;
import com.winterhavenmc.homestar.sounds.SoundId;
import com.winterhavenmc.library.messagebuilder.ItemForge;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


/**
 * Implements player event listener for HomeStar events
 */
public final class PlayerEventListener implements Listener
{
	// reference to main class
	private final PluginMain plugin;


	/**
	 * Class constructor for PlayerEventListener
	 *
	 * @param plugin reference to this plugin's main class
	 */
	public PlayerEventListener(final PluginMain plugin)
	{
		// reference to main
		this.plugin = plugin;

		// register events in this class
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	/**
	 * Player death event handler
	 *
	 * @param event PlayerDeathEvent handled by this method
	 */
	@EventHandler
	void onPlayerDeath(final PlayerDeathEvent event)
	{
		// get event player
		Player player = event.getEntity();

		// cancel any pending teleport for player
		plugin.teleportHandler.cancelTeleport(player);
	}


	/**
	 * Player quit event handler
	 *
	 * @param event PlayerQuitEvent handled by this method
	 */
	@EventHandler
	void onPlayerQuit(final PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		// cancel any pending teleport for player
		plugin.teleportHandler.cancelTeleport(player);
	}


	/**
	 * Prepare Item Craft event handler<br>
	 * Prevents HomeStar items from being used in crafting recipes if configured
	 *
	 * @param event PrepareItemCraftEvent handled by this method
	 */
	@EventHandler
	void onCraftPrepare(final PrepareItemCraftEvent event)
	{
		// if allow-in-recipes is true in configuration, do nothing and return
		if (plugin.getConfig().getBoolean("allow-in-recipes"))
		{
			return;
		}

		// if crafting inventory contains HomeStar item, set result item to null
		for (ItemStack itemStack : event.getInventory())
		{
			if (ItemForge.isCustomItem(itemStack))
			{
				event.getInventory().setResult(null);
			}
		}
	}


	/**
	 * EntityDamageByEntity event handler<br>
	 * Cancels pending teleport if player takes damage during warmup
	 *
	 * @param event EntityDamageEvent handled by this method
	 */
	@EventHandler(ignoreCancelled = true)
	void onEntityDamage(final EntityDamageEvent event)
	{
		// if cancel-on-damage configuration is true, check if damaged entity is player
		if (plugin.getConfig().getBoolean("cancel-on-damage"))
		{

			// if damaged entity is player, check for pending teleport and
			// if player is in warmup hashmap, cancel teleport and send player message
			if (event.getEntity() instanceof Player player && plugin.teleportHandler.isWarmingUp(player))
			{
				cancelTeleportWithMessage(player, MessageId.TELEPORT_CANCELLED_DAMAGE);
			}
		}
	}


	/**
	 * PlayerMoveEvent handler<br>
	 * Cancels teleport if player moves during warmup
	 *
	 * @param event PlayerMoveEvent handled by this method
	 */
	@EventHandler
	void onPlayerMovement(final PlayerMoveEvent event)
	{
		// if cancel-on-movement configuration is false, do nothing and return
		if (!plugin.getConfig().getBoolean("cancel-on-movement"))
		{
			return;
		}

		// if player is in warmup hashmap, check for player movement other than head turning
		// and cancel teleport and send player message if movement detected
		if (plugin.teleportHandler.isWarmingUp(event.getPlayer())
				&& event.getFrom().distance(Objects.requireNonNull(event.getTo())) > 0)
		{
			cancelTeleportWithMessage(event.getPlayer(), MessageId.TELEPORT_CANCELLED_MOVEMENT);
		}
	}


	/**
	 * Cancel player teleportation, send message and play sound
	 *
	 * @param player    the player whose teleportation is being cancelled
	 * @param messageId the message id of the message sent to the player
	 */
	private void cancelTeleportWithMessage(final Player player, final MessageId messageId)
	{
		plugin.teleportHandler.cancelTeleport(player);
		plugin.messageBuilder.compose(player, messageId).send();
		plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
	}

}
