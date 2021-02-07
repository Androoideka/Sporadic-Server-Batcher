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

import org.rtosss.batcherapp.exceptions.ErrorCode;
import org.rtosss.batcherapp.exceptions.RTOSException;
import org.rtosss.batcherapp.exceptions.StateException;
import org.rtosss.batcherapp.gui.Status;
import org.rtosss.batcherapp.gui.components.AperiodicArrivalManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RTS extends StatusObservable {
	private ProcessBuilder builder;
	private Process process;
	
	private ObservableList<TaskInstance> tasks;
	private PeriodicTask statTask;
	private PeriodicTask idleTask;
	
	private int serverCapacity;
	private int serverPeriod;
	
	private Thread outputThread;
	private long statInterval;
	private Thread statThread;
	
	private BlockingQueue<Character> visualOutput;
	private BlockingQueue<TickStats> visualStats;
	private AperiodicArrivalManager aperiodicManager;
	
	private BufferedReader outputReader;
	private BufferedReader controlReader;
	private BufferedWriter inputWriter;
	
	public RTS(String systemExecLocation) {
		super();
		builder = new ProcessBuilder(systemExecLocation);
		tasks = FXCollections.observableArrayList();
		statTask = new PeriodicTask("statwriter", TaskCode.getStatCode(), "", "1000");
		idleTask = new PeriodicTask("IDLE", TaskCode.getIdleCode(), "", "4294967295");
	}

	public void setVisualOutput(BlockingQueue<Character> visualOutput) {
		this.visualOutput = visualOutput;
	}

	public void setVisualStats(BlockingQueue<TickStats> visualStats) {
		this.visualStats = visualStats;
	}
	
	public void setAperiodicManager(AperiodicArrivalManager aperiodicManager) {
		this.aperiodicManager = aperiodicManager;
	}

	public ObservableList<TaskInstance> getTasks() {
		return tasks;
	}
	
	private void readInstance(Task task) throws IOException, RTOSException {
		String response = controlReader.readLine();
		if(response.startsWith("Handle: ")) {
			TaskInstance instance = new TaskInstance(task, response);
			tasks.add(instance);
			if(task instanceof AperiodicTask) {
				aperiodicManager.addTask(instance);
			}
		} else {
			RTOSException e = new RTOSException(response);
			if(e.getErrorCode() == ErrorCode.SCHEDULE_NOT_FEASIBLE) {
				tasks.clear();
			}
			throw e;
		}
	}
	
	public void start() throws IOException {
		if(process == null) {
			tasks.clear();
			process = builder.start();
			inputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			controlReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			statInterval = 0;
			
			updateStatus(Status.STARTED);
		}
	}
	
	public void refreshTaskCodes() throws RTOSException, IOException {
		String command = "recalculate_wcet";
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		command = "show_task_codes";
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		String response = controlReader.readLine();
		int taskCodeLength;
		try {
			taskCodeLength = Integer.parseUnsignedInt(response);
		} catch(NumberFormatException e) {
			throw new RTOSException(response);
		}
		TaskCode.getFunctions().clear();
		for(int i = 0; i < taskCodeLength; i++) {
			response = controlReader.readLine();
			String[] codeAttr = response.split(" ");
			TaskCode taskCode = new TaskCode(codeAttr[0], codeAttr[1]);
			TaskCode.getFunctions().add(taskCode);
		}
	}
	
	public void setStats(int interval) throws RTOSException, IOException
	{
		String command = "configure_stats " + Integer.toUnsignedString(interval);
		inputWriter.write(command);
		inputWriter.newLine();
		inputWriter.flush();
		readInstance(statTask);
		statInterval = Integer.toUnsignedLong(interval);
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
			readInstance(task);
		}
	}
	
	public void removeTasks(List<TaskInstance> selectedTasks) throws RTOSException, IOException, StateException {
		if(status != Status.ACTIVE) {
			throw StateException.factory(Status.ACTIVE);
		}
		// Send message to FreeRTOS
		for(TaskInstance task : selectedTasks) {
			String command = task.deleteTask();
			inputWriter.write(command);
			inputWriter.newLine();
			inputWriter.flush();
			tasks.remove(task);
		}
	}
	
	public int getMaxCapacity(int period) throws RTOSException, IOException, StateException {
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
			int capacity = Integer.parseUnsignedInt(response);
			return capacity;
		} catch(NumberFormatException e) {
			throw new RTOSException(response);
		}
	}
	
	public void initialiseServer(int serverCapacity, int serverPeriod) throws RTOSException, IOException, StateException {
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
		readInstance(idleTask);
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
		
		if(statInterval != 0) {
			// Initialise stat stream
			statThread = new Thread(() -> {
				File file = new File(System.getProperty("user.dir") + File.separator + "log.txt");
				try {
					Thread.sleep(statInterval - 1);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(process != null) {
					try (BufferedReader statsReader = new BufferedReader(new FileReader(file))) {
						String line;
						while((line = statsReader.readLine()) != null) {
							String[] stats = line.split(" ");
							int tick = Integer.parseUnsignedInt(stats[0]);
							String handle = stats[1];
							int capacity = Integer.parseUnsignedInt(stats[2]);
							boolean marker = Integer.parseInt(stats[3]) == 1 ? true : false;
							
							TickStats tickStats = new TickStats(tick, handle, capacity, marker);
							visualStats.put(tickStats);
						}
						Thread.sleep(statInterval);
					} catch (IOException e) {
						return;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			statThread.start();
		}
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
