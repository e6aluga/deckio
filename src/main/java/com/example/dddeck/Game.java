package com.example.dddeck;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.time.chrono.ThaiBuddhistChronology;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.*;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class Game {

    private String name;
    private String game;
    private String pcLocation;
    private String sdLocation;
    private String host;
    private String user;
    private String password;

    private SSHManager sshManager;

    @FXML
    private Label gameNameLabel;

    @FXML
    private Label pcLocationLabel;

    @FXML
    private Label sdLocationLabel;

    @FXML
    private Label sdStatusLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextArea console;

    public Game() {
    }

    @FXML
    public void setName(String game) {
        this.game = game;
        if (gameNameLabel != null) {
            gameNameLabel.setText(game);
        }
    }

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        console.setWrapText(true);
        console.setEditable(false);
        
    }

    private void appendToConsole(String message) {
        console.appendText(message + "\n");
    }

    public void setSSHManager(SSHManager sshManager) {
        this.sshManager = sshManager;
    }

    public void init(String name, SSHManager sshManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();
            Game controller = loader.getController();
            controller.setName(name);
            controller.setSSHManager(sshManager);
            controller.loadGameConfig(name);
            controller.loadSteamDeckSettings();
            controller.updateLabels();

            Stage stage = new Stage();
            stage.setTitle(name);
            this.name = name;

            InputStream iconStream = getClass().getClassLoader().getResourceAsStream("128.png");

            if (iconStream == null) {
                System.out.println("Icon not found!");
            } else {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
                System.out.println("Icon loaded successfully!");
            }

            stage.setScene(new Scene(root, 700, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameConfig(String name) {
        System.out.println(App.timestamp() + "loadGameConfig()");
        App.logToFile(App.timestamp() + "loadGameConfig()");
        Gson gson = new Gson();
        String filepath = String.format("configs/%s", name);
        Map<String, String> map = null;

        try (FileReader fileReader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            map = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(App.timestamp() + "Game settings " + map);
        App.logToFile(App.timestamp() + "Game settings " + map);
        if (map != null) {
            this.pcLocation = map.get("saveLocationPC");
            this.sdLocation = map.get("saveLocationSteamDeck");
            this.name = map.get("gameName");
        }
    }

    private void loadSteamDeckSettings(){
        System.out.println(App.timestamp() + "loadSteamDeckSettings()");
        App.logToFile(App.timestamp() + "loadSteamDeckSettings()");
        Gson gson = new Gson();
        String filepath = "settings.json";
        Map<String, String> map = null;

        try (FileReader fileReader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            map = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (map != null) {
            this.host = map.get("deckIp");
            this.user = map.get("deckUser");
            this.password = map.get("deckPassword");
        }
    }

    private void updateLabels() {
        if (pcLocation != null) {
            pcLocationLabel.setText("PC saves folder location: " + pcLocation);
        }
        if (sdLocation != null) {
            sdLocationLabel.setText("Steam Deck saves folder location: " + sdLocation);
        }
    }

    public void steamDeckToPc() {
        appendToConsole(App.timestamp() + "Starting to copy files from the Steam Deck...");
        Task<Void> steamDeckToPcTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                progressIndicator.setVisible(true);
                App.getSaveFromSD(name, pcLocation, sdLocation, host, user, password);
                return null;
            }
        };
    
        steamDeckToPcTask.setOnSucceeded(event -> {
            appendToConsole(App.timestamp() + "Task completed!");
            progressIndicator.setVisible(false);
    
        });

        steamDeckToPcTask.setOnFailed(event -> {
            appendToConsole(App.timestamp() + "Task failed: " + steamDeckToPcTask.getException());
            Throwable exception = steamDeckToPcTask.getException();
            exception.printStackTrace();
        });
    
        new Thread(steamDeckToPcTask).start();
    }

public void pcToSteamDeck() {
    appendToConsole(App.timestamp() + "Starting to copy files to the Steam Deck...");
    Task<Void> pcToSteamDeckTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
            progressIndicator.setVisible(true);
            BackupManager backupManager = new BackupManager();
            String path = String.format("backups/[SD] " + App.timestamp_() + " " + name);
            backupManager.backupSaveFromSD(name, sdLocation, path, host, user, password);
            copyFilesToSd(sdLocation, pcLocation);
            return null;
        }
    };

    pcToSteamDeckTask.setOnSucceeded(event -> {
        appendToConsole(App.timestamp() + "Task completed!");
        System.out.println("Task completed successfully!");
        App.logToFile("Task completed successfully!");
        progressIndicator.setVisible(false);
    });

    pcToSteamDeckTask.setOnFailed(event -> {
        appendToConsole(App.timestamp() + "Task failed!" + pcToSteamDeckTask.getException());
        Throwable exception = pcToSteamDeckTask.getException();
        exception.printStackTrace();
    });

    new Thread(pcToSteamDeckTask).start();
}
    public void copyFilesToSd(String remoteDirectoryPath, String localDirectoryPath) {
        Session session = null;
        ChannelExec execChannel = null;
        ChannelSftp sftpChannel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(this.user, this.host, 22);
            session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            String command = String.format("find %s -mindepth 1 -delete", remoteDirectoryPath);

            execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand(command);
            execChannel.setInputStream(null);
            execChannel.setErrStream(System.err);

            java.io.InputStream in = execChannel.getInputStream();
            execChannel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                    App.logToFile(new String(tmp, 0, i));
                    appendToConsole(App.timestamp() + new String(tmp, 0, i));
                    
                }
                if (execChannel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("Exit Status: " + execChannel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            execChannel.disconnect();
            appendToConsole(App.timestamp() + "Folder cleared");
            System.out.println("Folder cleared");
            App.logToFile("Folder cleared");

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            copyFilesFromRemote(sftpChannel, remoteDirectoryPath, localDirectoryPath);

            copyFilesAndDirectoriesToRemote(sftpChannel, localDirectoryPath, remoteDirectoryPath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (execChannel != null && execChannel.isConnected()) {
                execChannel.disconnect();
            }
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private void copyFilesFromRemote(ChannelSftp sftpChannel, String remoteDirectoryPath, String localDirectoryPath) throws SftpException, Exception {
        Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(remoteDirectoryPath);

        Path localDirPath = Paths.get(localDirectoryPath);
        if (!Files.exists(localDirPath)) {
            Files.createDirectories(localDirPath);
        }

        for (ChannelSftp.LsEntry entry : files) {
            if (!entry.getAttrs().isDir()) {
                String remoteFilePath = remoteDirectoryPath + "/" + entry.getFilename();
                String localFilePath = localDirectoryPath + "/" + entry.getFilename();
                try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
                    sftpChannel.get(remoteFilePath, fos);
                    System.out.println("Download complete!: " + entry.getFilename());
                    App.logToFile("Download complete!: " + entry.getFilename());
                    appendToConsole(App.timestamp() + "Download complete!: " + entry.getFilename());
                }
            }
        }
    }

    private void copyFilesAndDirectoriesToRemote(ChannelSftp sftpChannel, String localDirectoryPath, String remoteDirectoryPath) throws SftpException, Exception {
        Files.walk(Paths.get(localDirectoryPath)).forEach(path -> {
            try {
                if (Files.isDirectory(path)) {
                    String remoteDirPath = remoteDirectoryPath + "/" + Paths.get(localDirectoryPath).relativize(path).toString().replace("\\", "/");
                    try {
                        sftpChannel.mkdir(remoteDirPath);
                        System.out.println("folder created: " + remoteDirPath);
                        App.logToFile("folder created: " + remoteDirPath);
                        appendToConsole(App.timestamp() + "folder created: " + remoteDirPath);
                    } catch (SftpException e) {
                    }
                } else if (Files.isRegularFile(path)) {
                    String remoteFilePath = remoteDirectoryPath + "/" + Paths.get(localDirectoryPath).relativize(path).toString().replace("\\", "/");
                    sftpChannel.put(path.toString(), remoteFilePath);
                    System.out.println("File uploaded: " + remoteFilePath);
                    App.logToFile("File uploaded: " + remoteFilePath);
                    appendToConsole(App.timestamp() + "File uploaded: " + remoteFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}



    


