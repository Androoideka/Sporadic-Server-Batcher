package org.rtosss.batcherapp.exceptions;

public class CustomException extends RuntimeException {
	private ErrorCode errorCode;

	public CustomException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
