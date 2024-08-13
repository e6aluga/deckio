package com.example.dddeck;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DeckController {

    @FXML
    private TextField deckIpField;
    @FXML
    private TextField deckUserField;
    @FXML
    private TextField deckPasswordField;
    @FXML
    private TextField deckPortField;

    @FXML
    private void initialize(){
    }
    @FXML
    private void createDeckConfig(){
        String deckIp = deckIpField.getText();
        String deckUser = deckUserField.getText();
        String deckPassword = deckPasswordField.getText();
        String deckPort = deckPortField.getText();

        DeckData deckData = new DeckData();

        deckData.setIp(deckIp);
        deckData.setUser(deckUser);
        deckData.setPassword(deckPassword);
        deckData.setPort(deckPort);
        
        Gson gson = new Gson();
        
        try(FileWriter writer = new FileWriter("settings.json")){
            gson.toJson(deckData, writer);
        } catch (IOException e){
            e.printStackTrace();
        }

        Stage stage = (Stage) deckIpField.getScene().getWindow();
        stage.close();
    }
}
