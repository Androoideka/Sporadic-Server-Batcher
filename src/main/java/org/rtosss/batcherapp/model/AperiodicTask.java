package org.rtosss.batcherapp.model;

public class AperiodicTask extends Task {
	private int arrivalOffset;
	
	public AperiodicTask(String name, TaskCode taskCode, String params, String arrivalOffset) {
		super(name, taskCode, params);
		this.arrivalOffset = Integer.parseUnsignedInt(arrivalOffset);
	}

	public int getArrivalOffset() {
		return arrivalOffset;
	}
	
	public void setArrivalOffset(int arrivalOffset) {
		this.arrivalOffset = arrivalOffset;
	}

	@Override
	public String addTask() {
		String command = this.getBaseCommand();
		command = "add_task " + command + Integer.toUnsignedString(arrivalOffset);
		return command;
	}
}
