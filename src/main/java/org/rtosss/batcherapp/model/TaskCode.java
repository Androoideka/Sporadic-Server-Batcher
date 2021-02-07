package org.rtosss.batcherapp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class TaskCode {
	private static ObservableList<TaskCode> functions;
	private static TaskCode statCode;
	private static TaskCode idleCode;
	private final String methodName;
	private final int computationTime;
	
	public TaskCode(String methodName, String computationTime) {
		this.methodName = methodName;
		this.computationTime = Integer.parseUnsignedInt(computationTime);
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public int getComputationTime() {
		return computationTime;
	}
	
	@Override
	public String toString() {
		return methodName + " - " + Integer.toUnsignedString(computationTime) + " ticks";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
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
		TaskCode other = (TaskCode) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

	public static void add(TaskCode taskCode) {
		functions.add(taskCode);
	}
	
	public static ObservableList<TaskCode> getFunctions() {
		if (functions == null) {
			functions = FXCollections.observableArrayList();
			functions.add(new TaskCode("task0", "1"));
			functions.add(new TaskCode("task1", "1"));
			functions.add(new TaskCode("vTask1", "1"));
			functions.add(new TaskCode("vTask2", "2"));
			functions.add(new TaskCode("vTask3", "3"));
			functions.add(new TaskCode("vTask4", "4"));
			functions.add(new TaskCode("vInput", "1"));
		}
		return functions;
	}
	
	public static TaskCode getStatCode() {
		if(statCode == null) {
			statCode = new TaskCode("vWriteStatsTask", "1");
		}
		return statCode;
	}
	
	public static TaskCode getIdleCode() {
		if(idleCode == null) {
			idleCode = new TaskCode("prvIdleTask", "0");
		}
		return idleCode;
	}
	
}
