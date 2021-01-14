package org.rtosss.batcherapp.model;

public abstract class Task {
	protected String name;
	protected TaskCode taskCode;
	protected String params;
	protected String handle;
	
	protected Task(String name, TaskCode taskCode, String params) {
		this.name = name;
		this.taskCode = taskCode;
		if(params.isBlank()) {
			this.params = "NULL";
		} else {
			this.params = params;
		}
		this.handle = null;
	}
	
	public String getName() {
		return name;
	}
	public String getFunc() {
		return taskCode.getMethodName() + "(" + params + ")";
	}
	public String getComputationTime() {
		return taskCode.getComputationTime();
	}
	public TaskCode getTaskCode() {
		return taskCode;
	}
	public String getParams() {
		return params;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	protected String getBaseCommand() {
		return name + " " + taskCode.getMethodName() + " " + params;
	}
	public abstract String addTask();
	public String deleteTask() {
		return "remove_task " + handle;
	}
	
	@Override
	public String toString() {
		return "Task " + handle + " - " + name;
	}
	
}
