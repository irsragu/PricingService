package com.logos.PricingService.Pojo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User implements Runnable {

	ObjectOutputStream out;
	Socket socket;
	List<PriceObserver> pricesObservers;
	String userName;
	Logger logger = Logger.getGlobal();

	public User(String userName, Socket socket, List<PriceObserver> pricesObservers) throws IOException {
		super();
		this.socket = socket;
		this.pricesObservers = pricesObservers;
		this.userName = userName;
		out = new ObjectOutputStream(socket.getOutputStream());
		logger.info("User : " + userName + " is created");
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	public String getUserName() {
		return userName;
	}

	public Socket getSocket() {
		return socket;
	}

	public void updatePrice(PriceObserver priceObserver) {
		// logger.info("In the User Object..... Price is " + price.toString());
		try {
			// logger.info("Dequeueing the price for User: " + userName + "
			// PRice is: " + priceObserver.getPrice().toString());
			out.writeObject(priceObserver.getPrice());
			out.flush();
			out.reset();

		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
			for (PriceObserver pr : pricesObservers) {
				logger.info("***************************De register the price : " + pr.getSymbol() + "for User: "
						+ userName);
				pr.unregister(this);
			}
			try {
				socket.close();
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "", e1);
			}
		}

	}

	@Override
	public void run() {
		logger.info("User: " + userName + " is Registered for the following Price feeds:");
		for (PriceObserver priceObserver : pricesObservers) {
			priceObserver.register(this);
			logger.info(priceObserver.getSymbol());
		}
	}

	public List<PriceObserver> getPriceObservers() {
		return pricesObservers;
	}

	public void setPriceToUser(PriceObserver priceObserver) {
		if (priceObserver != null) {
			if (!pricesObservers.contains(priceObserver)) {
				priceObserver.register(this);
				pricesObservers.add(priceObserver);
			}
		}
	}

}
