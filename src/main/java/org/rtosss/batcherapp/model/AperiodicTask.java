package org.rtosss.batcherapp.model;

public class AperiodicTask extends Task {
	private Integer arrivalTime;
	
	public AperiodicTask(String name, TaskCode taskCode, String params) {
		super(name, taskCode, params);
		this.arrivalTime = null;
	}
	
	public AperiodicTask(String name, TaskCode taskCode, String params, String arrivalTime) {
		super(name, taskCode, params);
		this.arrivalTime = Integer.parseUnsignedInt(arrivalTime);
	}

	public String getArrivalTime() {
		if(arrivalTime == null) {
			return "Dynamic";
		}
		return Integer.toUnsignedString(arrivalTime);
	}

	@Override
	public String addTask() {
		String command = this.getBaseCommand();
		if(arrivalTime == null) {
			command = "add_task " + command;
		} else {
			command = "add_task_later " + command + " " + Integer.toUnsignedString(arrivalTime);
		}
		return command;
	}
}
