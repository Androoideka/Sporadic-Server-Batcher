package org.rtosss.batcherapp.exceptions;

public class RTOSException extends RuntimeException {
	private ErrorCode errorCode;

	public RTOSException(String message) {
		super(message);
		if(message.equals("Your command wasn't recognized.")) {
			errorCode = ErrorCode.NOTFOUND;
		} else if(message.equals("Cannot execute this command while the scheduler is active.")) {
			errorCode = ErrorCode.SCHEDULER_RUNNING;
		} else if(message.equals("Schedule was not feasible with given set of tasks and server parameters.")) {
			errorCode = ErrorCode.SCHEDULE_NOT_FEASIBLE;
		} else if(message.equals("Couldn't allocate required memory.")) {
			errorCode = ErrorCode.COULD_NOT_ALLOCATE_MEMORY;
		} // The next 3 exceptions shouldn't really appear but they're there for completeness.
		else if(message.equals("Your command wasn't recognized.")) {
			errorCode = ErrorCode.INVALIDCOMMAND;
		} else if(message.equals("Command is missing additional parameters.")) {
			errorCode = ErrorCode.MISSINGPARAM;
		} else if(message.equals("Parameters couldn't be read properly.")) {
			errorCode = ErrorCode.BADINPUT;
		}
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
