package org.rtosss.batcherapp.model;

import java.io.IOException;

import org.rtosss.batcherapp.exceptions.CustomException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RTS {
	private ProcessBuilder builder;
	private Process process;
	private ObservableList<Task> tasks;
	
	public RTS(String systemExecLocation) {
		builder = new ProcessBuilder(systemExecLocation);
		tasks = FXCollections.observableArrayList();
	}
	
	public ObservableList<Task> getTasks() {
		return tasks;
	}
	
	public void start() throws IOException {
		if(process == null) {
			process = builder.start();
		}
	}
	
	public void sendBatch(Batch batch) throws CustomException {
		// TO-DO use standard input to send it
	}
	
	public void stop() {
		if(process != null) {
			process.destroy();
		}
	}
}
