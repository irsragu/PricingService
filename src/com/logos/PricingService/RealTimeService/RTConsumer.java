
///*|----------------------------------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      	--
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  					--
// *|           Copyright Thomson Reuters 2016. All rights reserved.            		--
///*|----------------------------------------------------------------------------------------------------

package com.logos.PricingService.RealTimeService;

import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.PriceObserver;
import com.logos.PricingService.Reuters.ReutersClient;
import com.thomsonreuters.ema.access.ElementList;
import com.thomsonreuters.ema.access.EmaFactory;
import com.thomsonreuters.ema.access.OmmArray;
import com.thomsonreuters.ema.access.OmmConsumer;
import com.thomsonreuters.ema.access.OmmConsumerConfig;
import com.thomsonreuters.ema.access.OmmException;
import com.thomsonreuters.ema.rdm.EmaRdm;

public class RTConsumer implements Runnable {
	HashMap<String, PriceObserver> symbolPriceMap;
	Queue<PriceObserver> priceWriterQueue;
	Logger logger = Logger.getGlobal();

	public RTConsumer(HashMap<String, PriceObserver> symbolPriceMap, Queue<PriceObserver> priceWriterQueue) {
		super();
		this.symbolPriceMap = symbolPriceMap;
		this.priceWriterQueue = priceWriterQueue;
	}

	@Override
	public void run() {
		OmmConsumer consumer = null;
		try {
			logger.info("Getting price feed for .............. " + symbolPriceMap.size());
			ReutersClient appClient = new ReutersClient(symbolPriceMap, priceWriterQueue);
			OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig();
			// consumer =
			// EmaFactory.createOmmConsumer(EmaFactory.createOmmConsumerConfig().consumerName("Consumer_2").username("HK8_03_RHB_SWVPNTRIAL03"));
			consumer = EmaFactory
					.createOmmConsumer(config.host("159.220.108.145:14002").username("HK8_03_RHB_SWVPNTRIAL03"));
			ElementList batch = EmaFactory.createElementList();
			OmmArray array = EmaFactory.createOmmArray();

			for (String symbol : symbolPriceMap.keySet()) {
				array.add(EmaFactory.createOmmArrayEntry().ascii(symbol));
			}
			batch.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_BATCH_ITEM_LIST, array));
			consumer.registerClient(EmaFactory.createReqMsg().serviceName("hEDD").payload(batch), appClient);
		} catch (OmmException excp) {
			logger.severe(excp.getMessage());
		}
	}

	// @Override
	// public void run() {
	// MockPricingService mps = new MockPricingService(symbolPriceMap,
	// priceWriterQueue);
	// mps.startFeed();
	// }

}
