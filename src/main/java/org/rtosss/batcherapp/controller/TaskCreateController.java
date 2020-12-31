package org.rtosss.batcherapp.controller;

import org.rtosss.batcherapp.gui.TaskCreateDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class TaskCreateController implements EventHandler<ActionEvent> {
	private TaskCreateDialog dialog;
	
	public TaskCreateController(TaskCreateDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void handle(ActionEvent arg0) {
		dialog.addTask();
		dialog.close();
	}

}
