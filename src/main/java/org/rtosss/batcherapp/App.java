package org.rtosss.batcherapp;

import org.rtosss.batcherapp.gui.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(MainView.getInstance(), 1280, 720);
        stage.setScene(scene);
        stage.setTitle("FreeRTOS Sporadic Server Batcher");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}