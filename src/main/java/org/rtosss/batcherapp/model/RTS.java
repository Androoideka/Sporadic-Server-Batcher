package org.rtosss.batcherapp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.rtosss.batcherapp.exceptions.RTOSException;
import org.rtosss.batcherapp.exceptions.StateException;
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
	
	private BlockingQueue<Character> visualOutput;
	private IStatusObserver visualStats;
	
	private BufferedReader outputReader;
	private BufferedReader controlReader;
	private BufferedWriter inputWriter;
	
	public RTS(String systemExecLocation) {
		super();
		builder = new ProcessBuilder(systemExecLocation);
		tasks = FXCollections.observableArrayList();
	}

	public void setVisualOutput(BlockingQueue<Character> visualOutput) {
		this.visualOutput = visualOutput;
	}

	public void setVisualStats(IStatusObserver visualStats) {
		this.visualStats = visualStats;
	}

	public ObservableList<Task> getTasks() {
		return tasks;
	}
	
	private void readStatTask() throws IOException {
		String response = controlReader.readLine();
		PeriodicTask statTask = new PeriodicTask("stat", TaskCode.getStatTask(), "", "1000");
		statTask.setHandle(response.substring(response.indexOf(' ') + 1));
		tasks.add(statTask);
	}
	
	public void start() throws RTOSException, IOException {
		if(process == null) {
			process = builder.start();
			inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			controlReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			// Comment this out if not using stats
			readStatTask();
			
			updateStatus(Status.STARTED);
		}
	}
	
	public void sendBatch(Batch batch) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED && status != Status.ACTIVE) {
			Status[] statuses = {Status.STARTED, Status.ACTIVE};
			throw StateException.factory(statuses);
		}
		// Send messages to FreeRTOS
		for(Task task : batch.getTasks()) {
			String command = task.addTask();
			inputWriter.write(command);
			inputWriter.newLine();
			inputWriter.flush();
			String response = controlReader.readLine();
			if(response.startsWith("Handle: ")) {
				task.setHandle(response.substring(response.indexOf(' ') + 1));
				tasks.add(task);
			} else {
				throw new RTOSException(response);
			}
		}
	}
	
	public void removeTasks(List<Task> selectedTasks) throws RTOSException, IOException, StateException {
		if(status != Status.ACTIVE) {
			throw StateException.factory(Status.ACTIVE);
		}
		// Send message to FreeRTOS
		for(Task task : selectedTasks) {
			String command = task.deleteTask();
			inputWriter.write(command);
			inputWriter.newLine();
			inputWriter.flush();
			tasks.remove(task);
		}
	}
	
	public Integer getMaxCapacity(Integer period) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED) {
			throw StateException.factory(Status.STARTED);
		}
		// Send message to FreeRTOS
		String command = "get_max_server_capacity " + Integer.toUnsignedString(period);
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		String response = controlReader.readLine();
		try {
			Integer capacity = Integer.parseUnsignedInt(response);
			return capacity;
		} catch(NumberFormatException e) {
			throw new RTOSException(response);
		}
	}
	
	public void initialiseServer(Integer capacity, Integer period) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED) {
			throw StateException.factory(Status.STARTED);
		}
		// Send message to FreeRTOS
		String command = "initialise_server " + Integer.toUnsignedString(capacity) + " " + Integer.toUnsignedString(period);
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		String response = controlReader.readLine();
		if(!response.equals("Scheduler started.")) {
			throw new RTOSException(response);
		}
		// Initialise output stream
		outputThread = new Thread(() -> {
			try {
				int output;
				while((output = outputReader.read()) != -1) {
					char c = (char) output;
					visualOutput.put(c);
				}
			} catch (IOException e) {
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		outputThread.start();
		
		updateStatus(Status.ACTIVE);
	}
	
	public void stop() {
		if(process != null) {
			process.destroy();
			try {
				inputWriter.close();
				controlReader.close();
				outputReader.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			if(outputThread != null) {
				try {
					outputThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				outputThread = null;
			}
			if(statThread != null) {
				try {
					statThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				statThread = null;
			}
			process = null;
			updateStatus(Status.LOADED);
		}
	}
}
