package com.example.dddeck;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;


// main window
public class StartController {
 
// экспорт FXML элементов   
@FXML
private ListView<String> listView;
private ExecutorService executorService;
private String selectedItem;
 
@FXML
public void initialize(){

    // System.out.println(App.timestamp() + "StartController initialize()");
    // SSHManager sshManager = new SSHManager();
    // String output = sshManager.sshStatus("192.168.2.74", "deck", "1234", "22", "date");
    // System.out.println(App.timestamp() + output);


    ContextMenu contextMenu = new ContextMenu();

    MenuItem menuItem1 = new MenuItem("Edit");
    MenuItem menuItem2 = new MenuItem("Delete");

    menuItem1.setOnAction(event -> handleMenuAction("editConfig"));
    menuItem2.setOnAction(event -> handleMenuAction("deleteConfig"));

    contextMenu.getItems().addAll(menuItem1, menuItem2);

    // фабрика ячеек ListView
    listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>(){
        @Override
        public ListCell<String> call (ListView<String> listView){
            ListCell<String> cell = new ListCell<>(){
                @Override
                protected void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    if (empty || item == null){
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(item);
                        setContextMenu(contextMenu);
                    }
                }
            };
            // обработчик событий на ListView
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    selectedItem = cell.getItem();
                    System.out.println(App.timestamp() + "ListView selectedItem: " + selectedItem);
                }
            });
            return cell;
        };
    });


    
    String directoryPath = "configs/";
    updateListView(directoryPath);

    // инициализируем и запускаем WatchService для мониторинга изменений
    executorService = Executors.newSingleThreadExecutor();
    startWatching(directoryPath);
}

@FXML
public void openAddGameWindow(){
    System.out.println(App.timestamp() + "StartController openAddGameWindow()");
    try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addgame.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Add game");
        stage.setScene(new Scene(root, 700, 300));
        stage.show();
    } catch (IOException e){
        e.printStackTrace();
    }
}

@FXML
public void openDeckWindow(){
    System.out.println(App.timestamp() + "StartController openDeckWindow()");
    try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/deck.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Steam Deck Settings");
        stage.setScene(new Scene(root, 700, 300));
        stage.show();
    } catch (IOException e){
        e.printStackTrace();
    }
}

private void handleMenuAction(String function) {
    String selectedItem = listView.getSelectionModel().getSelectedItem();
    System.out.println("Selected item: " + selectedItem + " - Action: " + function);
    if (selectedItem != null) {
        switch (function){
            case "editConfig":
                editConfig(selectedItem);
                break;
            case "deleteConfig":
                deleteConfig(selectedItem);
                break;
            default:
                System.out.println(App.timestamp() + "Unknown function" + function);
                break;
        }
        } else {
            System.out.println(App.timestamp() + "No item selected - Action: " + function);
        }
}

    private void updateListView(String directoryPath) {
        File directory = new File(directoryPath);
        String[] fileNames = directory.list();

        if (fileNames != null) {
            Platform.runLater(() -> {
                listView.getItems().clear();
                listView.getItems().addAll(fileNames);
            });
        }
    }
// Обработчик событий в ListView для динамического обновления списка
    private void startWatching(String directoryPath) {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(directoryPath);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

                executorService.submit(() -> {
                    try {
                        WatchKey key;
                        while ((key = watchService.take()) != null) {
                            for (WatchEvent<?> event : key.pollEvents()) {
                                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE ||
                                    event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                                    updateListView(directoryPath);
                                }
                            }
                            key.reset();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
// функция для остановки WatchService
    public void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
// функция в контекстном меню ListView для удаления конфига
    private boolean deleteConfig(String selectedItem){
        System.out.println(App.timestamp() + "StartController deleteConfig()");
        File file = new File(String.format("configs/%s", selectedItem));
        if (file.exists()){
            return file.delete();
        } else {
            System.out.println(App.timestamp() + "File not found: " + selectedItem);
            return false;
        }
    }

// функция для редактирования конфига в ListView
    private void editConfig(String selectedItem){
        System.out.println(App.timestamp() + "StartController editConfig()");
        //todo
        EditConfigController editConfigController = new EditConfigController();
        editConfigController.setConfigName(selectedItem);
        editConfigController.openUpdateConfigWindow(selectedItem);
    }
    @FXML
    private void openBackupsAction() {
       App.getSaveFromSD("PC", "FlyingDeck");
    }
}

