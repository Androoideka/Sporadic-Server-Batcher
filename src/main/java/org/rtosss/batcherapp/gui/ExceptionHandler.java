package org.rtosss.batcherapp.gui;

import java.io.IOException;

import org.rtosss.batcherapp.exceptions.CustomException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExceptionHandler {
	public static void showException(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		if(e instanceof CustomException) {
			CustomException ce = (CustomException) e;
			alert.setTitle("FreeRTOS error");
			alert.setHeaderText(ce.getErrorCode().toString());
		} else if(e instanceof IOException) {
			alert.setTitle("File error");
			alert.setHeaderText(null);
		}
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
}
