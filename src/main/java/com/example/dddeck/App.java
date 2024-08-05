package com.example.dddeck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("FXML Example");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();  
    }

    public static void main(String[] args) {
        launch(args);
    }
}