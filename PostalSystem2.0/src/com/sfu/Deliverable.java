package com.sfu;

public class Deliverable {
	private Office iniatingOffice;
	private Office destOffice;
	private String intendedDest;
	private String recipient;
	private int initDay;

	public Office getIniatingOffice() {
		return iniatingOffice;
	}
	public void setIniatingOffice(Office iniatingOffice) {
		this.iniatingOffice = iniatingOffice;
	}
	public Office getDestOffice() {
		return destOffice;
	}
	public void setDestOffice(Office destOffice) {
		this.destOffice = destOffice;
	}
	public String getIntendedDest() {
		return intendedDest;
	}
	public void setIntendedDest(String intendedDest) {
		this.intendedDest = intendedDest;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public int getInitDay() {
		return initDay;
	}
	public void setInitDay(int initDay) {
		this.initDay = initDay;
	}
}
