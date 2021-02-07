package org.rtosss.batcherapp.model;

import java.util.ArrayList;
import java.util.List;

public class Batch {
	private List<Task> tasks;
	
	public Batch(List<Task> tasks) {
		this.tasks = new ArrayList<>();
		this.tasks.addAll(tasks);
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
}
