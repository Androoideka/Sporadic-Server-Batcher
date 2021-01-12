package org.rtosss.batcherapp.model;

import java.io.IOException;
import java.util.List;

import org.rtosss.batcherapp.exceptions.CustomException;
import org.rtosss.batcherapp.gui.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RTS extends StatusObservable {
	private ProcessBuilder builder;
	private Process process;
	private ObservableList<Task> tasks;
	
	public RTS(String systemExecLocation) {
		super();
		builder = new ProcessBuilder(systemExecLocation);
		tasks = FXCollections.observableArrayList();
	}
	
	public ObservableList<Task> getTasks() {
		return tasks;
	}
	
	public void start() throws IOException {
		if(process == null) {
			process = builder.start();
			updateStatus(Status.STARTED);
		}
	}
	
	public void sendBatch(Batch batch) throws CustomException {
		// TO-DO use standard input to send it
	}
	
	public void removeTasks(List<Task> selectedTasks) {
		// Send message to FreeRTOS
		tasks.removeAll(selectedTasks);
	}
	
	public void getMaxCapacity(Integer period) {
		
	}
	
	public void initializeServer(Integer capacity, Integer period) throws CustomException {
		// Send message to FreeRTOS
		updateStatus(Status.ACTIVE);
	}
	
	public void stop() {
		if(process != null) {
			process.destroy();
			updateStatus(Status.LOADED);
		}
	}
}
