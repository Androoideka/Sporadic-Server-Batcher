package org.rtosss.batcherapp.gui;

import java.io.IOException;

import org.rtosss.batcherapp.gui.components.UnsignedIntegerField;
import org.rtosss.batcherapp.model.RTS;
import org.rtosss.batcherapp.model.Task;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

public class SettingsView extends BorderPane implements IStatusObserver {
	private RTS system;
	private boolean isOn;
	
	private VBox left;
	private ListView<Task> taskList;
	private Button cancelTask;
	
	private VBox right;
	private Label serverPeriodLabel;
	private Label serverCapacityLabel;
	private UnsignedIntegerField serverPeriod;
	private UnsignedIntegerField serverCapacity;
	private Button checkMaxCapacity;
	private Button initialise;
	
	private TextArea output;
	
	private Button switchButton;
	
	public SettingsView() {
		super();
		
		taskList = new ListView<Task>();
		taskList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cancelTask = new Button("Cancel tasks");
		cancelTask.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					system.removeTasks(taskList.getSelectionModel().getSelectedItems());
				} catch(Exception e) {
					ExceptionHandler.showException(e);
				}
			}
			
		});
		left = new VBox(taskList, cancelTask);
		left.setSpacing(8);
		left.setAlignment(Pos.CENTER);
		left.setPadding(new Insets(20));
		
		serverPeriodLabel = new Label("Server Period");
		serverCapacityLabel = new Label("Server Capacity");
		serverPeriod = new UnsignedIntegerField();
		serverPeriod.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
		serverCapacity = new UnsignedIntegerField();
		serverCapacity.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
		checkMaxCapacity = new Button("Max capacity");
		initialise = new Button("Initialise");
		
		HBox options = new HBox(checkMaxCapacity, initialise);
		//options.setAlignment(Pos.CENTER);
		
		right = new VBox(serverPeriodLabel, serverPeriod, serverCapacityLabel, serverCapacity, options);
		right.setSpacing(8);
		right.setAlignment(Pos.CENTER);
		right.setPadding(new Insets(20));
		
		output = new TextArea();
		output.setEditable(false);
		
		switchButton = new Button("Start");
		switchButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				isOn = !isOn;
				if(isOn) {
					try {
						system.start();
					} catch (IOException e) {
						ExceptionHandler.showException(e);
					}
				}  else {
					system.stop();
				}
			}
			
		});
		BorderPane.setAlignment(switchButton, Pos.CENTER);
		BorderPane.setMargin(switchButton, new Insets(10));
		
		this.setLeft(left);
		this.setRight(right);
		this.setCenter(output);
		this.setBottom(switchButton);
	}

	@Override
	public void updateStatus(Status status) {
		if(status == Status.UNAVAILABLE || status == Status.LOADED) {
			taskList.setItems(null);
			isOn = false;
			checkMaxCapacity.setDisable(true);
			initialise.setDisable(true);
			switchButton.setText("Start");
			output.setText("");
		} else if(status == Status.STARTED) {
			taskList.setItems(system.getTasks());
			isOn = true;
			initialise.setDisable(false);
			checkMaxCapacity.setDisable(false);
			switchButton.setText("Stop");
		} else if(status == Status.ACTIVE) {
			checkMaxCapacity.setDisable(true);
			initialise.setDisable(true);
		}
	}
	
	@Override
	public void setRTS(RTS system) {
    	if(system != null) {
    		system.addObserver(this);
    		system.setVisualOutput(this);
    	}
		this.system = system;
	}

	@Override
	public void sendMessage(String message) {
		output.setText(message);
	}
}
