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

package com.winterhavenmc.homestar.util;

import com.winterhavenmc.homestar.PluginMain;
import com.winterhavenmc.library.messagebuilder.models.keys.ConstantKey;
import com.winterhavenmc.library.messagebuilder.models.keys.ItemKey;
import com.winterhavenmc.library.messagebuilder.models.keys.ValidConstantKey;
import com.winterhavenmc.library.messagebuilder.models.keys.ValidItemKey;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;


/**
 * Factory class with methods for creating and using HomeStar item stacks
 */
public final class HomeStarUtility
{
	public static final ValidConstantKey HOME_KEY = ConstantKey.of("LOCATION.HOME").isValid().orElseThrow();
	public static final ValidConstantKey SPAWN_KEY = ConstantKey.of("LOCATION.SPAWN").isValid().orElseThrow();
	public static final String ITEM_KEY_STRING = "HOMESTAR";
	private final PluginMain plugin;

	/**
	 * class constructor
	 *
	 * @param plugin reference to plugin main class
	 */
	public HomeStarUtility(final PluginMain plugin)
	{
		this.plugin = plugin;
	}


	/**
	 * Create a HomeStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of HomeStar items in newly created stack
	 * @return ItemStack of HomeStar items
	 */
	public ItemStack create(final int passedQuantity)
	{
		int quantity = Math.max(1, passedQuantity);

		ValidItemKey validItemKey = ItemKey.of(ITEM_KEY_STRING).isValid().orElseThrow();
		Optional<ItemStack> itemStack = plugin.messageBuilder.items().createItem(validItemKey);
		if (itemStack.isPresent())
		{
			ItemStack returnItem = itemStack.get();
			quantity = Math.min(quantity, returnItem.getMaxStackSize());
			returnItem.setAmount(quantity);
			return returnItem;
		}
		else
		{
			return null;
		}
	}

}
