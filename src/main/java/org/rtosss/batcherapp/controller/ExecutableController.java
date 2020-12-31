package org.rtosss.batcherapp.controller;

import java.io.File;

import org.rtosss.batcherapp.gui.MainView;
import org.rtosss.batcherapp.model.RTS;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExecutableController implements EventHandler<ActionEvent> {

	private MainView view;
	
	public ExecutableController(MainView view) {
		this.view = view;
	}
	
	@Override
	public void handle(ActionEvent arg0) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select FreeRTOS Sporadic Server Executable");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Executables", "*.exe"));
		File selectedFile = fileChooser.showOpenDialog(view.getScene().getWindow());
		if(selectedFile != null) {
			view.setRTS(new RTS(selectedFile.toString()));
		}
		else {
			view.setRTS(null);
		}
	}
	
}
