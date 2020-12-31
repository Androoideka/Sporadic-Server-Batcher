package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.SystemInfo;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class InfoView extends BorderPane {
	public InfoView() {
	    var javaVersion = SystemInfo.javaVersion();
	    var javafxVersion = SystemInfo.javafxVersion();

	    var label = new Label("JavaFX " + javafxVersion + " running on Java " + javaVersion + ".");
	    this.setCenter(label);
	}
}
