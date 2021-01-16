package org.rtosss.batcherapp.gui.components;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.rtosss.batcherapp.model.TickStats;

import javafx.animation.AnimationTimer;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollBar;

public class ChartMessageConsumer extends AnimationTimer {
	private final BlockingQueue<TickStats> messageQueue;
	private final Series<Number, Number> serverSeries;
	private final Series<Number, String> taskSeries;
	private final ScrollBar scroller;
	private Integer maxTick = 10;
	
	public ChartMessageConsumer(Series<Number, Number> serverSeries, Series<Number, String> taskSeries, ScrollBar scroller) {
		super();
		messageQueue = new LinkedBlockingQueue<>();
		this.serverSeries = serverSeries;
		this.taskSeries = taskSeries;
		this.scroller = scroller;
		scroller.setMax(0);
	}

	public BlockingQueue<TickStats> getMessageQueue() {
		return messageQueue;
	}

	@Override
	public void handle(long arg0) {
		SortedSet<TickStats> stats = new TreeSet<>();
		messageQueue.drainTo(stats);
		for(TickStats stat : stats) {
			if(stat.getTick() > maxTick) {
				maxTick = stat.getTick();
				scroller.setMax(maxTick - 10);
			}
			try {
				Data<Number, Number> graphTick = serverSeries.getData().get(stat.getTick());
				graphTick.setYValue(stat.getCapacity());
			} catch(IndexOutOfBoundsException e) {
				serverSeries.getData().add(new Data<>(stat.getTick(), stat.getCapacity()));
			}
			Data<Number, String> taskTick;
			try {
				taskTick = taskSeries.getData().get(stat.getTick());
				taskTick.setYValue(stat.getHandle());
			} catch(IndexOutOfBoundsException e) {
				taskTick = new Data<>(stat.getTick(), stat.getHandle());
				taskSeries.getData().add(taskTick);
			}
			if(stat.isMarker()) {
				taskTick.getNode().setStyle("-fx-background-color: black;");
			} else {
				taskTick.getNode().setStyle("-fx-background-color: transparent;");
			}
		}
	}

}
