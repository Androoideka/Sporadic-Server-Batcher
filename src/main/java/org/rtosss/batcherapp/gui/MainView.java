package org.rtosss.batcherapp.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.rtosss.batcherapp.controller.ExecutableController;
import org.rtosss.batcherapp.model.RTS;

public class MainView extends BorderPane {
	private static MainView instance = null;
	
	private RTS system;
	
	private Label statusValue;
	private Label statusLabel;
	private HBox statusAligner;
	private Button systemSelection;
	private HBox systemAligner;
	private BorderPane topBar;
	private TabPane systemViews;
	
	private Tab systemSettings;
	private Tab systemChart;
	private Tab systemBatcher;
	private Tab systemInfo;
	
	private MainView() {
		super();
		
		statusValue = new Label();
		statusLabel = new Label("Status: ");
		
		statusAligner = new HBox(statusLabel, statusValue);
		statusAligner.setAlignment(Pos.CENTER);

		systemSelection = new Button("Select");
		systemSelection.setOnAction(new ExecutableController(this));
		
		systemAligner = new HBox(systemSelection);
		systemAligner.setAlignment(Pos.CENTER);
		
		topBar = new BorderPane();
		topBar.setLeft(systemAligner);
		topBar.setRight(statusAligner);
		topBar.setStyle("-fx-background-color: #A9A9A9;");
		topBar.setPadding(new Insets(15, 12, 15, 12));
		
		systemViews = new TabPane();
		systemBatcher = new Tab("Batcher", new BatcherView());
		systemSettings = new Tab("Settings", new SettingsView());
		systemChart = new Tab("Chart", new ChartView());
		systemInfo = new Tab("Info", new InfoView());
		
		systemViews.getTabs().add(systemBatcher);
		systemViews.getTabs().add(systemSettings);
		systemViews.getTabs().add(systemChart);
		systemViews.getTabs().add(systemInfo);
		
		systemViews.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		this.setTop(topBar);
		this.setCenter(systemViews);
		
		setStatus(Status.UNAVAILABLE);
	}
	
	public void setStatus(Status status) {
		statusValue.setText(status.toString());
		statusValue.setTextFill(status.getColor());
		
		for(Tab tab : systemViews.getTabs()) {
			if(status == Status.UNAVAILABLE && tab != systemBatcher) {
				tab.getContent().setDisable(true);
			} else {
				tab.getContent().setDisable(false);
			}
			if(tab.getContent() instanceof StatusDependent) {
				StatusDependent iTab = (StatusDependent) tab.getContent();
				iTab.setStatus(status);
			}
		}
	}
	
    public RTS getRTS() {
    	return system;
    }
    
    public void setRTS(RTS system) {
    	if(this.system != system && this.system != null) {
    		this.system.stop();
    	}
    	this.system = system;
    }
	
	public static MainView getInstance() {
		if(instance == null) {
			instance = new MainView();
		}
		return instance;
	}
}
