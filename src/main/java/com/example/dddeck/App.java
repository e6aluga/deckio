package com.example.dddeck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class App extends Application {

    String version = "1.0.0-beta.1";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 300, 400);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application is closing...");
            Platform.exit();
            System.exit(0);
        });

        primaryStage.setTitle("deckio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    public static String timestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = String.format("%s ", dtf.format(LocalDateTime.now()));
        return time;
    }

    public static String timestamp_(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH_mmss");
        String time = String.format("%s", dtf.format(LocalDateTime.now()));
        return time;
    }
    public static String timestamp__(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM dd_HH_mm_ss");
        String time = String.format("%s", dtf.format(LocalDateTime.now()));
        return time;
    }

    public static void timer(int time) {
        try {
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
    public static void getSaveFromSD(String name, String pcDir, String sdDir, String host, String user, String password){
        System.out.println(timestamp() + "Getting saves from Steam Deck");
        App.logToFile(timestamp() + "Getting saves from Steam Deck");

        BackupManager backupManager = new BackupManager();
        SSHManager sshManager = new SSHManager();
        File folder = new File("backups"); // директория, где хранятся сейвы игры для очистки
        File savePcFolder = new File(pcDir);

        String pcDir_ = String.format("%s/", pcDir); // pcDir, но со слешем

        backupManager.backupSaveFromPC(pcDir, name); // делаем бекап перед заменой файлов с дека

        cleanDirectory(savePcFolder); // чистим локальную директорию перед новыми сейвами

        sshManager.getRemoteDir(sdDir, pcDir_, host, user, password); // загружаем сейвы с дека и заменяем файлы новыми

        System.out.println(timestamp() + "Completed!");
        App.logToFile(timestamp() + "Completed!");
    }

    public static void cleanDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        cleanDirectory(file);
                    }
                    file.delete();
                }
            }
        }
    }

    public static void openFolderInExplorer(String folderPath) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            String command;
            if (os.contains("win")) {
                command = "explorer.exe " + folderPath;
            } else if (os.contains("mac")) {
                command = "open " + folderPath;
            } else if (os.contains("nix") || os.contains("nux")) {
                command = "xdg-open " + folderPath;
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to open folder in file explorer.");
            App.logToFile("Failed to open folder in file explorer.");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            App.logToFile(e.getMessage());
        }
    }

    public static String getVersion(){
        return "1.0.0-beta.1";
    }

    public static void logToFile(String message) {
        try (FileWriter fileWriter = new FileWriter("logs.txt", true); // true 
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}