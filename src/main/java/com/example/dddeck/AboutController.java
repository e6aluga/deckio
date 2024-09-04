package com.example.dddeck;

import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;


public class AboutController {

    @FXML
    Label versionLabel;

    @FXML
    private Hyperlink githubLink;

    @FXML
    private Hyperlink authorLink;

    @FXML
    private Hyperlink updatesLink;

    public void initialize(){
        String version = App.getVersion();
        versionLabel.setText("deckio: " + version);
        githubLink.setOnAction(e -> openLink("https://github.com/e6aluga/deckio"));
        authorLink.setOnAction(e -> openLink("https://github.com/e6aluga"));
        updatesLink.setOnAction(e -> openLink("https://github.com/e6aluga/deckio/releases"));
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
        
        }
    }
}
