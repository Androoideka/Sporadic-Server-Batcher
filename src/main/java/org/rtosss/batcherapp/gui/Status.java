package org.rtosss.batcherapp.gui;

import javafx.scene.paint.Color;

public enum Status {
	UNAVAILABLE(Color.RED, ""),
	LOADED(Color.ORANGE, "System is selected but not started"),
	STARTED(Color.DARKGREEN, "System is started but scheduler is inactive"),
	ACTIVE(Color.LIGHTGREEN, "Scheduler is active");
	
	private final Color color;
	private final String error;
	
	private Status(Color color, String error) {
		this.color = color;
		this.error = error;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getError() {
		return error;
	}
}
