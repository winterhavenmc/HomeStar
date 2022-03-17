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

package com.winterhavenmc.homestar.messages;

/**
 * Enum with entries for all player messages in language configuration files
 */
public enum MessageId {

	COMMAND_HELP_DESTROY,
	COMMAND_HELP_GIVE,
	COMMAND_HELP_HELP,
	COMMAND_HELP_RELOAD,
	COMMAND_HELP_STATUS,
	COMMAND_HELP_INVALID,
	COMMAND_HELP_USAGE_HEADER,

	COMMAND_FAIL_ARGS_COUNT_OVER,
	COMMAND_FAIL_ARGS_COUNT_UNDER,
	COMMAND_FAIL_DESTROY_CONSOLE,
	COMMAND_FAIL_DESTROY_NO_MATCH,
	COMMAND_FAIL_GIVE_INVENTORY_FULL,
	COMMAND_FAIL_PLAYER_NOT_FOUND,
	COMMAND_FAIL_QUANTITY_INVALID,
	COMMAND_FAIL_INVALID_COMMAND,

	COMMAND_SUCCESS_DESTROY,
	COMMAND_SUCCESS_GIVE,
	COMMAND_SUCCESS_GIVE_SELF,
	COMMAND_SUCCESS_GIVE_TARGET,
	COMMAND_SUCCESS_RELOAD,

	PERMISSION_DENIED_RELOAD,
	PERMISSION_DENIED_STATUS,
	PERMISSION_DENIED_DESTROY,
	PERMISSION_DENIED_GIVE,
	PERMISSION_DENIED_HELP,
	PERMISSION_DENIED_USE,

	TELEPORT_CANCELLED_DAMAGE,
	TELEPORT_CANCELLED_INTERACTION,
	TELEPORT_CANCELLED_MOVEMENT,
	TELEPORT_CANCELLED_NO_ITEM,
	TELEPORT_COOLDOWN,
	TELEPORT_FAIL_NO_BEDSPAWN,
	TELEPORT_FAIL_SHIFT_CLICK,
	TELEPORT_FAIL_WORLD_DISABLED,
	TELEPORT_SUCCESS,
	TELEPORT_MIN_DISTANCE,
	TELEPORT_WARMUP,

	LOG_USAGE,
}
