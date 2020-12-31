package org.rtosss.batcherapp.model;

public abstract class Task {
	protected String name;
	protected TaskCode taskCode;
	protected String params;
	protected long handle;
	
	protected Task(String name, TaskCode taskCode, String params) {
		this.name = name;
		this.taskCode = taskCode;
		this.params = params;
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
		return Long.toUnsignedString(handle);
	}
	public void setHandle(String handle) {
		this.handle = Long.parseUnsignedLong(handle);
	}
	
}
