package com.example.dddeck;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigManager {

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
    }

    @FXML
    public void saveGame() {
        String configName = configNameField.getText();
        String gameName = gameNameField.getText();
        String saveLocationPC = saveLocationPCField.getText();
        String saveLocationSteamDeck = saveLocationSteamDeckField.getText();

        addConfig(configName, gameName, saveLocationPC, saveLocationSteamDeck);
    }


    public void addConfig(String configName, String gameName, String saveLocationPC, String saveLocationSteamDeck) {
        ConfigData configData = new ConfigData();
        
        configData.setName(configName);
        configData.setGameName(gameName);
        configData.setSaveLocationPC(saveLocationPC);
        configData.setSaveLocationSteamDeck(saveLocationSteamDeck);
        
        System.out.println(configData.getData());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            String jsonFileName = String.format("configs/%s.json", configData.getName());
            try (FileWriter writer = new FileWriter(jsonFileName)) {
                gson.toJson(configData, writer);
            }

            String jsonString = gson.toJson(configData);
            System.out.println(jsonString);
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
