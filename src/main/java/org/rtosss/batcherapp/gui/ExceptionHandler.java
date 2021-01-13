package org.rtosss.batcherapp.gui;

import java.io.IOException;

import org.rtosss.batcherapp.exceptions.RTOSException;
import org.rtosss.batcherapp.exceptions.StateException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExceptionHandler {
	public static void showException(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		if(e instanceof RTOSException) {
			RTOSException ce = (RTOSException) e;
			alert.setTitle("FreeRTOS error");
			alert.setHeaderText(ce.getErrorCode().toString());
		} else if(e instanceof IOException) {
			alert.setTitle("File error");
			alert.setHeaderText(null);
		} else if(e instanceof StateException) {
			alert.setTitle("State error");
			alert.setHeaderText(null);
		}
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
}
