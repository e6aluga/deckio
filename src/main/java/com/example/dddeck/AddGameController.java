package com.example.dddeck;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddGameController {

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
        System.out.println(App.timestamp() + "AddGameController initialize()");
    }

    @FXML
    public void saveGame() {
        System.out.println(App.timestamp() + "AddGameController saveGame()");
        String configName = configNameField.getText();
        String gameName = gameNameField.getText();
        String saveLocationPC = saveLocationPCField.getText();
        String saveLocationSteamDeck = saveLocationSteamDeckField.getText();

        addConfig(configName, gameName, saveLocationPC, saveLocationSteamDeck);

        Stage stage = (Stage) configNameField.getScene().getWindow();
        stage.close();
    }


    public void addConfig(String configName, String gameName, String saveLocationPC, String saveLocationSteamDeck) {
        System.out.println(App.timestamp() + "AddGameController addConfig()");
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
            System.out.println(App.timestamp() + jsonString);
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
