package org.rtosss.batcherapp.model;

public class AperiodicTask extends Task {
	private int arrivalTime;
	
	public AperiodicTask(String name, TaskCode taskCode, String params, String arrivalTime) {
		super(name, taskCode, params);
		this.arrivalTime = Integer.parseUnsignedInt(arrivalTime);
	}

	public String getArrivalTime() {
		return Integer.toUnsignedString(arrivalTime);
	}
}
