package org.rtosss.batcherapp.gui;

import java.io.IOException;

import org.rtosss.batcherapp.gui.components.ExceptionHandler;
import org.rtosss.batcherapp.gui.components.TextAreaMessageConsumer;
import org.rtosss.batcherapp.gui.components.UnsignedIntegerField;
import org.rtosss.batcherapp.model.RTS;
import org.rtosss.batcherapp.model.TaskInstance;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsView extends BorderPane implements IStatusObserver {
	private RTS system;
	private boolean isOn;
	
	private VBox left;
	private Label statIntervalLabel;
	private UnsignedIntegerField statInterval;
	private Button setStatInterval;
	private ListView<TaskInstance> taskList;
	private Button cancelTask;
	
	private VBox right;
	private Label serverPeriodLabel;
	private Label serverCapacityLabel;
	private UnsignedIntegerField serverPeriod;
	private UnsignedIntegerField serverCapacity;
	private Button checkMaxCapacity;
	private Button initialise;
	
	private TextArea output;
	private TextAreaMessageConsumer messageConsumer;
	
	private Button switchButton;
	
	public SettingsView() {
		super();
		
		statIntervalLabel = new Label("Stat Write Interval");
		statInterval = new UnsignedIntegerField(true);
		setStatInterval = new Button("Enable Stats");
		setStatInterval.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				int interval = Integer.parseUnsignedInt(statInterval.getText());
				try {
					system.setStats(interval);
					setStatInterval.setDisable(true);
				} catch(Exception e) {
					ExceptionHandler.showException(e);
				}
			}
			
		});
		taskList = new ListView<TaskInstance>();
		taskList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cancelTask = new Button("Cancel Tasks");
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
		left = new VBox(8, statIntervalLabel, statInterval, setStatInterval, taskList, cancelTask);
		left.setAlignment(Pos.CENTER);
		left.setPadding(new Insets(20));
		
		serverCapacityLabel = new Label("Server Capacity");
		serverPeriodLabel = new Label("Server Period");
		serverCapacity = new UnsignedIntegerField(false);
		serverPeriod = new UnsignedIntegerField(true);
		checkMaxCapacity = new Button("Max Capacity");
		checkMaxCapacity.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Integer period = Integer.parseUnsignedInt(serverPeriod.getText());
				try {
					Integer capacity = system.getMaxCapacity(period);
					serverCapacity.setText(Integer.toUnsignedString(capacity));
				} catch(Exception e) {
					ExceptionHandler.showException(e);
				}
			}
			
		});
		initialise = new Button("Initialise");
		initialise.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Integer capacity = Integer.parseUnsignedInt(serverCapacity.getText());
				Integer period = Integer.parseUnsignedInt(serverPeriod.getText());
				try {
					system.initialiseServer(capacity, period);
				} catch(Exception e) {
					ExceptionHandler.showException(e);
				}
			}
		});
		
		HBox options = new HBox(8, checkMaxCapacity, initialise);
		
		right = new VBox(8, serverCapacityLabel, serverCapacity, serverPeriodLabel, serverPeriod, options);
		right.setAlignment(Pos.CENTER);
		right.setPadding(new Insets(20));
		
		output = new TextArea();
		output.setEditable(false);
		messageConsumer = new TextAreaMessageConsumer(output);
		messageConsumer.start();
		
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
			isOn = false;
			setStatInterval.setDisable(true);
			checkMaxCapacity.setDisable(true);
			initialise.setDisable(true);
			switchButton.setText("Start");
			cancelTask.setDisable(true);
		} else if(status == Status.STARTED) {
			isOn = true;
			setStatInterval.setDisable(false);
			initialise.setDisable(false);
			checkMaxCapacity.setDisable(false);
			switchButton.setText("Stop");
			output.setText("");
		} else if(status == Status.ACTIVE) {
			setStatInterval.setDisable(true);
			checkMaxCapacity.setDisable(true);
			initialise.setDisable(true);
			cancelTask.setDisable(false);
		}
	}
	
	@Override
	public void setRTS(RTS system) {
    	if(system != null) {
    		system.addObserver(this);
    		system.setVisualOutput(messageConsumer.getMessageQueue());
    		taskList.setItems(system.getTasks());
    	}
		this.system = system;
	}
}
