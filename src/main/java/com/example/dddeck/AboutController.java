package com.example.dddeck;

import javafx.fxml.FXML;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import java.awt.Desktop;


import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.control.Hyperlink;


public class AboutController {

    @FXML
    Label versionLabel;

    @FXML
    private Hyperlink githubLink;

    @FXML
    private Hyperlink authorLink;

    public void initialize(){
        String version = App.getVersion();
        versionLabel.setText("FlyingDeck: " + version);
        githubLink.setOnAction(e -> openLink("https://github.com/e6aluga/FlyingDeck"));
        authorLink.setOnAction(e -> openLink("https://github.com/e6aluga"));


    }

        // Метод для открытия ссылки в браузере
        private void openLink(String url) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Desktop is not supported.");
            
}
}
}
