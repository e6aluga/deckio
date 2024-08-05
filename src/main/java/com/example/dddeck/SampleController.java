package com.example.dddeck;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SampleController {

    @FXML
    private Label label;

    @FXML
    protected void handleButtonAction(ActionEvent event) {
        label.setText("Button clicked!");
    }
}
