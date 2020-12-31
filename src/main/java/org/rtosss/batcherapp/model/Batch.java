package org.rtosss.batcherapp.model;

import java.util.ArrayList;
import java.util.List;

public class Batch {
	private List<Task> tasks;
	
	public Batch(List<PeriodicTask> periodicTasks, List<AperiodicTask> aperiodicTasks) {
		tasks = new ArrayList<>();
		tasks.addAll(aperiodicTasks);
		tasks.addAll(periodicTasks);
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
}
