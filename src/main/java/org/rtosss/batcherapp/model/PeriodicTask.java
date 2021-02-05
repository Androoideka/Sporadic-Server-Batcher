package org.rtosss.batcherapp.model;

public class PeriodicTask extends Task {
	private int period;

	public PeriodicTask(String name, TaskCode taskCode, String params, String period) {
		super(name, taskCode, params);
		this.period = Integer.parseUnsignedInt(period);
	}

	public int getPeriod() {
		return period;
	}
	
	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public String addTask() {
		return "add_task_periodic " + this.getBaseCommand() + " " + getPeriod();
	}
}
