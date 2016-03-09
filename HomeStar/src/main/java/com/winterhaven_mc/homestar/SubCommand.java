package com.winterhaven_mc.homestar;

public enum SubCommand {
	
	GIVE,
	DESTROY,
	STATUS,
	RELOAD;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
