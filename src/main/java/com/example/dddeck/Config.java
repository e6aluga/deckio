package com.example.dddeck;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Config {

    @FXML
    private TextField configNameField;
    @FXML
    private TextField gameNameField;
    @FXML
    private TextField saveLocationPCField;
    @FXML
    private TextField saveLocationSteamDeckField;

    @FXML
    private void initialize() {
        // Метод initialize вызывается автоматически после того, как все элементы FXML инициализированы
    }

    @FXML
    private void saveGame() {
        String configName = configNameField.getText();
        String gameName = gameNameField.getText();
        String saveLocationPC = saveLocationPCField.getText();
        String saveLocationSteamDeck = saveLocationSteamDeckField.getText();

        addConfig(configName, gameName, saveLocationPC, saveLocationSteamDeck);
    }

    private void addConfig(String configName, String gameName, String saveLocationPC, String saveLocationSteamDeck) {
        ConfigData configData = new ConfigData();
        
        configData.setName(configName);
        configData.setGameName(gameName);
        configData.setSaveLocationPC(saveLocationPC);
        configData.setSaveLocationSteamDeck(saveLocationSteamDeck);
        
        System.out.println(configData.getData());

        // Создаем объект Gson для работы с JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            // Запись данных в JSON файл
            String jsonFileName = String.format("%s.json", configData.getName());
            try (FileWriter writer = new FileWriter(jsonFileName)) {
                gson.toJson(configData, writer);
            }

            // Конвертируем объект в JSON строку
            String jsonString = gson.toJson(configData);
            System.out.println(jsonString);
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
