package com.winterhaven_mc.homestar.commands;

import com.winterhaven_mc.homestar.PluginMain;
import com.winterhaven_mc.homestar.messages.Message;
import com.winterhaven_mc.homestar.sounds.SoundId;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.homestar.messages.Macro.ITEM_QUANTITY;
import static com.winterhaven_mc.homestar.messages.MessageId.*;


public class DestroyCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	DestroyCommand(PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("destroy");
		this.setUsage("/homestar destroy");
		this.setDescription(COMMAND_HELP_DESTROY);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_DESTROY_CONSOLE).send(plugin.languageHandler);
			return true;
		}

		// if command sender does not have permission to destroy HomeStars, output error message and return true
		if (!sender.hasPermission("homestar.destroy")) {
			Message.create(sender, PERMISSION_DENIED_DESTROY).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		Player player = (Player) sender;
		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player is holding a homestar stack
		if (!plugin.homeStarFactory.isItem(playerItem)) {
			Message.create(sender, COMMAND_FAIL_DESTROY_NO_MATCH).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		int quantity = playerItem.getAmount();
		playerItem.setAmount(0);
		player.getInventory().setItemInMainHand(playerItem);
		Message.create(sender, COMMAND_SUCCESS_DESTROY).setMacro(ITEM_QUANTITY, quantity).send(plugin.languageHandler);
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_DESTROY);
		return true;
	}

}
