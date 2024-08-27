package com.example.dddeck;

import java.io.FileReader;
import java.io.IOException;
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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    public Game() {
        // Конструктор по умолчанию
    }

    @FXML
    public void setName(String game) {
        this.game = game;
        if (gameNameLabel != null) {
            gameNameLabel.setText(game);  // Устанавливаем текст в метку
        }
    }

    @FXML
    public void initialize() {
        // Метод инициализации вызывается после загрузки FXML
    }

    public void setSSHManager(SSHManager sshManager) {
        this.sshManager = sshManager;
    }

    public void init(String name, SSHManager sshManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();

            // Получаем контроллер, который был создан автоматически
            Game controller = loader.getController();
            controller.setName(name);  // Передаём имя игры в контроллер
            controller.setSSHManager(sshManager); // Передаём SSHManager в контроллер

            // Загружаем конфигурацию игры после установки имени
            controller.loadGameConfig(name);
            controller.loadSteamDeckSettings();
            controller.updateLabels();
            // controller.checkSdStatus();

            Stage stage = new Stage();
            stage.setTitle(name);
            this.name = name;
            stage.setScene(new Scene(root, 700, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameConfig(String name) {
        System.out.println(App.timestamp() + "loadGameConfig()");
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
        if (map != null) {
            this.pcLocation = map.get("saveLocationPC");
            this.sdLocation = map.get("saveLocationSteamDeck");
        }
    }

    private void loadSteamDeckSettings(){
        System.out.println(App.timestamp() + "loadSteamDeckSettings()");
        Gson gson = new Gson();
        String filepath = "settings.json";
        Map<String, String> map = null;

        try (FileReader fileReader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            map = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(App.timestamp() + "Steam Deck Settings: " + map);

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

    private boolean checkSdStatus(){
        String status = sshManager.sshExec(sshManager.getSession(), "date");
        if (status != null){
            System.out.println(App.timestamp() + "checkSdStatus: successfull");
            sdStatusLabel.setText("Steam Deck status: connected!");
            return true;
            
        } else {
            System.out.println(App.timestamp() + "checkSdStatus: error");
            sdStatusLabel.setText("Steam Deck status: error!");
            return false;
        }
    }

    public void steamDeckToPc(){
        App.getSaveFromSD(this.name, this.pcLocation, this.sdLocation, this.host, this.user, this.password);
        
    }

    public void pcToSteamDeck(){
        BackupManager backupManager = new BackupManager();
        String path = String.format("backups/[SD] " + App.timestamp_() + " " + this.name);
        backupManager.backupSaveFromSD(this.name, this.sdLocation, path, this.host, this.user, this.password);
        copyFilesToSd(this.sdLocation, this.pcLocation);
        
    }

    public void copyFilesToSd(String remoteDirectoryPath, String localDirectoryPath) {
        Session session = null;
        ChannelExec execChannel = null;
        ChannelSftp sftpChannel = null;

        try {
            // Устанавливаем соединение
            JSch jsch = new JSch();
            session = jsch.getSession(this.user, this.host, 22);
            session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Создаем команду для очистки папки
            String command = String.format("find %s -mindepth 1 -delete", remoteDirectoryPath);

            // Выполняем команду очистки папки
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
                }
                if (execChannel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("Exit Status: " + execChannel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            execChannel.disconnect();

            System.out.println("Папка успешно очищена.");

            // Подключаемся к SFTP каналу
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // Копируем файлы с удаленного устройства на локальный ПК
            copyFilesFromRemote(sftpChannel, remoteDirectoryPath, localDirectoryPath);

            // Отправляем файлы и папки обратно в очищенную папку
            copyFilesAndDirectoriesToRemote(sftpChannel, localDirectoryPath, remoteDirectoryPath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Освобождаем ресурсы
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

        // Создаем локальную папку, если она не существует
        Path localDirPath = Paths.get(localDirectoryPath);
        if (!Files.exists(localDirPath)) {
            Files.createDirectories(localDirPath);
        }

        for (ChannelSftp.LsEntry entry : files) {
            if (!entry.getAttrs().isDir()) {
                // Копируем каждый файл с удаленного устройства на локальный ПК
                String remoteFilePath = remoteDirectoryPath + "/" + entry.getFilename();
                String localFilePath = localDirectoryPath + "/" + entry.getFilename();
                try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
                    sftpChannel.get(remoteFilePath, fos);
                    System.out.println("Скачан файл: " + entry.getFilename());
                }
            }
        }
    }

    private void copyFilesAndDirectoriesToRemote(ChannelSftp sftpChannel, String localDirectoryPath, String remoteDirectoryPath) throws SftpException, Exception {
        Files.walk(Paths.get(localDirectoryPath)).forEach(path -> {
            try {
                if (Files.isDirectory(path)) {
                    // Создаем папку на удаленном устройстве
                    String remoteDirPath = remoteDirectoryPath + "/" + Paths.get(localDirectoryPath).relativize(path).toString().replace("\\", "/");
                    try {
                        sftpChannel.mkdir(remoteDirPath);
                        System.out.println("Создана папка: " + remoteDirPath);
                    } catch (SftpException e) {
                        // Папка может уже существовать, игнорируем ошибку
                    }
                } else if (Files.isRegularFile(path)) {
                    // Загружаем файл на удаленное устройство
                    String remoteFilePath = remoteDirectoryPath + "/" + Paths.get(localDirectoryPath).relativize(path).toString().replace("\\", "/");
                    sftpChannel.put(path.toString(), remoteFilePath);
                    System.out.println("Загружен файл: " + remoteFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}



    


