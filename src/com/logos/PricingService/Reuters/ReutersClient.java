package com.logos.PricingService.Reuters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.Price;
import com.logos.PricingService.Pojo.PriceObserver;
import com.thomsonreuters.ema.access.AckMsg;
import com.thomsonreuters.ema.access.Data;
import com.thomsonreuters.ema.access.DataType;
import com.thomsonreuters.ema.access.FieldEntry;
import com.thomsonreuters.ema.access.FieldList;
import com.thomsonreuters.ema.access.GenericMsg;
import com.thomsonreuters.ema.access.Msg;
import com.thomsonreuters.ema.access.OmmConsumerClient;
import com.thomsonreuters.ema.access.OmmConsumerEvent;
import com.thomsonreuters.ema.access.RefreshMsg;
import com.thomsonreuters.ema.access.StatusMsg;
import com.thomsonreuters.ema.access.UpdateMsg;

public class ReutersClient implements OmmConsumerClient {
	HashMap<String, PriceObserver> symbolPrice;
	Queue<PriceObserver> priceWriterQueue;
	Logger logger = Logger.getGlobal();

	public ReutersClient(HashMap<String, PriceObserver> symbolPrice, Queue<PriceObserver> priceWriterQueue) {
		this.symbolPrice = symbolPrice;
		this.priceWriterQueue = priceWriterQueue;
	}

	public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
		String symbol = "";
		logger.fine("Item Name: " + (refreshMsg.hasName() ? symbol = refreshMsg.name() : "<not set>"));
		logger.fine("Service Name: " + (refreshMsg.hasServiceName() ? refreshMsg.serviceName() : "<not set>"));

		logger.fine("Item State: " + refreshMsg.state());

		if (DataType.DataTypes.FIELD_LIST == refreshMsg.payload().dataType()) {
			PriceObserver priceObserver = symbolPrice.get(symbol);
			Price price = priceObserver.getPrice();
			if (symbolPrice.get(symbol) != null) {
				// logger.info(price.toString());
				boolean isUpdated = decode(refreshMsg.payload().fieldList(), price);

				if (isUpdated) {
					priceObserver.isUpdated();
					priceWriterQueue.add(priceObserver);
					// logger.info(price.toString());
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {
		String symbol = "";

		logger.fine("Item Name: " + (updateMsg.hasName() ? symbol = updateMsg.name() : "<not set>"));
		logger.fine("Service Name: " + (updateMsg.hasServiceName() ? updateMsg.serviceName() : "<not set>"));

		if (DataType.DataTypes.FIELD_LIST == updateMsg.payload().dataType()) {
			PriceObserver priceObserver = symbolPrice.get(symbol);
			Price price = priceObserver.getPrice();
			if (price != null) {
				boolean isUpdated = decode(updateMsg.payload().fieldList(), price);

				if (isUpdated) {
					priceObserver.isUpdated();
					priceWriterQueue.add(priceObserver);
					logger.fine(price.toString());
				}

			}
		}
	}

	public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent event) {
		logger.info("Item Name: " + (statusMsg.hasName() ? statusMsg.name() : "<not set>"));
		logger.info("Service Name: " + (statusMsg.hasServiceName() ? statusMsg.serviceName() : "<not set>"));

		if (statusMsg.hasState())
			logger.info("Item State: " + statusMsg.state());
	}

	public void onGenericMsg(GenericMsg genericMsg, OmmConsumerEvent consumerEvent) {
	}

	public void onAckMsg(AckMsg ackMsg, OmmConsumerEvent consumerEvent) {
	}

	public void onAllMsg(Msg msg, OmmConsumerEvent consumerEvent) {
	}

	boolean decode(FieldList fieldList, Price price) {
		boolean isUpdated = false;
		for (FieldEntry fieldEntry : fieldList) {
			String fieldName = fieldEntry.name();
			// logger.info("Field Entry Name: **********************" +
			// fieldName);
			// logger.info("Feild Entry Code: **********************" +
			// fieldEntry.code());
			if (Data.DataCode.BLANK != fieldEntry.code()) {
				if (fieldName.equalsIgnoreCase("bid") || fieldName.equalsIgnoreCase("ask")
						|| fieldName.equalsIgnoreCase("TRDPRC_1") || fieldName.equalsIgnoreCase("acvol_1")
						|| fieldName.equalsIgnoreCase("QUOTIM") || fieldName.equalsIgnoreCase("QUOTE_DATE")) {
					if (fieldName.equalsIgnoreCase("bid")) {
						price.setBid(fieldEntry.real().asDouble());
					} else if (fieldName.equalsIgnoreCase("ask")) {
						price.setAsk(fieldEntry.real().asDouble());
					} else if (fieldName.equalsIgnoreCase("TRDPRC_1")) {
						price.setLast(fieldEntry.real().asDouble());
					} else if (fieldName.equalsIgnoreCase("acvol_1")) {
						price.setVol(fieldEntry.real().asDouble());
					} else if (fieldName.equalsIgnoreCase("QUOTE_DATE")) {

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						@SuppressWarnings("deprecation")
						Date quoteDate = new Date(fieldEntry.date().toString());
						price.setDate(sdf.format(quoteDate));
					} else if (fieldName.equalsIgnoreCase("QUOTIM")) {
						price.setTime(fieldEntry.time().hour() + ":" + fieldEntry.time().minute() + ":"
								+ fieldEntry.time().second() + ":" + fieldEntry.time().millisecond());
					}
					isUpdated = true;
				}
			}
		}
		return isUpdated;
	}
}