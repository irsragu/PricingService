package com.logos.PricingService.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.logos.PricingService.Pojo.HistoricalPrice;

public class CSVtoHistoricalPrice {
	Logger logger = Logger.getGlobal();
	String startDate;
	String endDate;
	String symbol;
	String type;

	public CSVtoHistoricalPrice(String startDate, String endDate, String symbol, String type) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.symbol = symbol;
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<HistoricalPrice> getHistoricalPrices() {
		List<HistoricalPrice> symbolHistPriceList;
		String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
		ReadUserConfig rx = new ReadUserConfig(fXmlFile);
		String fileName;
		String query;
		File csvFile;

		if (type.equalsIgnoreCase("daily")) {
			fileName = "Daily_Historical.csv";
			csvFile = new File(rx.getFtpLocalDirectory() + fileName);
			query = "SELECT RIC,\"Trade Date\",\"Open Price\",\"High Price\",\"Low Price\",\"Universal Close Price\",Volume FROM  "
					+ csvFile.getName().split(".csv")[0] + " WHERE RIC = '" + symbol + "' "
					+ " AND \"Trade Date\" between '" + startDate + "' and '" + endDate + "'";
		} else {
			fileName = symbol + "_pb.csv";
			csvFile = new File(rx.getFtpLocalDirectory() + fileName);
			query = "SELECT Symbol,Date,Open,High,Low,Close,Volume,Time FROM  " + csvFile.getName().split(".csv")[0]
					+ " WHERE Symbol = '" + symbol + "' " + " AND Date between '" + startDate + "' and '" + endDate
					+ "'";
		}
		logger.info(query);
		symbolHistPriceList = queryCsv(query, type, csvFile);

		return symbolHistPriceList;
	}

	private List<HistoricalPrice> queryCsv(String query, String type, File csvFile) {
		// Load the driver.
		ResultSet results = null;
		List<HistoricalPrice> symbolHistPriceList = new ArrayList<HistoricalPrice>();
		try {
			Class.forName("org.relique.jdbc.csv.CsvDriver");

			// Create a connection. The first command line parameter is
			// the directory containing the .csv files.
			// A single connection is thread-safe for use by several threads.
			Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + csvFile.getParent());

			// Create a Statement object to execute the query with.
			// A Statement is not thread-safe.
			Statement stmt = conn.createStatement();

			// Select the ID and NAME columns from sample.csv
			// String query = "SELECT Symbol,Date,Open,High,Low,Close,Volume
			// FROM " + csvFile.getName().split(".csv")[0]
			// + " WHERE Symbol = '" + symbol + "' " + " AND Date between '" +
			// startDate + "' and '" + endDate
			// + "'";
			results = stmt.executeQuery(query);
			if (type.equalsIgnoreCase("daily")) {
				while (results.next()) {
					HistoricalPrice historicalPrice = new HistoricalPrice();
					historicalPrice.setRic(results.getString("RIC"));
					historicalPrice.setOpen(results.getString("Open Price"));
					historicalPrice.setHigh(results.getString("High Price"));
					historicalPrice.setLow(results.getString("Low Price"));
					historicalPrice.setClose(results.getString("Universal Close Price"));
					historicalPrice.setCloseDate(results.getString("Trade Date"));
					historicalPrice.setVol(results.getString("Volume"));
					symbolHistPriceList.add(historicalPrice);
					logger.info(historicalPrice.toString());

					System.out.println(results.getString(1));
					System.out.println(results.getString(2));
					System.out.println(results.getString(3));
					System.out.println(results.getString(4));
					System.out.println(results.getString(5));
					System.out.println(results.getString(6));
					System.out.println(results.getString(7));

				}
			} else {
				while (results.next()) {
					HistoricalPrice historicalPrice = new HistoricalPrice();
					historicalPrice.setRic(results.getString("Symbol"));
					historicalPrice.setOpen(results.getString("Open"));
					historicalPrice.setHigh(results.getString("High"));
					historicalPrice.setLow(results.getString("Low"));
					historicalPrice.setClose(results.getString("Close"));
					historicalPrice.setCloseDate(results.getString("Date"));
					historicalPrice.setTime(results.getString("Time"));
					historicalPrice.setVol(results.getString("Volume"));
					symbolHistPriceList.add(historicalPrice);
					logger.info(historicalPrice.toString());

					System.out.println(results.getString(1));
					System.out.println(results.getString(2));
					System.out.println(results.getString(3));
					System.out.println(results.getString(4));
					System.out.println(results.getString(5));
					System.out.println(results.getString(6));
					System.out.println(results.getString(7));
					System.out.println(results.getString(8));
				}
			}

			results.close();
			conn.close();
		} catch (

		ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return symbolHistPriceList;
	}

	// public static void main(String[] args) {
	// CSVtoHistoricalPrice csvFile = new CSVtoHistoricalPrice("2006-03-28",
	// "2007-03-30", "GBPJPY=R", "daily");
	// csvFile.getHistoricalPrices();
	// }

}
