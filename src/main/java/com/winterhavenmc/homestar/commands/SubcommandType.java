package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.PluginMain;


public enum SubcommandType {

	DESTROY() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new DestroyCommand(plugin);
		}
	},

	GIVE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new GiveCommand(plugin);
		}
	},

	RELOAD() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ReloadCommand(plugin);
		}
	},

	STATUS() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new StatusCommand(plugin);
		}
	};


	abstract Subcommand create(final PluginMain plugin);

}
