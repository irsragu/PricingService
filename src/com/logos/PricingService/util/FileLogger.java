package com.logos.PricingService.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {
	final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	static FileHandler fileHandler;

	public static void initialize() {
		try {
			InputStream is = FileLogger.class.getResourceAsStream("logging.properties");
			LogManager.getLogManager().readConfiguration(is);
			fileHandler = new FileHandler("PricingService.log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
			logger.addHandler(new ConsoleHandler());
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
