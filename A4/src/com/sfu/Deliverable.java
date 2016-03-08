package com.sfu;

public class Deliverable {
	private Office iniatingOffice;
	private Office destOffice;
	private String intendedDest;
	private String recipient;
	private int initDay;
	private int arrivalDay; //my shit


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

	//my shit
	public int getArrivalDay() {
		return arrivalDay;
	}
	public void setArrivalDay(int arrivalDay) {
		this.arrivalDay = arrivalDay;
	}
	//end of my shit
}
