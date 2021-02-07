package org.rtosss.batcherapp.gui;

import java.util.Optional;

import org.rtosss.batcherapp.gui.components.ExceptionHandler;
import org.rtosss.batcherapp.model.AperiodicTask;
import org.rtosss.batcherapp.model.Batch;
import org.rtosss.batcherapp.model.PeriodicTask;
import org.rtosss.batcherapp.model.RTS;
import org.rtosss.batcherapp.model.Task;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class BatcherView extends BorderPane implements IStatusObserver {
	private RTS system;
	
	private ObservableList<Task> taskList;
	private TableView<Task> taskTable;
	
	private Button addTask;
	private Button removeTask;
	private Button createBatch;
	
	private HBox bottom;
	
	public BatcherView() {
		super();
		
		taskTable = new TableView<>();
		taskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		TableColumn<Task, String> pname = new TableColumn<>("Task Name");
		pname.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Task, String> pfunc = new TableColumn<>("Function");
		pfunc.setCellValueFactory(new PropertyValueFactory<>("func"));
		TableColumn<Task, Integer> pcompTime = new TableColumn<>("Worst-Case Running Time");
		pcompTime.setCellValueFactory(new PropertyValueFactory<>("computationTime"));
		
		TableColumn<Task, String> period = new TableColumn<>("Period");
		period.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Task, String> p) {
				if(p.getValue() instanceof PeriodicTask) {
					Integer period = ((PeriodicTask)p.getValue()).getPeriod();
					return new ReadOnlyStringWrapper(Integer.toUnsignedString(period));
				}
				return new ReadOnlyStringWrapper("Aperiodic");
			}
			
		});
		
		TableColumn<Task, String> arrivalOffset = new TableColumn<>("Arrival Offset");
		arrivalOffset.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Task, String> p) {
				if(p.getValue() instanceof AperiodicTask) {
					Integer period = ((AperiodicTask)p.getValue()).getArrivalOffset();
					return new ReadOnlyStringWrapper(Integer.toUnsignedString(period));
				}
				return new ReadOnlyStringWrapper("Periodic");
			}
			
		});
		
		taskTable.getColumns().add(pname);
		taskTable.getColumns().add(pfunc);
		taskTable.getColumns().add(pcompTime);
		taskTable.getColumns().add(period);
		taskTable.getColumns().add(arrivalOffset);
		
		taskTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		taskList = FXCollections.observableArrayList();
		taskTable.setItems(taskList);
		
		createBatch = new Button("Send Batch");
		createBatch.setDisable(true);
		createBatch.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Batch batch = new Batch(taskTable.getSelectionModel().getSelectedItems());
				try {
					system.sendBatch(batch);
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
					Task task = result.get();
					taskList.add(task);
				}
			}
			
		});
		removeTask = new Button("Remove Tasks");
		removeTask.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				taskList.removeAll(taskTable.getSelectionModel().getSelectedItems());
			}
			
		});
		
		bottom = new HBox(createBatch, addTask, removeTask);
		bottom.setPadding(new Insets(20));
		bottom.setSpacing(8);
		bottom.setAlignment(Pos.CENTER);
		
		this.setCenter(taskTable);
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
