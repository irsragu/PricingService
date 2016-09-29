package com.logos.PricingService.Pojo;

import java.io.Serializable;

public class HistoricalPrice implements Serializable {

	/**
	 * 
	 */

	String ric;
	String vol;
	String open;
	String close;
	String high;
	String low;
	String closeDate;
	String time;

	public String getRic() {
		return ric;
	}

	public void setRic(String ric) {
		this.ric = ric;
	}

	public String getVol() {
		return vol;
	}

	public void setVol(String vol) {
		this.vol = vol;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "HistoricalPrice [ric=" + ric + ", vol=" + vol + ", open=" + open + ", close=" + close + ", high=" + high
				+ ", low=" + low + ", closeDate=" + closeDate + ", time=" + time + "]";
	}
}
