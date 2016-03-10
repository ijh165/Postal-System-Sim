package com.sfu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sfu.Logging.LogType;

public class Office {
	private String name;
	private int transitTime;
	private int requiredPostage;
	private int capacity;
	private int persuasionAmount;
	private int maxPackageLength;
	private List<Deliverable> toMail = new ArrayList<>();
	private List<Deliverable> toPickUp = new ArrayList<>();
	private Set<String> criminalSet;
	private Network network;



	public Office(String name, int transitTime, int requiredPostage,
				int capacity, int persuasionAmount, int maxPackageLength) {
		super();
		this.name = name;
		this.transitTime = transitTime;
		this.requiredPostage = requiredPostage;
		this.capacity = capacity;
		this.persuasionAmount = persuasionAmount;
		this.maxPackageLength = maxPackageLength;
	}

	//getters and setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTransitTime() {
		return transitTime;
	}
	public void setTransitTime(int transitTime) {
		this.transitTime = transitTime;
	}
	public int getRequiredPostage() {
		return requiredPostage;
	}
	public void setRequiredPostage(int requiredPostage) {
		this.requiredPostage = requiredPostage;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public int getPersuasionAmount() {
		return persuasionAmount;
	}
	public void setPersuasionAmount(int persuasionAmount) {
		this.persuasionAmount = persuasionAmount;
	}
	public int getMaxPackageLength() {
		return maxPackageLength;
	}
	public void setMaxPackageLength(int maxPackageLength) {
		this.maxPackageLength = maxPackageLength;
	}
	public Set<String> getCriminalSet() {
		return criminalSet;
	}
	public void setCriminalSet(Set<String> criminalSet) {
		this.criminalSet = criminalSet;
	}
	public Network getNetwork() {
		return network;
	}
	public void setNetwork(Network network) {
		this.network = network;
	}

	/*public void acceptLetterIfGood(Letter letter) {
		boolean hasCriminalRecipient = criminalSet.contains(letter.getRecipient());
		boolean officeFull = isFull();
		Office destOffice = letter.getDestOffice();
		if (destOffice != null && !hasCriminalRecipient && !officeFull) {
			accept(letter);
		} else {
			Logging.rejectDeliverable(LogType.MASTER, letter);
			Logging.rejectDeliverable(LogType.OFFICE, letter);
		}
	}*/

	//Receive deliverable
	public void accept(Deliverable d) {
		Logging.deliverableAccepted(LogType.OFFICE, d);
		toMail.add(d);
	}

	//Receive deliverable without logging
	public void acceptWithoutLogging(Deliverable d) {
		toMail.add(d);
	}

	//Receive from network
	public void receiveFromNetwork(Deliverable d) {
		if (d instanceof Package) {
			Package p = (Package) d;
			if (this.maxPackageLength < p.getLength()) {
				Logging.deliverableDestroyed(LogType.MASTER, d);
				Logging.deliverableDestroyed(LogType.OFFICE, d);
				return;
			}
		}

		if (isFull()) {
			Logging.deliverableDestroyed(LogType.MASTER, d);
			Logging.deliverableDestroyed(LogType.OFFICE, d);
			return;
		}

		toPickUp.add(d);
	}

	//Remove deliverables to be picked up longer than 14 days
	public void drop(int day) {
		for (int idx = 0; idx < toPickUp.size(); idx++) {
			Deliverable d = toPickUp.get(idx);
			if (day-(d.getInitDay()+d.getIniatingOffice().getTransitTime()) >= 14) {
				toPickUp.remove(d);
				if(d instanceof Package) {
					//directly destroy for package
					Logging.deliverableDestroyed(LogType.MASTER, d);
					Logging.deliverableDestroyed(LogType.OFFICE, d);
				} else {
					//for letter...
					Letter l = (Letter) d;
					if(l.getReturnRecipient().equals("NONE")) {
						//directly destroy letter if don't have return address
						Logging.deliverableDestroyed(LogType.MASTER, d);
						Logging.deliverableDestroyed(LogType.OFFICE, d);
					} else {
						//return letter if have return address

						Letter letter = new Letter();
						letter.setIniatingOffice(l.getDestOffice());
						letter.setDestOffice(l.getIniatingOffice());
						letter.setInitDay(day);
						letter.setRecipient(l.getReturnRecipient());
						letter.setReturnRecipient("NONE");
						letter.setIntendedDest(l.getIniatingOffice().getName());

						Logging.newDeliverable(LogType.OFFICE, letter);

						boolean hasCriminalRecipient = criminalSet.contains(letter.getRecipient());
						boolean officeFull = isFull();
						if (letter.getDestOffice() != null && !hasCriminalRecipient && !officeFull) {
							accept(letter);
						} else {
							Logging.rejectDeliverable(LogType.MASTER, letter);
							Logging.rejectDeliverable(LogType.OFFICE, letter);
						}
					}
				}
				idx = -1;
			}
		}
	}

	//Send all deliverables to network
	public void sendToNetwork() {
		for (int idx = toMail.size()-1; idx >= 0; idx--) {
			Deliverable d = toMail.get(idx);
			toMail.remove(idx);
			network.put(d);
			Logging.transitSent(LogType.OFFICE, d);

		}
	}

	//Pickup a deliverable
	public Deliverable pickUp(String recipient, int day) {
		int size = toPickUp.size();
		for (int idx = size-1 ; idx >= 0 ; idx--) {
			Deliverable d = toPickUp.get(idx);
			if (recipient.equals(d.getRecipient())) {
				toPickUp.remove(idx);
				Logging.itemComplete(LogType.OFFICE, d, day);
				return d;
			}
		}
		return null;
	}

	public boolean isFull() {
		return (this.toMail.size() + this.toPickUp.size()) >= capacity;
	}

	public boolean isEmpty() {
		return (this.toMail.size() + this.toPickUp.size()) == 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Office) && (this.name.equals(((Office)obj).getName()));
	}

	@Override
 	public String toString() {
 		return this.name;
 	}
}
