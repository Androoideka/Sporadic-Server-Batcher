package org.rtosss.batcherapp.model;

public abstract class Task {
	protected String name;
	protected TaskCode taskCode;
	protected String params;
	
	protected Task(String name, TaskCode taskCode, String params) {
		this.name = name;
		this.taskCode = taskCode;
		if(params.isBlank()) {
			this.params = "NULL";
		} else {
			this.params = params;
		}
	}
	
	public String getName() {
		return name;
	}
	public String getFunc() {
		return taskCode.getMethodName() + "(" + params + ")";
	}
	public Integer getComputationTime() {
		return taskCode.getComputationTime();
	}
	public TaskCode getTaskCode() {
		return taskCode;
	}
	public String getParams() {
		return params;
	}
	protected String getBaseCommand() {
		return name + " " + taskCode.getMethodName() + " " + params;
	}
	public abstract String addTask();
	
	@Override
	public String toString() {
		return "Task " + name;
	}
	
}
