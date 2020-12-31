package org.rtosss.batcherapp.gui;

import javafx.scene.paint.Color;

public enum Status {
	UNAVAILABLE(Color.RED),
	LOADED(Color.ORANGE),
	STARTED(Color.DARKGREEN),
	ACTIVE(Color.LIGHTGREEN);
	
	private final Color color;
	
	private Status(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
