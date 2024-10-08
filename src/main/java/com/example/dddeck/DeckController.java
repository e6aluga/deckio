package com.example.dddeck;

import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;

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
    private Hyperlink deckHelpLink;

    SSHManager sshManager;
    DeckData deckData;

    @FXML
    private void initialize(){
        System.out.println(App.timestamp() + "DeckController initialize()");
        App.logToFile(App.timestamp() + "DeckController initialize()");
        deckHelpLink.setOnAction(e -> openLink("https://github.com/e6aluga/deckio?tab=readme-ov-file#how-to-setup-ssh-on-your-steam-deck"));

    }

    @FXML
    private void createDeckConfig(){
        System.out.println(App.timestamp() + "DeckController createDeckConfig()");
        App.logToFile(App.timestamp() + "DeckController createDeckConfig()");
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
            App.logToFile(App.timestamp() + "creating settings.json");
    }

    public void setDeckData(DeckData deckData){
        this.deckData = deckData;
    }
    
    private void openLink(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported.");
            App.logToFile("Desktop is not supported.");
        }
    }

}
