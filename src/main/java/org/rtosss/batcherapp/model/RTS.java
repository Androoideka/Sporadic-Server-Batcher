package org.rtosss.batcherapp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.rtosss.batcherapp.exceptions.CustomException;
import org.rtosss.batcherapp.gui.IStatusObserver;
import org.rtosss.batcherapp.gui.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RTS extends StatusObservable {
	private ProcessBuilder builder;
	private Process process;
	private ObservableList<Task> tasks;
	
	private Thread outputThread;
	private Thread statThread;
	
	private IStatusObserver visualOutput;
	private IStatusObserver visualStats;
	
	private StringBuilder output;
	
	private BufferedReader controlReader;
	private BufferedWriter inputWriter;
	
	public RTS(String systemExecLocation) {
		super();
		builder = new ProcessBuilder(systemExecLocation);
		tasks = FXCollections.observableArrayList();
	}
	
	public void setVisualOutput(IStatusObserver visualOutput) {
		this.visualOutput = visualOutput;
	}

	public void setVisualStats(IStatusObserver visualStats) {
		this.visualStats = visualStats;
	}

	public ObservableList<Task> getTasks() {
		return tasks;
	}
	
	public void start() throws IOException {
		if(process == null) {
			process = builder.start();
			output = new StringBuilder("");
			outputThread = new Thread(() -> {
				try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while((line = outputReader.readLine()) != null) {
						System.out.println("test");
						output.append(line);
						visualOutput.sendMessage(output.toString());
					}
				} catch (IOException e) {
					
				}
			});
			outputThread.start();
			inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			controlReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
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
			inputWriter = null;
			controlReader = null;
			try {
				outputThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			process = null;
			updateStatus(Status.LOADED);
		}
	}
}
