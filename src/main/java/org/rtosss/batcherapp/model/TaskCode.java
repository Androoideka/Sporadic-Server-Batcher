package org.rtosss.batcherapp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskCode {
	private static ObservableList<TaskCode> functions;
	private final String methodName;
	private final int computationTime;
	
	public TaskCode(String methodName, String computationTime) {
		this.methodName = methodName;
		this.computationTime = Integer.parseUnsignedInt(computationTime);
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	/*public Integer getComputationTime() {
		return computationTime;
	}*/
	
	public String getComputationTime() {
		return Integer.toUnsignedString(computationTime);
	}
	
	@Override
	public String toString() {
		return methodName + " - " + Integer.toUnsignedString(computationTime) + " ticks";
	}
	
	public static void add(TaskCode taskCode) {
		functions.add(taskCode);
	}
	
	public static ObservableList<TaskCode> getFunctions() {
		if (functions == null) {
			functions = FXCollections.observableArrayList();
			functions.add(new TaskCode("vTask0", "1"));
			functions.add(new TaskCode("vTask1", "1"));
		}
		return functions;
	}
	
}
