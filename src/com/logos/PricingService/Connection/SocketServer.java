package com.logos.PricingService.Connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.logos.PricingService.Pojo.HistoricalPrice;
import com.logos.PricingService.Pojo.Price;
import com.logos.PricingService.Pojo.PriceObserver;
import com.logos.PricingService.Pojo.User;
import com.logos.PricingService.RealTimeService.RTPricingService;
import com.logos.PricingService.util.CSVtoHistoricalPrice;
import com.logos.PricingService.util.FileLogger;
import com.logos.PricingService.util.GenerateHistoricalFile;
import com.logos.PricingService.util.GenerateOnStartUp;
import com.logos.PricingService.util.ReadUserConfig;
import com.logos.PricingService.util.TimeScheduler;

/*
 * Socket Server class that catches the user request and sends back an appropriate response.
 * This class starts the Reuters pricing feed capture.
 */
public class SocketServer {
	private ServerSocket serverSocket;
	// A global HashMap of symbol and the associated PriceObserver
	HashMap<String, PriceObserver> symbolPriceObserver;
	// A global HashMap of userID to User Object
	HashMap<String, User> userIdMap;
	// A global Queue to hold prices that need to be written to file - both
	// realtime and price bar
	Queue<PriceObserver> priceWriterQueue;

	String userConfigFile;
	Logger logger = Logger.getGlobal();

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public SocketServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public void startSocketService() {
		// Initiate a global File logger to log Info-Severe from all classes.
		FileLogger.initialize();

		// Create a new HashMap that holds the Symbol and PriceObserver
		symbolPriceObserver = new HashMap<String, PriceObserver>();
		// Create the new Queue
		priceWriterQueue = new ConcurrentLinkedQueue<PriceObserver>();
		// Reads the user configs from XML file
		String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
		ReadUserConfig rx = new ReadUserConfig(fXmlFile);
		// Create a List to hold the master symbol list
		List<String> masterSymbolList = new ArrayList<String>();

		masterSymbolList = rx.getMasterSymbolList();
		logger.info("Master List");

		// The heart.. start the pricing service which gets the reuters live
		// price for the symbols in the master list
		RTPricingService pricingService = new RTPricingService(symbolPriceObserver, masterSymbolList, priceWriterQueue);
		pricingService.startPricingFeed();

		//Get Historical File on Startup
		GenerateOnStartUp generateOnStartUp = new GenerateOnStartUp();
		new Thread(generateOnStartUp).start();
		
		// Populate the Historical price at 6PM EST everyday
		TimeScheduler timeScheduler = new TimeScheduler();
		timeScheduler.startTask();

		// Create the new HashMap that holds the UserId to User Object map. This
		// is needed to have a reference to all users and to access the state
		// elements of teh user.
		userIdMap = new HashMap<String, User>();

		logger.info("Waiting for client on port " + serverSocket.getLocalPort() + "...");

		try {
			// An endless loop to handle socket requests.
			while (true) {
				logger.info("In the while loop ....");
				// Wait for the Socket input from the terminal
				Socket socket = serverSocket.accept();
				logger.info("Just connected to " + socket.getRemoteSocketAddress());

				// Create an input stream for the socket. We get info here from
				// the terminal for a user.
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

				// We receive Object array.
				Object[] inputMessage = (Object[]) in.readObject();
				String userID;
				logger.info("Input message ................................................... " + inputMessage);
				if (inputMessage != null) {
					// Determine the ACTION, which is the first element in the
					// array - don't break the protocol.
					String action = (String) inputMessage[0];
					// If the ACTION is LOGIN
					if (action.equalsIgnoreCase("login")) {
						// Get the user ID, which should be the second element
						// in the object list
						userID = (String) inputMessage[1];
						// Get the associated password from the XML config file
						String password = rx.getPassword(userID);
						if (password == null) {
							logger.info("User not Valid .....");
							// if password is null then the user does not
							// exists.. so send back the message
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject("User not valid");
							out.flush();
							out.close();
							// break;
							continue;
						} else {
							// the password is AES encrypted. DEcrypt it and
							// check
							byte[] encryptedPasswordBytes = (byte[]) inputMessage[2];

							String passphrase = "1234567812345678";
							byte[] passphraseByte = passphrase.getBytes();
							SecretKeySpec secret = new SecretKeySpec(passphraseByte, "AES");
							Cipher cipher = Cipher.getInstance("AES");

							cipher.init(Cipher.DECRYPT_MODE, secret);
							byte[] decryptedPasswordByte = cipher.doFinal(encryptedPasswordBytes);
							String decryptedPassword = new String(decryptedPasswordByte);
							if (!decryptedPassword.equals(password)) {
								// if password does not match send back the
								// message
								logger.info("Password Invalid");
								ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
								out.writeObject("Password Invalid");
								out.flush();
								out.close();
								// break;
								continue;
							}
							// out.writeObject("User OK");
						}
						logger.info("User :" + userID + " logged in");
						// User and password is valid, so proceed

						// need a List symbol the user is entitled to get prices
						// for
						List<String> userEntitledSymbolList = rx.getSymbols(userID);
						// Create a List of PriceObserver object that the user
						// is entitled for
						List<PriceObserver> userEntitledPriceList = new ArrayList<PriceObserver>();
						// Populate this List with actual PriceObserver objects
						// from the master list
						for (String userEntitledPrice : userEntitledSymbolList) {
							userEntitledPriceList.add(symbolPriceObserver.get(userEntitledPrice));
						}

						// Create the user.
						User user = new User(userID, socket, userEntitledPriceList);
						if (!userIdMap.containsKey(userID)) {
							// If userID map already has a entry then the user
							// is logged in. So ignore.
							userIdMap.put(userID, user);
							new Thread(user).start();
						}
					} else if (action.equalsIgnoreCase("logout")) {
						logger.info("In logout ===============================================");
						userID = (String) inputMessage[1];
						// For logout, get the user object reference and then
						// stop the price feed. This saves network trafiic
						if (userIdMap.containsKey(userID)) {
							User logoutUser = userIdMap.get(userID);
							for (PriceObserver priceObserver : logoutUser.getPriceObservers()) {
								logger.info("***************************De register the price : "
										+ priceObserver.getPrice().getSymbol() + "for User: "
										+ logoutUser.getUserName());
								priceObserver.unregister(logoutUser);
							}
							logoutUser.getSocket().close();
							userIdMap.remove(userID);
						}
					} else if (action.equalsIgnoreCase("add")) {
						logger.info("In Add Symbol ===============================================");
						userID = (String) inputMessage[1];
						// User is requesting to add new symbol to his
						// entitlement
						if (userIdMap.containsKey(userID)) {
							User addSymbolUser = userIdMap.get(userID);
							// The third object contains a list of the symbols
							// to be added
							String[] addSymbols = ((String) inputMessage[2]).split(";");
							for (String newSymbol : addSymbols) {
								// Only if the symbol is in the master list, we
								// get the prices
								if (symbolPriceObserver.containsKey(newSymbol)) {
									// so add it to the PriceObserver
									addSymbolUser.setPriceToUser(symbolPriceObserver.get(newSymbol));
								} else {
									// Else it is not yet being published to any
									// user, so add it master list and then
									// publish for this user.
									PriceObserver priceObserver = new PriceObserver(newSymbol);
									Price p = new Price();
									p.setSymbol(newSymbol);
									priceObserver.setPrice(p);
									symbolPriceObserver.put(newSymbol, priceObserver);
									addSymbolUser.setPriceToUser(priceObserver);
									logger.info("Creating PRice for the forst time .....");
								}
								logger.info("Adding symbol : " + newSymbol + " for user : " + userID);
							}
						}
					} else if (action.equalsIgnoreCase("historical")) {
						logger.info("In Historial Price ===============================================");
						userID = (String) inputMessage[1];
						String symbol = (String) inputMessage[2];
						String startDate = (String) inputMessage[3];
						String endDate = (String) inputMessage[4];
						if (endDate.isEmpty() || endDate == null) {
							Date today = new Date();
							endDate = new SimpleDateFormat("yyyy-MM-dd").format(today);
						}
						String historicalType = (String) inputMessage[5];
						List<HistoricalPrice> symbolHistPriceList = new ArrayList<HistoricalPrice>();
						CSVtoHistoricalPrice csvFile = new CSVtoHistoricalPrice(startDate, endDate, symbol,
								historicalType);
						symbolHistPriceList = csvFile.getHistoricalPrices();

						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						if (symbolHistPriceList.isEmpty()) {
							String msg = "No Historical data available for " + symbol + " and between " + startDate
									+ " and " + endDate;
							out.writeObject(msg);
						} else
							out.writeObject(symbolHistPriceList);
					}
				}

			}
		} catch (

		SocketTimeoutException s) {
			logger.severe("Socket timed out!");
			logger.log(Level.SEVERE, "", s);
		} catch (IOException e) {
			logger.severe(e.toString());
			logger.log(Level.SEVERE, "", e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 1234;
		try {
			SocketServer socketServer = new SocketServer(port);
			socketServer.startSocketService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}