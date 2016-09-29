package com.logos.PricingService.RealTimeService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.Price;
import com.logos.PricingService.Pojo.PriceObserver;

public class RealtimePriceWriter implements Runnable {
	Queue<PriceObserver> priceWriterQueue;
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	Logger logger = Logger.getGlobal();

	public RealtimePriceWriter(Queue<PriceObserver> priceWriterQueue) {
		super();
		this.priceWriterQueue = priceWriterQueue;
	}

	@Override
	public void run() {
		try {
			FileWriter rtfileWriter;
			FileWriter pbfileWriter;
			PriceObserver priceObserver;
			Price price;

			int curr_min = 0;
			int prev_min = 0;

			while (true) {
				if (!priceWriterQueue.isEmpty()) {
					priceObserver = priceWriterQueue.remove();
					price = priceObserver.getPrice();
					// logger.info("Dequing teh price object tp write to file
					// ..." + price.toString());
					rtfileWriter = priceObserver.getRtfileWriter();
					pbfileWriter = priceObserver.getPbfileWriter();
					rtfileWriter.append(price.getSymbol());
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(String.valueOf(price.getBid()));
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(String.valueOf(price.getAsk()));
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(String.valueOf(price.getLast()));
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(String.valueOf(price.getVol()));
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(String.valueOf(price.getDate()));
					rtfileWriter.append(COMMA_DELIMITER);
					rtfileWriter.append(price.getTime());
					rtfileWriter.append(NEW_LINE_SEPARATOR);
					rtfileWriter.flush();

					curr_min = Integer.parseInt(price.getTime().split(":")[1]);
					prev_min = priceObserver.getPrev_min();
					if (curr_min != prev_min && (curr_min - prev_min) % 2 == 0) {
						priceObserver.setPrev_min(curr_min);
						// logger.info("Writing the Price bar to file ..." +
						// price.getSymbol());

						pbfileWriter.append(price.getSymbol());
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getOpen()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getHigh()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getLow()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getClose()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getVol()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(String.valueOf(price.getDate()));
						pbfileWriter.append(COMMA_DELIMITER);
						pbfileWriter.append(price.getTime());
						pbfileWriter.append(NEW_LINE_SEPARATOR);
						pbfileWriter.flush();
						price.setOpen(price.getClose());
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

}
