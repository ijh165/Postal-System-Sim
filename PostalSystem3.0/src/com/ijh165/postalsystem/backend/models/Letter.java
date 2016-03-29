package com.ijh165.postalsystem.backend.models;

public class Letter extends Deliverable {
	private String returnRecipient;

	public String getReturnRecipient() {
		return returnRecipient;
	}

	public void setReturnRecipient(String returnRecipient) {
		this.returnRecipient = returnRecipient;
	}
}
