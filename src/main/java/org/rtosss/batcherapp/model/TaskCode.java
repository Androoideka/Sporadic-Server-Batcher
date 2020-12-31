package org.rtosss.batcherapp.model;

public class TaskCode {
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
		return "TaskCode [name = " + methodName + ", running time = " + Integer.toUnsignedString(computationTime) + " ticks]";
	}
	
}
