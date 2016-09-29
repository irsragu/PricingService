package com.logos.PricingService.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeScheduler extends TimerTask {
//	private final static long ONCE_PER_DAY = 1000 * 60 * 60 * 24;
	private final static long ONCE_PER_DAY = 1000 * 60 * 2;

	// private final static int ONE_DAY = 1;
	private final static int SIX_PM = 18;
	private final static int ZERO_MINUTES = 0;

	public TimeScheduler() {
		super();
	}

	@Override
	public void run() {
		System.out.println("In the TimeScheduler run ...");
		GenerateHistoricalFile ghf = new GenerateHistoricalFile();
		ghf.generateHistoricalFile();
	}

	// call this method from your servlet init method
	@SuppressWarnings("deprecation")
	public void startTask() {
		System.out.println("Staryting teh timer task task now ....");
		Date date6pm = new Date();
		date6pm.setHours(SIX_PM);
		date6pm.setMinutes(ZERO_MINUTES);
		Timer timer = new Timer();
		timer.schedule(this, date6pm, ONCE_PER_DAY);
	}
	
	public static void main(String[] args)
	{
		TimeScheduler timeScheduler = new TimeScheduler();
		timeScheduler.startTask();
	}

}