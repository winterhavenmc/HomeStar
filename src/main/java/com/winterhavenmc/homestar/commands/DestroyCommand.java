package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.homestar.sounds.SoundId;

import com.winterhavenmc.homestar.messages.Macro;
import com.winterhavenmc.homestar.messages.MessageId;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;


final class DestroyCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	DestroyCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("destroy");
		this.setUsage("/homestar destroy");
		this.setDescription(MessageId.COMMAND_HELP_DESTROY);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_DESTROY_CONSOLE).send();
			return true;
		}

		// if command sender does not have permission to destroy HomeStars, output error message and return true
		if (!sender.hasPermission("homestar.destroy")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_DESTROY).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		Player player = (Player) sender;
		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player is holding a homestar stack
		if (!plugin.homeStarFactory.isItem(playerItem)) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_DESTROY_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		int quantity = playerItem.getAmount();
		playerItem.setAmount(0);
		player.getInventory().setItemInMainHand(playerItem);
		plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_DESTROY)
				.setMacro(Macro.ITEM_QUANTITY, quantity)
				.send();
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_DESTROY);
		return true;
	}

}
