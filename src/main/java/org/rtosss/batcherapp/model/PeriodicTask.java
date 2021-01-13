package org.rtosss.batcherapp.model;

public class PeriodicTask extends Task {
	private Integer period;

	public PeriodicTask(String name, TaskCode taskCode, String params, String period) {
		super(name, taskCode, params);
		this.period = Integer.parseUnsignedInt(period);
	}

	public String getPeriod() {
		return Integer.toUnsignedString(period);
	}

	@Override
	public String addTask() {
		return "add_task_periodic " + this.getBaseCommand() + " " + getPeriod();
	}
}
