package org.rtosss.batcherapp.exceptions;

import org.rtosss.batcherapp.gui.Status;

public class StateException extends RuntimeException {
	private StateException(String message) {
		super(message);
	}
	
	public static StateException factory(Status status) {
		String message = "Acceptable state is: " + status.getError();
		return new StateException(message);
	}
	
	public static StateException factory(Status[] statuses) {
		String message = "Acceptable states are: ";
		for(Status status : statuses) {
			message += status.getError();
			message += ", ";
		}
		return new StateException(message);
	}
}
