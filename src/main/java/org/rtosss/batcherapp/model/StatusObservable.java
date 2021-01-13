package org.rtosss.batcherapp.model;

import java.util.ArrayList;
import java.util.List;

import org.rtosss.batcherapp.gui.IStatusObserver;
import org.rtosss.batcherapp.gui.Status;

public class StatusObservable {
	private List<IStatusObserver> observers;
	protected Status status;
	
	public StatusObservable() {
		observers = new ArrayList<>();
		status = Status.LOADED;
	}
	
	public void addObserver(IStatusObserver observer) {
		observers.add(observer);
		observer.updateStatus(status);
	}
	
	public void removeObservers(boolean replacementAbsent) {
		if(replacementAbsent) {
			updateStatus(Status.UNAVAILABLE);
		}
		observers.clear();
	}
	
	protected void updateStatus(Status status) {
		this.status = status;
		for(IStatusObserver observer : observers) {
			observer.updateStatus(status);
		}
	}
}
