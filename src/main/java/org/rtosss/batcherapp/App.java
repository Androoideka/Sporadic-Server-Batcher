package org.rtosss.batcherapp;

import org.rtosss.batcherapp.gui.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {
	private MainView mainView;
	
    @Override
    public void start(Stage stage) {
    	mainView = new MainView();
        Scene scene = new Scene(mainView, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("FreeRTOS Sporadic Server Batcher");
        stage.show();
        stage.setOnCloseRequest(event -> {
        	mainView.setRTS(null);
        });
    }

    public static void main(String[] args) {
        launch();
    }

}