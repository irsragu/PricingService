package com.logos.PricingService.Pojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.logos.PricingService.util.ReadUserConfig;

public class PriceObserver {
	String symbol;
	List<User> users = new ArrayList<User>();
	private final Object MUTEX = new Object();
	private boolean changed;
	Price price;
	int curr_min;
	int prev_min;

	private static final String RT_FILE_HEADER = "Symbol,Bid,Ask,Last,Volume,Date,Time";
	private static final String PB_FILE_HEADER = "Symbol,Open,High,Low,Close,Volume,Date,Time";
	private static final String NEW_LINE_SEPARATOR = "\n";
	File realtimePriceFile;
	FileWriter rtfileWriter;
	File pricebarFile;
	FileWriter pbfileWriter;
	Logger logger = Logger.getGlobal();

	public PriceObserver(String symbol) {
		this.symbol = symbol;
		prev_min = 0;
	}

	public String getSymbol() {
		return symbol;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public void initializeWriter() {
		try {
			String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
			ReadUserConfig rx = new ReadUserConfig(fXmlFile);
			realtimePriceFile = new File(rx.getRealtimeDirectory() + symbol + "_rt.csv");
			rtfileWriter = new FileWriter(realtimePriceFile, true);
			rtfileWriter.append(RT_FILE_HEADER.toString());
			rtfileWriter.append(NEW_LINE_SEPARATOR);
			rtfileWriter.flush();

			pricebarFile = new File(rx.getPricebarDirectory() + symbol + "_pb.csv");
			pbfileWriter = new FileWriter(pricebarFile, true);
			pbfileWriter.append(PB_FILE_HEADER.toString());
			pbfileWriter.append(NEW_LINE_SEPARATOR);
			pbfileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void register(User user) {
		if (user != null) {
			synchronized (MUTEX) {
				if (!users.contains(user)) {
					logger.info("Yheeeee users size before " + users.size());
					users.add(user);
					logger.info("Yheeeee users size after " + users.size());
				}
			}
		}
	}

	public void unregister(User user) {
		synchronized (MUTEX) {
			logger.info("Yhe users size before " + users.size());
			users.remove(user);
			logger.info("Yhe users size after " + users.size());
		}
	}

	public void notifyAllUsers() {
		List<User> usersLocal = null;
		// synchronization is used to make sure any Observer registered after
		// message is received is not notified
		synchronized (MUTEX) {
			if (!changed)
				return;
			usersLocal = new ArrayList<User>(this.users);
			this.changed = false;
		}
		for (User user : usersLocal) {
			// logger.info("The user :" + user.userName + "is being
			// updated with price.....");
			user.updatePrice(this);
		}
	}

	public void isUpdated() {
		this.changed = true;
		notifyAllUsers();
	}

	@Override
	public String toString() {
		return "PriceObserver [symbol=" + symbol + ", users=" + users + ", changed=" + changed + ", price=" + price
				+ "]";
	}

	public List<User> getUsers() {
		return users;
	}

	public File getRealtimePriceFile() {
		return realtimePriceFile;
	}

	public void setRealtimePriceFile(File realtimePriceFile) {
		this.realtimePriceFile = realtimePriceFile;
	}

	public FileWriter getRtfileWriter() {
		return rtfileWriter;
	}

	public void setRtfileWriter(FileWriter rtfileWriter) {
		this.rtfileWriter = rtfileWriter;
	}

	public File getPricebarFile() {
		return pricebarFile;
	}

	public void setPricebarFile(File pricebarFile) {
		this.pricebarFile = pricebarFile;
	}

	public FileWriter getPbfileWriter() {
		return pbfileWriter;
	}

	public void setPbfileWriter(FileWriter pbfileWriter) {
		this.pbfileWriter = pbfileWriter;
	}

	public int getCurr_min() {
		return curr_min;
	}

	public void setCurr_min(int curr_min) {
		this.curr_min = curr_min;
	}

	public int getPrev_min() {
		return prev_min;
	}

	public void setPrev_min(int prev_min) {
		this.prev_min = prev_min;
	}
}
