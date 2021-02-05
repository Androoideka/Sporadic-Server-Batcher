package org.rtosss.batcherapp.gui.components;

import org.rtosss.batcherapp.model.AperiodicTask;
import org.rtosss.batcherapp.model.TaskInstance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class AperiodicArrivalManager {
	
	private LineChart<Number, Number> aperiodicChart;
	
	public AperiodicArrivalManager(LineChart<Number, Number> aperiodicChart) {
		this.aperiodicChart = aperiodicChart;
	}
	
	public void addTask(TaskInstance task) {
		AperiodicTask aperiodic = (AperiodicTask) task.getTask();
		boolean flag = false;
		for(Series<Number, Number> taskSeries : aperiodicChart.getData()) {
			Data<Number, Number> point = taskSeries.getData().get(0);
			if(point.getXValue().equals(task.getArrivalTime()) 
					&& point.getYValue().equals(aperiodic.getComputationTime())) {
				taskSeries.setName(taskSeries.getName() + ", " + task.getHandle());
				flag = true;
				break;
			}
		}
		if(!flag) {
			ObservableList<Data<Number, Number>> line = FXCollections.observableArrayList();
			Data<Number, Number> arrival = new Data<Number, Number>(task.getArrivalTime(),
					aperiodic.getComputationTime());
			line.add(arrival);
			Series<Number, Number> taskSeries = new Series<>(task.getHandle(), line);
			aperiodicChart.getData().add(taskSeries);
		}
	}
}
