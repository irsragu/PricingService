package com.logos.PricingService.Pojo;

import java.io.Serializable;

public class Price implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String symbol;
	double bid;
	double ask;
	double last;
	double vol;
	double open;
	double close;
	double high;
	double low;
	String time;
	String date;
	private boolean priceBarInitiated;

	public Price() {
		priceBarInitiated = false;
	}

	public void setLast(double last) {
		this.last = last;
		if (priceBarInitiated) {
			setPriceBar();
		} else {
			open = last;
			close = last;
			high = last;
			low = last;
			this.priceBarInitiated = true;
		}
	}

	public void setPriceBar() {
		if (last > high)
			high = last;
		if (last < low)
			low = last;
		close = last;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
		setLast(bid);
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	public double getLast() {
		return last;
	}

	public double getVol() {
		return vol;
	}

	public void setVol(double vol) {
		this.vol = vol;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Price [symbol=" + symbol + ", bid=" + bid + ", ask=" + ask + ", last=" + last + ", vol=" + vol
				+ ", open=" + open + ", close=" + close + ", high=" + high + ", low=" + low + ", time=" + time
				+ ", date=" + date + ", priceBarInitiated=" + priceBarInitiated + "]";
	}
}
