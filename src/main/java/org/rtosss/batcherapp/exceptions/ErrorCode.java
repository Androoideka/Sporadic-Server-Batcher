package org.rtosss.batcherapp.exceptions;

public enum ErrorCode {
	SCHEDULER_RUNNING(-3),
	SCHEDULE_NOT_FEASIBLE(-2),
	SERVER_NOT_FEASIBLE(-6),
	COULD_NOT_ALLOCATE_MEMORY(-1),
	INVALIDCOMMAND(-100),
	MISSINGPARAM(-1000),
	BADINPUT(-1001),
	NOTFOUND(-1002);
	
	private final int code;

	private ErrorCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	//private final Integer code;
	
	
}
