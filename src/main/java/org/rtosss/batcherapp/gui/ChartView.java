package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.gui.components.ChartMessageConsumer;
import org.rtosss.batcherapp.model.RTS;
import org.rtosss.batcherapp.model.Task;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChartView extends VBox implements IStatusObserver {
	private RTS system;
	
	private NumberAxis xServerAxisTicks;
	private NumberAxis yAxisCapacity;
	private AreaChart<Number, Number> serverChart;
	
	private NumberAxis xTaskAxisTicks;
	private CategoryAxis yAxisTask;
	private LineChart<Number, String> taskChart;
	
	private ScrollBar scroller;
	
	private ChartMessageConsumer messageConsumer;
	
	public ChartView() {
		super();
		this.setAlignment(Pos.CENTER);
		
		xServerAxisTicks = new NumberAxis("Ticks", 0, 10, 1);
		xServerAxisTicks.setMinorTickVisible(false);
		
		yAxisCapacity = new NumberAxis("Capacity", 0, 10, 1);
		yAxisCapacity.setMinorTickVisible(false);
		
		serverChart = new AreaChart<>(xServerAxisTicks, yAxisCapacity);
		serverChart.setTitle("Server capacity over time");
		
		xTaskAxisTicks = new NumberAxis("Ticks", 0, 10, 1);
		xTaskAxisTicks.setMinorTickVisible(false);
		
		yAxisTask = new CategoryAxis();
		yAxisTask.setLabel("Task Handles");
		
		taskChart = new LineChart<>(xTaskAxisTicks, yAxisTask);
		taskChart.setTitle("Running tasks over time");
		
		scroller = new ScrollBar();
		scroller.setMin(0);
		scroller.setMax(10);
		scroller.setBlockIncrement(1);
		scroller.setUnitIncrement(1);
		scroller.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				double realPosition = scroller.getValue();
				double newValue = Math.floor(realPosition);
				scroller.valueProperty().set(newValue);
				xServerAxisTicks.setLowerBound(newValue);
				xServerAxisTicks.setUpperBound(newValue + 10);
				xTaskAxisTicks.setLowerBound(newValue);
				xTaskAxisTicks.setUpperBound(newValue + 10);
			}
			
		});
		
		VBox.setVgrow(serverChart, Priority.ALWAYS);
		VBox.setVgrow(taskChart, Priority.ALWAYS);
		this.getChildren().add(serverChart);
		this.getChildren().add(taskChart);
		this.getChildren().add(scroller);
	}

	@Override
	public void updateStatus(Status status) {
		if(status == Status.ACTIVE) {
			serverChart.getData().clear();
			taskChart.getData().clear();
			
			yAxisCapacity.setUpperBound(system.getServerCapacity() + 2); // +2 just makes it look nicer
			ObservableList<String> handles = FXCollections.observableArrayList();
			for(Task task : system.getTasks()) {
				handles.add(task.getHandle());
			}
			yAxisTask.setCategories(handles);
			
			Series<Number, Number> server = new Series<>();
			serverChart.getData().add(server);
			
			Series<Number, String> task = new Series<>();
			taskChart.getData().add(task);
			
			messageConsumer = new ChartMessageConsumer(server, task, scroller);
			messageConsumer.start();
			system.setVisualStats(messageConsumer.getMessageQueue());
		} else {
			if(messageConsumer != null) {
				messageConsumer.stop();
			}
		}
	}

	@Override
	public void setRTS(RTS system) {
    	if(system != null) {
    		system.addObserver(this);
    	}
		this.system = system;
	}
}
