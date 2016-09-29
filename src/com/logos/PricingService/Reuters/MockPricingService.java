package com.logos.PricingService.Reuters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.Price;
import com.logos.PricingService.Pojo.PriceObserver;

public class MockPricingService {
	HashMap<String, PriceObserver> symbolPriceMap;
	Queue<PriceObserver> priceWriterQueue;
	Logger logger = Logger.getGlobal();

	public MockPricingService(HashMap<String, PriceObserver> symbolPriceMap, Queue<PriceObserver> priceWriterQueue) {
		super();
		this.symbolPriceMap = symbolPriceMap;
		this.priceWriterQueue = priceWriterQueue;
	}

	public void startFeed() {
		int i = 0;
		try {
			while (true) {
				Thread.sleep(5000);
				i++;
				for (PriceObserver priceObserver : symbolPriceMap.values()) {
					Price price = priceObserver.getPrice();
					price.setBid(i + 0.01);
					price.setAsk(i + 0.02);
					price.setLast(i + 0.03);
					price.setVol(i * 10);
					price.setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
					priceObserver.setPrice(price);
					priceObserver.isUpdated();
					priceWriterQueue.add(priceObserver);
					// logger.info("In the Mocking Pricing Feeder " +
					// price.toString());
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
