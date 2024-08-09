package com.example.dddeck;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


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
    private Button saveButton;

    @FXML
    public void saveGame(){
        String configName = configNameField.getText();
        String gameName = gameNameField.getText();
        String saveLocationPC = saveLocationPCField.getText();
        String saveLocationSteamDeck = saveLocationSteamDeckField.getText();

        ConfigData configData = new ConfigData();
        configData.setName(configName);
        configData.setGameName(gameName);
        configData.setSaveLocationPC(saveLocationPC);
        configData.setSaveLocationSteamDeck(saveLocationSteamDeck);
        System.out.println(configData.getData());

        // Config.addConfig();
    }
}
