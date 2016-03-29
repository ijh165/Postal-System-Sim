package com.ijh165.postalsystem.backend.models;

import com.ijh165.postalsystem.util.Logging;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ijh165.postalsystem.util.Logging.LogType;

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

	//Receive deliverable
	public void accept(Deliverable d) {
		Logging.deliverableAccepted(LogType.OFFICE, d);
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

	//Remove deliverables unpicked up for longer than 14 days
	public void dropUnpickedUp(int day) {
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

	//Pickup a deliverable, return list of picked up deliverables (which is empty if pickup fails)
	public List<Deliverable> pickUp(String recipient, int day) {
		List<Deliverable> pickedUpDeliverableList = new ArrayList<>();
		int size = toPickUp.size();
		for (int idx = size-1 ; idx >= 0 ; idx--) {
			Deliverable d = toPickUp.get(idx);
			if (recipient.equals(d.getRecipient()) && d.isAvailableForPickUp()) {
				d.resetDaysDelayed();
				toPickUp.remove(idx);
				Logging.itemComplete(LogType.OFFICE, d, day);
				pickedUpDeliverableList.add(d);
			}
		}
		return pickedUpDeliverableList;
	}

	//delay deliverable with the specified recipient and days delayed, return true upon success
	public boolean delayDeliverable(String recipient, int daysDelayed) {
		boolean success = false;
		for (Deliverable d : toPickUp) {
			if (d.getRecipient().equals(recipient)) {
				d.setAvailableForPickUp(false);
				d.delay(daysDelayed);
				success = true;
			}
		}
		return success;
	}

	//update pickup availability of deliverables if delay is over
	public void updatePickUpAvailability(int day) {
		for (Deliverable d : toPickUp) {
			if (!d.isAvailableForPickUp() &&
				day >= d.getInitDay() + d.getIniatingOffice().getTransitTime() + d.getDaysDelayed() + 1)
			{
				//make it available for pickup
				d.setAvailableForPickUp(true);
				//re-log arriving deliverable
				Logging.transitArrived(LogType.OFFICE, d);
			}
		}
	}

	//add the amount of currently stored mail in each office to the postage and persuasion costs
	public void inflation() {
		int incrAmount = toMail.size() + toPickUp.size();
		requiredPostage += incrAmount;
		persuasionAmount += incrAmount;
	}

	//subtract the amount of currently stored mail in each office to the postage and persuasion costs (costs cannot go below zero)
	public void deflation() {
		int decrAmount = toMail.size() + toPickUp.size();
		requiredPostage = (requiredPostage-decrAmount)>0 ? (requiredPostage-decrAmount):0 ;
		persuasionAmount = (persuasionAmount-decrAmount)>0 ? (persuasionAmount-decrAmount):0;
	}

	//destroy all letters awaiting pickup
	public void destroyLettersAwaitingPickup() {
		for (int idx = toPickUp.size()-1; idx >= 0; idx--) {
			Deliverable d = toPickUp.get(idx);
			if (d instanceof Letter) {
				toPickUp.remove(idx);
				Logging.deliverableDestroyed(LogType.MASTER, d);
				Logging.deliverableDestroyed(LogType.OFFICE, d);
			}
		}
	}

	//destroy all packages awaiting pickup
	public void destroyPackagesAwaitingPickup() {
		for (int idx = toPickUp.size()-1; idx >= 0; idx--) {
			Deliverable d = toPickUp.get(idx);
			if (d instanceof Package) {
				toPickUp.remove(idx);
				Logging.deliverableDestroyed(LogType.MASTER, d);
				Logging.deliverableDestroyed(LogType.OFFICE, d);
			}
		}
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
