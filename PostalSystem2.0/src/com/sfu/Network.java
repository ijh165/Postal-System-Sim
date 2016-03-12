package com.sfu;

import java.util.ArrayList;
import java.util.List;

import com.sfu.Logging.LogType;

public class Network {
	private List<Deliverable> deliverablesInTransit = new ArrayList<>();
	/*private Map<String, Office> officeMap = new HashMap<>();*/

	public void put(Deliverable d) {
		deliverablesInTransit.add(d);
	}

	public void checkAndDeliver(int day) {
		for (int idx = deliverablesInTransit.size()-1 ; idx >= 0 ; idx--) {
			Deliverable d = deliverablesInTransit.get(idx);
			Office initOffice = d.getIniatingOffice();
			if (day >= d.getInitDay() + initOffice.getTransitTime() + d.getDaysDelayed() + 1) {
				d.resetDaysDelayed();
				Office destOffice = d.getDestOffice();
				deliverablesInTransit.remove(idx);
				if (RunCommand.isDestroyedOffice(destOffice)) {
					//if destination office is destroyed...
					if (d instanceof Letter) {
						Letter l = (Letter) d;
						if (!l.getReturnRecipient().equals("NONE")) {
							//return letter without logging if it has return address
							Letter letter = new Letter();
							letter.setIniatingOffice(l.getDestOffice());
							letter.setDestOffice(l.getIniatingOffice());
							letter.setInitDay(day);
							letter.setRecipient(l.getReturnRecipient());
							letter.setReturnRecipient("NONE");
							letter.setIntendedDest(l.getIniatingOffice().getName());
							deliverablesInTransit.add(letter);
						}
					}
				} else {
					//make deliverable available for pickup
					d.setAvailableForPickUp(true);
					//put the deliverable into destination office
					destOffice.receiveFromNetwork(d);
					//log arriving deliverable
					Logging.transitArrived(LogType.OFFICE, d);
				}
			}
		}
	}

	//delay deliverable with the specified recipient and days delayed, return true upon success
	public boolean delayDeliverable(String recipient, int daysDelayed) {
		boolean success = false;
		for (Deliverable d : deliverablesInTransit) {
			if (d.getRecipient().equals(recipient)) {
				d.delay(daysDelayed);
				success = true;
			}
		}
		return success;
	}

	public boolean isNetworkEmpty() {
		return deliverablesInTransit.size() == 0;
	}
}
