package com.example.dddeck;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Game {

    private String game;
    private String pcLocation;
    private String sdLocation;
    private String host;
    private String user;
    private String password;

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

    public void init(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();

            // Получаем контроллер, который был создан автоматически
            Game controller = loader.getController();
            controller.setName(name);  // Передаём имя игры в контроллер

            // Загружаем конфигурацию игры после установки имени
            controller.loadGameConfig(name);
            controller.loadSteamDeckSettings();
            controller.updateLabels();
            controller.checkSdStatus();

            Stage stage = new Stage();
            stage.setTitle(name);
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
            pcLocationLabel.setText(pcLocation);
        }
        if (sdLocation != null) {
            sdLocationLabel.setText(sdLocation);
        }
    }

    private boolean checkSdStatus(){
        SSHManager sshManager = new SSHManager();
        String status = sshManager.sshExec(this.host, this.user, this.password, "22", "date");
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
}



    


