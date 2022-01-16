package com.winterhavenmc.homestar.commands;

import com.winterhavenmc.homestar.PluginMain;


public enum SubcommandType {

	DESTROY() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			subcommandRegistry.register(new DestroyCommand(plugin));
		}
	},

	GIVE() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			subcommandRegistry.register(new GiveCommand(plugin));
		}
	},

	HELP() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			subcommandRegistry.register(new HelpCommand(plugin, subcommandRegistry));
		}
	},

	RELOAD() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			subcommandRegistry.register(new ReloadCommand(plugin));
		}
	},

	STATUS() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			subcommandRegistry.register(new StatusCommand(plugin));
		}
	};


	abstract void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry);

}
