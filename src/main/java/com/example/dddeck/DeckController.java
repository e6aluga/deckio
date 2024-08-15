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
        System.out.println(App.timestamp() + "DeckController initialize()");
    }
    @FXML
    private void createDeckConfig(){
        System.out.println(App.timestamp() + "DeckController createDeckConfig()");
        String deckIp = deckIpField.getText();
        String deckUser = deckUserField.getText();
        String deckPassword = deckPasswordField.getText();
        String deckPort = deckPortField.getText();

        DeckData deckData = new DeckData();

        deckData.setIp(deckIp);
        deckData.setUser(deckUser);
        deckData.setPassword(deckPassword);
        deckData.setPort(deckPort);

        SSHManager sshManager = new SSHManager();
        String SSHStatus = sshManager.sshExec(deckIp, deckUser, deckPassword, deckPort, "echo 'status'");
        // System.out.println(App.timestamp() + "Timer start");
        // App.timer(5000);
        // System.out.println(App.timestamp() + "Timer end");
        if (SSHStatus != null){
            Gson gson = new Gson();
            
            try(FileWriter writer = new FileWriter("settings.json")){
                gson.toJson(deckData, writer);
            } catch (IOException e){
                e.printStackTrace();
            }

            Stage stage = (Stage) deckIpField.getScene().getWindow();
            stage.close();
            System.out.println(App.timestamp() + "creating settings.json");
        } else {
            System.out.println(App.timestamp() + "Error: connection refused. Check your login data.");
        }
    }
}
