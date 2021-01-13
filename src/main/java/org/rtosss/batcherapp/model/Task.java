package org.rtosss.batcherapp.model;

public abstract class Task {
	protected String name;
	protected TaskCode taskCode;
	protected String params;
	protected Integer handle;
	
	protected Task(String name, TaskCode taskCode, String params) {
		this.name = name;
		this.taskCode = taskCode;
		if(params.isBlank()) {
			this.params = "NULL";
		} else {
			this.params = params;
		}
		this.handle = 0;
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
		return Integer.toUnsignedString(handle);
	}
	public void setHandle(Integer handle) {
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
		return "Task " + getHandle() + " - " + name;
	}
	
}
