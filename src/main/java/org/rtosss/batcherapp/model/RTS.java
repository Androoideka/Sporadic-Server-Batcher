package org.rtosss.batcherapp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.rtosss.batcherapp.exceptions.RTOSException;
import org.rtosss.batcherapp.exceptions.StateException;
import org.rtosss.batcherapp.gui.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RTS extends StatusObservable {
	private ProcessBuilder builder;
	private Process process;
	
	private ObservableList<Task> tasks;
	private PeriodicTask statTask;
	private PeriodicTask idleTask;
	
	private Integer serverCapacity;
	private Integer serverPeriod;
	
	private Thread outputThread;
	private Thread statThread;
	
	private BlockingQueue<Character> visualOutput;
	private BlockingQueue<TickStats> visualStats;
	
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

	public void setVisualStats(BlockingQueue<TickStats> visualStats) {
		this.visualStats = visualStats;
	}

	public ObservableList<Task> getTasks() {
		return tasks;
	}
	
	private void readStatTask() throws IOException {
		String response = controlReader.readLine();
		statTask = new PeriodicTask("stat", TaskCode.getStatCode(), "", "1000");
		statTask.setHandle(response.substring(response.indexOf(' ') + 1));
		tasks.add(statTask);
	}
	
	public void start() throws RTOSException, IOException {
		if(process == null) {
			tasks.clear();
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
	
	public void initialiseServer(Integer serverCapacity, Integer serverPeriod) throws RTOSException, IOException, StateException {
		if(status != Status.STARTED) {
			throw StateException.factory(Status.STARTED);
		}
		// Send message to FreeRTOS
		String command = "initialise_server " 
		+ Integer.toUnsignedString(serverCapacity) 
		+ " " + Integer.toUnsignedString(serverPeriod);
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		String response = controlReader.readLine();
		if(response.startsWith("Handle: ")) {
			idleTask = new PeriodicTask("IDLE", TaskCode.getIdleCode(), "", "4294967295");
			idleTask.setHandle(response.substring(response.indexOf(' ') + 1));
			tasks.add(idleTask);
		} else {
			throw new RTOSException(response);
		}
		this.serverCapacity = serverCapacity;
		this.serverPeriod = serverPeriod;
		
		updateStatus(Status.ACTIVE);
		
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
		
		// Initialise stat stream
		statThread = new Thread(() -> {
			File file = new File(System.getProperty("user.dir") + File.separator + "log.txt");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(process != null) {
				try (BufferedReader statsReader = new BufferedReader(new FileReader(file))) {
					String line;
					while((line = statsReader.readLine()) != null && !line.equals("0 00000000 0")) {
						String[] stats = line.split(" ");
						Integer tick = Integer.parseUnsignedInt(stats[0]);
						String handle = stats[1];
						Integer capacity = Integer.parseUnsignedInt(stats[2]);
						
						TickStats tickStats = new TickStats(tick, handle, capacity);
						visualStats.put(tickStats);
					}
					Thread.sleep(200);
				} catch (IOException e) {
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		statThread.start();
	}
	
	public Integer getServerCapacity() {
		return serverCapacity;
	}

	public Integer getServerPeriod() {
		return serverPeriod;
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
			process = null;
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
			updateStatus(Status.LOADED);
		}
	}
}
