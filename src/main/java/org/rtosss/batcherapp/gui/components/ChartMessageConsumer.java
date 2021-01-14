package org.rtosss.batcherapp.gui.components;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.rtosss.batcherapp.model.TickStats;

import javafx.animation.AnimationTimer;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class ChartMessageConsumer extends AnimationTimer {
	private final BlockingQueue<TickStats> messageQueue;
	private final XYChart.Series<Number, Number> serverSeries;
	
	public ChartMessageConsumer(XYChart.Series<Number, Number> serverSeries) {
		super();
		messageQueue = new LinkedBlockingQueue<>();
		this.serverSeries = serverSeries;
	}

	public BlockingQueue<TickStats> getMessageQueue() {
		return messageQueue;
	}

	@Override
	public void handle(long arg0) {
		SortedSet<TickStats> stats = new TreeSet<>();
		messageQueue.drainTo(stats);
		for(TickStats stat : stats) {
			try {
				Data<Number, Number> graphTick = serverSeries.getData().get(stat.getTick());
				graphTick.setYValue(stat.getCapacity());
			} catch(IndexOutOfBoundsException e) {
				serverSeries.getData().add(new Data<>(stat.getTick(), stat.getCapacity()));
			}
		}
	}

}
