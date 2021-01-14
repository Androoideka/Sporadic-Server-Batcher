package org.rtosss.batcherapp.gui;

import java.util.Optional;

import org.rtosss.batcherapp.gui.components.ExceptionHandler;
import org.rtosss.batcherapp.model.AperiodicTask;
import org.rtosss.batcherapp.model.Batch;
import org.rtosss.batcherapp.model.PeriodicTask;
import org.rtosss.batcherapp.model.RTS;
import org.rtosss.batcherapp.model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class BatcherView extends BorderPane implements IStatusObserver {
	private RTS system;
	
	private ObservableList<PeriodicTask> periodicTasks;
	private ObservableList<AperiodicTask> aperiodicTasks;
	
	private TableView<PeriodicTask> periodicTaskTable;
	private TableView<AperiodicTask> aperiodicTaskTable;
	
	private HBox tables;
	
	private Button addTask;
	private Button removeTask;
	private Button createBatch;
	
	private HBox bottom;
	
	public BatcherView() {
		super();
		
		periodicTasks = FXCollections.observableArrayList();
		periodicTaskTable = new TableView<>();
		HBox.setHgrow(periodicTaskTable, Priority.ALWAYS);
		periodicTaskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		TableColumn<PeriodicTask, String> pname = new TableColumn<>("Task Name");
		pname.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<PeriodicTask, String> pfunc = new TableColumn<>("Function");
		pfunc.setCellValueFactory(new PropertyValueFactory<>("func"));
		TableColumn<PeriodicTask, String> pcompTime = new TableColumn<>("Worst-Case Running Time");
		pcompTime.setCellValueFactory(new PropertyValueFactory<>("computationTime"));
		TableColumn<PeriodicTask, String> period = new TableColumn<>("Period");
		period.setCellValueFactory(new PropertyValueFactory<>("period"));
		
		periodicTaskTable.getColumns().add(pname);
		periodicTaskTable.getColumns().add(pfunc);
		periodicTaskTable.getColumns().add(pcompTime);
		periodicTaskTable.getColumns().add(period);
		
		periodicTaskTable.setItems(periodicTasks);
		
		aperiodicTasks = FXCollections.observableArrayList();
		aperiodicTaskTable = new TableView<>();
		HBox.setHgrow(aperiodicTaskTable, Priority.ALWAYS);
		aperiodicTaskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		TableColumn<AperiodicTask, String> aname = new TableColumn<>("Task Name");
		aname.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<AperiodicTask, String> afunc = new TableColumn<>("Function");
		afunc.setCellValueFactory(new PropertyValueFactory<>("func"));
		TableColumn<AperiodicTask, String> acompTime = new TableColumn<>("Worst-Case Running Time");
		acompTime.setCellValueFactory(new PropertyValueFactory<>("computationTime"));
		TableColumn<AperiodicTask, String> arrivalTime = new TableColumn<>("Arrival Time");
		arrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
		
		aperiodicTaskTable.getColumns().add(aname);
		aperiodicTaskTable.getColumns().add(afunc);
		aperiodicTaskTable.getColumns().add(acompTime);
		aperiodicTaskTable.getColumns().add(arrivalTime);
		aperiodicTaskTable.setItems(aperiodicTasks);
		
		tables = new HBox(periodicTaskTable, aperiodicTaskTable);
		
		createBatch = new Button("Send Batch");
		createBatch.setDisable(true);
		createBatch.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					system.sendBatch(new Batch(periodicTaskTable.getSelectionModel().getSelectedItems(), aperiodicTaskTable.getSelectionModel().getSelectedItems()));
				} catch(Exception e) {
					ExceptionHandler.showException(e);
				}
			}
			
		});
		addTask = new Button("Add Task");
		addTask.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				TaskCreateDialog dialog = new TaskCreateDialog();
				Optional<Task> result = dialog.showAndWait();
				if(result.isPresent()) {
					if(result.get() instanceof PeriodicTask) {
						PeriodicTask task = (PeriodicTask) result.get();
						periodicTasks.add(task);
					} else if(result.get() instanceof AperiodicTask) {
						AperiodicTask task = (AperiodicTask) result.get();
						aperiodicTasks.add(task);
					}
				}
			}
			
		});
		removeTask = new Button("Remove Tasks");
		removeTask.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				periodicTasks.removeAll(periodicTaskTable.getSelectionModel().getSelectedItems());
				aperiodicTasks.removeAll(aperiodicTaskTable.getSelectionModel().getSelectedItems());
			}
			
		});
		
		bottom = new HBox(createBatch, addTask, removeTask);
		bottom.setPadding(new Insets(20));
		bottom.setSpacing(8);
		bottom.setAlignment(Pos.CENTER);
		
		this.setCenter(tables);
		this.setBottom(bottom);
	}

	@Override
	public void updateStatus(Status status) {
		if(status == Status.UNAVAILABLE || status == Status.LOADED) {
			createBatch.setDisable(true);
		} else {
			createBatch.setDisable(false);
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
