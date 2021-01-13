package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.model.RTS;

import javafx.scene.layout.BorderPane;

public class ChartView extends BorderPane implements IStatusObserver {
	public ChartView() {
		super();
	}

	@Override
	public void updateStatus(Status status) {
		if(status == Status.ACTIVE) {
			// Enable chart
		} else {
			// Disable chart
		}
	}

	@Override
	public void setRTS(RTS system) {
    	if(system != null) {
    		system.addObserver(this);
    		system.setVisualStats(this);
    	}
	}

	@Override
	public void sendMessage(String message) {
		// Information for last few ticks I guess
	}
}
