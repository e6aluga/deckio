package com.example.dddeck;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    SSHManager sshManager;
    DeckData deckData;

    @FXML
    private void initialize(){
        System.out.println(App.timestamp() + "DeckController initialize()");

    }


    @FXML
    private void createDeckConfig(){
        System.out.println(App.timestamp() + "DeckController createDeckConfig()");
        String deckIp = deckIpField.getText();
        String deckUser = deckUserField.getText();
        String deckPassword = deckPasswordField.getText();
        String deckPort = deckPortField.getText();

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
            System.out.println(App.timestamp() + "creating settings.json");
    }

    public void setDeckData(DeckData deckData){
        this.deckData = deckData;
    }

}
