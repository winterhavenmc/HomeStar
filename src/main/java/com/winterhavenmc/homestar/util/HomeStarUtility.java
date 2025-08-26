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
import org.bukkit.inventory.ItemStack;
import java.util.Optional;


/**
 * Factory class with methods for creating and using HomeStar item stacks
 */
public final class HomeStarUtility
{
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

		Optional<ItemStack> itemStack = plugin.messageBuilder.itemForge().createItem("HOMESTAR");
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
