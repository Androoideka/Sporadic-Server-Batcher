package org.rtosss.batcherapp.model;

public class TaskInstance {
	private Task task;
	private String handle;
	private Integer arrivalTime;
	
	public TaskInstance(Task task, String handle, String arrivalTime) {
		this.task = task;
		this.handle = handle;
		this.arrivalTime = Integer.parseUnsignedInt(arrivalTime);
	}
	
	public TaskInstance(Task task, String response) {
		this.task = task;
		String[] attributes = response.split(",");
		String[] handleAttr = attributes[0].split(": ");
		String handle = handleAttr[1];
		String[] arrivalAttr = attributes[1].split(": ");
		String arrivalTime = arrivalAttr[1];
		this.handle = handle;
		this.arrivalTime = Integer.parseUnsignedInt(arrivalTime);
	}
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public Integer getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = Integer.parseUnsignedInt(arrivalTime);
	}
	public String deleteTask() {
		return "stop_task " + handle;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((handle == null) ? 0 : handle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskInstance other = (TaskInstance) obj;
		if (handle == null) {
			if (other.handle != null)
				return false;
		} else if (!handle.equals(other.handle))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instance " + handle + " of " + task.toString();
	}
}
