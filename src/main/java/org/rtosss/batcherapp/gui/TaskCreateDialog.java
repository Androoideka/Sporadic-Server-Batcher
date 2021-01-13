package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.controller.TaskCreateController;
import org.rtosss.batcherapp.gui.components.LimitedTextField;
import org.rtosss.batcherapp.gui.components.UnsignedIntegerField;
import org.rtosss.batcherapp.model.AperiodicTask;
import org.rtosss.batcherapp.model.PeriodicTask;
import org.rtosss.batcherapp.model.Task;
import org.rtosss.batcherapp.model.TaskCode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TaskCreateDialog extends Dialog<Task> {
	private GridPane grid;
	
	private VBox nameBox;
	private VBox taskCodeBox;
	private VBox paramsBox;
	private VBox ticksBox;
	
	private Label nameLabel;
	private LimitedTextField name;
	private Label taskCodeLabel;
	private ComboBox<TaskCode> taskCodes;
	private Label paramsLabel;
	private LimitedTextField params;
	private Label ticksLabel;
	private UnsignedIntegerField ticks;
	private CheckBox periodic;
	private CheckBox sendNow;
	
	public TaskCreateDialog() {
		super();
		
		nameLabel = new Label("Name");
		name = new LimitedTextField();
		name.setMaxLength(12);
		nameBox = new VBox(nameLabel, name);
		
		taskCodeLabel = new Label("Function");
		taskCodes = new ComboBox<>(TaskCode.getFunctions());
		VBox.setVgrow(taskCodes, Priority.ALWAYS);
		taskCodeBox = new VBox(taskCodeLabel, taskCodes);
		
		paramsLabel = new Label("Parameters");
		params = new LimitedTextField();
		params.setMaxLength(17);
		params.setText("NULL");
		paramsBox = new VBox(paramsLabel, params);
		
		ticksLabel = new Label("Arrival");
		ticks = new UnsignedIntegerField();
		ticksBox = new VBox(ticksLabel, ticks);
		
		periodic = new CheckBox("Periodic");
		periodic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if(periodic.isSelected()) {
					ticksLabel.setText("Period");
					sendNow.setDisable(true);
				} else {
					ticksLabel.setText("Arrival");
					sendNow.setDisable(false);
				}
			}
			
		});
		
		sendNow = new CheckBox("Now");
		sendNow.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if(sendNow.isSelected()) {
					ticks.setDisable(true);
				} else {
					ticks.setDisable(false);
				}
			}
			
		});
		
		grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10)); 
		
		grid.setVgap(8);
		grid.setHgap(8);
		
		grid.setAlignment(Pos.CENTER);
		
		grid.add(nameBox, 0, 0);
		grid.add(taskCodeBox, 1, 0);
		grid.add(paramsBox, 2, 0);
		grid.add(periodic, 1, 2);
		grid.add(sendNow, 1, 3);
		grid.add(ticksBox, 1, 1);
		getDialogPane().setContent(grid);
		
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		((Button)getDialogPane().lookupButton(ButtonType.OK)).setOnAction(new TaskCreateController(this));
	}
	
	public boolean addTask() {
		if(name.getText().isBlank() || taskCodes.getValue() == null || params.getText().isBlank()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Creation failure");
			alert.setHeaderText("Missing values");
			if(name.getText().isBlank()) {
				alert.setContentText("You haven't provided a name.");
			} else if(taskCodes.getValue() == null) {
				alert.setContentText("You haven't provided a task code.");
			} else if(params.getText().isBlank()) {
				alert.setContentText("You haven't provided a parameter. If you don't wish to provide anything, type NULL.");
			}
			alert.showAndWait();
			return false;
		}
		if(periodic.isSelected()) {
			this.setResult(new PeriodicTask(name.getText(), taskCodes.getValue(), params.getText(), ticks.getText()));
		} else {
			if(sendNow.isSelected()) {
				this.setResult(new AperiodicTask(name.getText(), taskCodes.getValue(), params.getText(), null));
			}
			else {
				this.setResult(new AperiodicTask(name.getText(), taskCodes.getValue(), params.getText(), ticks.getText()));
			}
		}
		return true;
	}
}
