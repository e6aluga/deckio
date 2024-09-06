package com.example.dddeck;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditConfigController {

    private String configName;

    @FXML
    private TextField editConfigNameField;

    @FXML
    private TextField editGameNameField;

    @FXML
    private TextField editSaveLocationPCField;

    @FXML
    private TextField editSaveLocationSteamDeckField;

    public void openUpdateConfigWindow(String configName) {

        Gson gson = new Gson();
        this.configName = configName;
    
        try (FileReader reader = new FileReader(String.format("configs/%s", configName))) {
            ConfigData configData = gson.fromJson(reader, ConfigData.class);
    
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editgame.fxml"));
            Parent root = loader.load();
    
            // Получаем контроллер, связанный с загруженным FXML
            EditConfigController controller = loader.getController();
    
            controller.setConfigName(configName); 

            controller.editConfigNameField.setText(configData.getName());
            controller.editGameNameField.setText(configData.getGameName());
            controller.editSaveLocationPCField.setText(configData.getSaveLocationPC());
            controller.editSaveLocationSteamDeckField.setText(configData.getSaveLocationSteamDeck());

            Stage stage = new Stage();
            stage.setTitle("Edit Game");
            stage.setScene(new Scene(root, 700, 400));
            stage.show();

            this.configName = configName;
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private void handleSaveButtonAction() {
        updateConfig(); 
        Stage stage = (Stage) editConfigNameField.getScene().getWindow();
        stage.close();
    }

    public void setConfigName(String configName){
        this.configName = configName;
    }

    public void updateConfig() {
        String newConfigName = editConfigNameField.getText();
        String newGameName = editGameNameField.getText();
        String newSaveLocationPC = editSaveLocationPCField.getText();
        String newSaveLocationSteamDeck = editSaveLocationSteamDeckField.getText();
        Gson gson = new Gson();
        try {
            String configFilePath = String.format("configs/%s", configName);
            FileReader reader = new FileReader(configFilePath);
            ConfigData configData = gson.fromJson(reader, ConfigData.class);
            reader.close();

            configData.setName(newConfigName);
            configData.setGameName(newGameName);
            configData.setSaveLocationPC(newSaveLocationPC);
            configData.setSaveLocationSteamDeck(newSaveLocationSteamDeck);

            FileWriter writer = new FileWriter(configFilePath);

            gson.toJson(configData, writer);

            writer.close();

            System.out.println(App.timestamp() + "Config updated successfully!");
            App.logToFile(App.timestamp() + "Config updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
