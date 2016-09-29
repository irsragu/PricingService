package com.logos.PricingService.util;

public class GenerateOnStartUp implements Runnable {

	@Override
	public void run() {
		System.out.println("In the GenerateOnStartUp run ...");
		GenerateHistoricalFile ghf = new GenerateHistoricalFile();
		ghf.generateHistoricalFile();
	}

}
