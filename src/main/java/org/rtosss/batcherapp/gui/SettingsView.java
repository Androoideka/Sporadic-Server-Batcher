package org.rtosss.batcherapp.gui;

import java.io.IOException;

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
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

public class SettingsView extends BorderPane implements StatusDependent {
	private boolean isOn;
	
	private VBox left;
	private ListView<Task> taskList;
	private Button cancelTask;
	
	private VBox right;
	private Label serverPeriodLabel;
	private Label serverCapacityLabel;
	private UnsignedIntegerField serverPeriod;
	private UnsignedIntegerField serverCapacity;
	private Button initialize;
	
	private TextArea output;
	
	private Button switchButton;
	
	public SettingsView() {
		super();
		
		taskList = new ListView<Task>();
		taskList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cancelTask = new Button("Cancel tasks");
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
		initialize = new Button("Initialize");
		right = new VBox(serverPeriodLabel, serverPeriod, serverCapacityLabel, serverCapacity, initialize);
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
						MainView.getInstance().getRTS().start();
						MainView.getInstance().setStatus(Status.STARTED);
					} catch (IOException e) {
						ExceptionHandler.showException(e);
					}
				}  else {
					MainView.getInstance().getRTS().stop();
					MainView.getInstance().setStatus(Status.LOADED);
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
	public void setStatus(Status status) {
		if(status == Status.UNAVAILABLE || status == Status.LOADED) {
			taskList.setItems(null);
			isOn = false;
			initialize.setDisable(true);
			switchButton.setText("Start");
			output.setText("");
		} else if(status == Status.STARTED) {
			taskList.setItems(MainView.getInstance().getRTS().getTasks());
			isOn = true;
			initialize.setDisable(false);
			switchButton.setText("Stop");
		} else if(status == Status.ACTIVE) {
			initialize.setDisable(true);
		}
	}
}
