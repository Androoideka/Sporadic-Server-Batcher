package org.rtosss.batcherapp.gui;

import org.rtosss.batcherapp.gui.components.ChartMessageConsumer;
import org.rtosss.batcherapp.model.RTS;

import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ChartView extends BorderPane implements IStatusObserver {
	private RTS system;
	
	private VBox charts;
	
	private NumberAxis xAxisTicks;
	
	private NumberAxis yAxisCapacity;
	private AreaChart<Number, Number> serverChart;
	private ChartMessageConsumer messageConsumer;
	
	public ChartView() {
		super();
		xAxisTicks = new NumberAxis("Ticks", 0, 2000, 1);
		xAxisTicks.setMinorTickCount(0);
		
		yAxisCapacity = new NumberAxis("Capacity", 0, 10, 1);
		yAxisCapacity.setMinorTickCount(0);
		
		serverChart = new AreaChart<>(xAxisTicks, yAxisCapacity);
		serverChart.setTitle("Server capacity over time");
		
		charts = new VBox(serverChart);
		charts.setAlignment(Pos.CENTER);
		this.setCenter(charts);
	}

	@Override
	public void updateStatus(Status status) {
		if(status == Status.ACTIVE) {
			serverChart.getData().clear();
			yAxisCapacity.setUpperBound(system.getServerCapacity() + 2);
			Series<Number, Number> server = new Series<>();
			serverChart.getData().add(server);
			messageConsumer = new ChartMessageConsumer(server);
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
