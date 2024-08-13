package com.example.dddeck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/start.fxml"));
        primaryStage.setTitle("FlyingDeck");
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.show();  
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}