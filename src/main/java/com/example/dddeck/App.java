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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузите FXML файл
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        BorderPane root = loader.load();

        // Создайте сцену с корневым элементом и установите ее в Stage
        Scene scene = new Scene(root, 300, 400);
        primaryStage.setTitle("FlyingDeck");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    public static String timestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH mm ss");
        String time = String.format("%s ", dtf.format(LocalDateTime.now()));
        return time;
    }

    public static String timestamp_(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH_mmss");
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
    public static void getSaveFromSD(String name, String pcDir, String sdDir, String host, String user, String password){
        System.out.println(timestamp() + "Getting saves from Steam Deck");

        BackupManager backupManager = new BackupManager();
        SSHManager sshManager = new SSHManager();
        File folder = new File("backups"); // директория, где хранятся сейвы игры для очистки
        File savePcFolder = new File(pcDir);

        String pcDir_ = String.format("%s/", pcDir); // pcDir, но со слешем

        backupManager.backupSaveFromPC(pcDir, "test"); // делаем бекап перед заменой файлов с дека

        cleanDirectory(savePcFolder); // чистим локальную директорию перед новыми сейвами

        sshManager.getRemoteDir(sdDir, pcDir_, host, user, password); // загружаем сейвы с дека и заменяем файлы новыми

        System.out.println(timestamp() + "Completed!");
    }

    public static void cleanDirectory(File directory) {
        // Проверяем, что указанный путь является директорией
        if (directory.isDirectory()) {
            // Получаем список всех файлов и директорий внутри данной директории
            File[] files = directory.listFiles();
            if (files != null) { // Проверяем, что список файлов не равен null
                for (File file : files) {
                    // Если это директория, то рекурсивно очищаем её
                    if (file.isDirectory()) {
                        cleanDirectory(file);
                    }
                    // Удаляем файл или пустую директорию
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
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    
}