package org.rtosss.batcherapp.model;

public class PeriodicTask extends Task {
	private int period;

	public PeriodicTask(String name, TaskCode taskCode, String params, String period) {
		super(name, taskCode, params);
		this.period = Integer.parseUnsignedInt(period);
	}

	public String getPeriod() {
		return Integer.toUnsignedString(period);
	}
}
