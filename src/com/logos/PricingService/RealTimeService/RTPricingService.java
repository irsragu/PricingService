package com.logos.PricingService.RealTimeService;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.Price;
import com.logos.PricingService.Pojo.PriceObserver;

public class RTPricingService {
	List<String> masterSymbolList;
	HashMap<String, PriceObserver> symbolPrice;
	Queue<PriceObserver> priceWriterQueue;
	Logger logger = Logger.getGlobal();

	public RTPricingService(HashMap<String, PriceObserver> symbolPrice, List<String> masterSymbolList,
			Queue<PriceObserver> priceWriterQueue) {
		super();
		this.symbolPrice = symbolPrice;
		this.masterSymbolList = masterSymbolList;
		this.priceWriterQueue = priceWriterQueue;
	}

	public void startPricingFeed() {
		for (String symbol : masterSymbolList) {
			logger.info("Symbol " + symbol + " added");
			Price price = new Price();
			price.setSymbol(symbol);
			PriceObserver priceObserver = new PriceObserver(symbol);
			priceObserver.setPrice(price);
			priceObserver.initializeWriter();
			symbolPrice.put(symbol, priceObserver);
		}
		RTConsumer consumer = new RTConsumer(symbolPrice, priceWriterQueue);
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();
		Thread realtimePriceWriter = new Thread(new RealtimePriceWriter(priceWriterQueue));
		realtimePriceWriter.start();
	}

	public HashMap<String, PriceObserver> getSymbolPrice() {
		return symbolPrice;
	}

	public void setSymbolPrice(HashMap<String, PriceObserver> symbolPrice) {
		this.symbolPrice = symbolPrice;
	}

}
