package com.example.dddeck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

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

    public static String timestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String time = String.format("%s ", dtf.format(LocalDateTime.now()));
        return time;
    }

    public static String timestamp_(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH:mm:ss");
        String time = String.format("%s", dtf.format(LocalDateTime.now()));
        return time;
    }

    public static void timer(int time) {
        try {
            // time указывается в миллисекундах
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Timer executed");
    }

        public static void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.forEach(sourcePath -> {
                try {
                    Path targetPath = targetDir.resolve(sourceDir.relativize(sourcePath));
                    if (Files.isDirectory(sourcePath)) {
                        if (!Files.exists(targetPath)) {
                            Files.createDirectory(targetPath);
                        }
                    } else {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to copy: " + sourcePath + " -> " + e.getMessage());
                }
            });
        }
    }
    
}