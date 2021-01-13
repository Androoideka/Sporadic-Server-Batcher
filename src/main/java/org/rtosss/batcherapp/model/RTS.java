package org.rtosss.batcherapp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.rtosss.batcherapp.exceptions.RTOSException;
import org.rtosss.batcherapp.exceptions.StateException;
import org.rtosss.batcherapp.gui.ExceptionHandler;
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
	
	private BufferedReader outputReader;
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
	
	public void start() throws RTOSException, IOException {
		if(process == null) {
			process = builder.start();
			inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			controlReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String response = controlReader.readLine();
			try {
				int handle = Integer.parseUnsignedInt(response);
				PeriodicTask statTask = new PeriodicTask("stat", TaskCode.getStatTask(), "", "1000");
				statTask.setHandle(handle);
				tasks.add(statTask);
			} catch(NumberFormatException e) {
				ExceptionHandler.showException(new RTOSException(response));
			}
			
			updateStatus(Status.STARTED);
		}
	}
	
	public void sendBatch(Batch batch) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED && status != Status.ACTIVE) {
			Status[] statuses = {Status.STARTED, Status.ACTIVE};
			throw StateException.factory(statuses);
		}
		for(Task task : batch.getTasks()) {
			String command = task.addTask();
			System.out.println(command);
			inputWriter.write(command);
			inputWriter.newLine();
			String response = controlReader.readLine();
			try {
				int handle = Integer.parseUnsignedInt(response);
				task.setHandle(handle);
			} catch(NumberFormatException e) {
				throw new RTOSException(response);
			}
		}
	}
	
	public void removeTasks(List<Task> selectedTasks) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED && status != Status.ACTIVE) {
			Status[] statuses = {Status.STARTED, Status.ACTIVE};
			throw StateException.factory(statuses);
		}
		// Send message to FreeRTOS
		for(Task task : selectedTasks) {
			String command = task.deleteTask();
			System.out.println(command);
			inputWriter.write(command);
			inputWriter.newLine();
			tasks.remove(task);
		}
	}
	
	public int getMaxCapacity(Integer period) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED) {
			throw StateException.factory(Status.STARTED);
		}
		// Send message to FreeRTOS
		String command = "get_max_server_capacity " + Integer.toUnsignedString(period);
		inputWriter.write(command);
		inputWriter.newLine();
		String response = controlReader.readLine();
		try {
			int capacity = Integer.parseUnsignedInt(response);
			return capacity;
		} catch(NumberFormatException e) {
			throw new RTOSException(response);
		}
	}
	
	public void initializeServer(Integer capacity, Integer period) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED) {
			throw StateException.factory(Status.STARTED);
		}
		// Send message to FreeRTOS
		String command = "initialize_server " + Integer.toUnsignedString(capacity) + " " + Integer.toUnsignedString(period);
		inputWriter.write(command);
		inputWriter.newLine();
		String response = controlReader.readLine();
		if(!response.equals("Scheduler started.")) {
			throw new RTOSException(response);
		}
		// Initialise output stream
		outputThread = new Thread(() -> {
			try {
				StringBuilder output = new StringBuilder("");
				while(true) {
					int c = outputReader.read();
					if(c != -1) {
						output.append((char) c);
						//visualOutput.sendMessage(output.toString());
						System.out.println(output.toString());
					} else {
						Thread.sleep(10);
					}
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
