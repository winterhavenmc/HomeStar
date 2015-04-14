package com.winterhaven_mc.saguaro;

import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitRunnable;

public class TpsMeter extends BukkitRunnable {

	// reference to main class
	SaguaroMain plugin;
	
	// initialize tps
	public static double tps = -1;

	private static long nanoStartTime;
	private static long nanoEndTime = System.nanoTime();

	private long nanoSamplePeriod;
	
	/**
	 * Class constructor method
	 * @param plugin
	 */
	public TpsMeter(SaguaroMain plugin) {		
		this.plugin = plugin;
		this.nanoSamplePeriod = TimeUnit.SECONDS.toNanos(plugin.tpsSamplePeriod);
	}

	
	@Override
	public void run() {
		
		nanoStartTime = nanoEndTime;
		nanoEndTime = System.nanoTime();
		double nanoElapsedTime = nanoEndTime - nanoStartTime;

		tps = (double)nanoSamplePeriod / nanoElapsedTime * 20.0d;
		
	}

}
