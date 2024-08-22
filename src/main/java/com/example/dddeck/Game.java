package com.example.dddeck;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class Game {

    private String game;

    @FXML
    private Label gameNameLabel;

    public Game() {
        // Конструктор по умолчанию. Здесь не нужно ничего передавать.
    }

    // Этот метод будет вызван после загрузки FXML
    @FXML
    public void setName(String game) {
        System.out.println("game is: " + game);
        this.game = game;
        if (gameNameLabel != null) {
            gameNameLabel.setText(game);  // Устанавливаем текст в метку
        }
    }

    @FXML
    public void initialize() {
        // Здесь можно вызвать другие начальные действия, если нужно
    }

    public void init(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();

            // Получаем контроллер, который был создан автоматически
            Game controller = loader.getController();
            controller.setName(name);  // Передаём имя игры в контроллер

            Stage stage = new Stage();
            stage.setTitle(name);
            stage.setScene(new Scene(root, 700, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

