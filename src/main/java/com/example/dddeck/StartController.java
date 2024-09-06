package com.example.dddeck;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.jcraft.jsch.Session;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;

public class StartController {

    private SSHManager sshManager;
    private DeckData deckData;
    Session session;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ListView<String> listView;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label status;

    private ExecutorService executorService;
    private String selectedItem;

    public void initialize() {
        deckData = new DeckData();
        deckData.loadSteamDeckSettings();
        initializeContextMenu();
        setupListView();
        initializeMenuBar();
        String directoryPath = "configs/";
        updateListView(directoryPath); 
        startWatching(directoryPath);
    
        System.out.println(App.timestamp() + " SD settings: " + deckData.getIp() + "\n" + deckData.getUser() + "\n" + deckData.getPassword() + "\n" + deckData.getPort());

        App.logToFile(App.timestamp() + " SD settings: " + deckData.getIp() + "\n" + deckData.getUser() + "\n" + deckData.getPassword() + "\n" + deckData.getPort());

        if (deckData.getIp() != null && deckData.getUser() != null && deckData.getPassword() != null) {
            attemptConnection(); // Запускаем подключение
        } else {
            System.out.println(App.timestamp() + "Failed to initialize DeckData. Please check your settings.");
            App.logToFile(App.timestamp() + "Failed to initialize DeckData. Please check your settings.");
            status.setText("Failed to load DeckData.");
        }
    }
    
    private void attemptConnection() {
        Task<Void> sshTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (sshManager == null || sshManager.getSession() == null || !sshManager.getSession().isConnected()) {
                    try {
                        if (sshManager != null) {
                            sshManager.disconnect(session); // Закрываем старую сессию, если она существует
                        }
    
                        // Проверяем, что deckData инициализирован
                        if (deckData == null) {
                            System.out.println(App.timestamp() + "DeckData is still null.");
                            App.logToFile(App.timestamp() + "DeckData is still null.");
                            return null; // Прекращаем задачу, если deckData не инициализирован
                        }
    
                        sshManager = new SSHManager(deckData.getIp(), deckData.getUser(), deckData.getPassword(), 22);
                        session = sshManager.connect();
                        Thread.sleep(2000);
                        if (session != null && session.isConnected()) {
                            break; // Прерываем цикл, если подключение успешно
                        }
    
                    } catch (Exception e) {
                        e.printStackTrace();
                        Thread.sleep(5000); // Задержка перед повторной попыткой
                    }
                }
                return null;
            }
        };
       
        sshTask.setOnSucceeded(event -> {
            if (session != null && session.isConnected()) {
                status.setText("Status: connected!");
                progressIndicator.setVisible(false);
                System.out.println(App.timestamp() + " Successfully connected to SSH.");
                App.logToFile(App.timestamp() + " Successfully connected to SSH.");
            }
        });
    
        sshTask.setOnFailed(event -> {
            Throwable exception = sshTask.getException();
            exception.printStackTrace();
            progressIndicator.setVisible(true);
            status.setText("Status: connection failed, retrying...");
            attemptConnection(); // Если возникла ошибка, пробуем снова
        });
    

        new Thread(sshTask).start();


    }

    public DeckData getDeckData(){
        return this.deckData;
    }

    public SSHManager getSSHManager(){
        return this.sshManager;
    }
    public void openHelpPage(){
        openLink("https://github.com/e6aluga/deckio?tab=readme-ov-file#readme");
    }

    private void openLink(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println(App.timestamp() + "Desktop is not supported."); 
            App.logToFile(App.timestamp() + "Desktop is not supported."); 
        }
}

    private void initializeMenuBar() {
        Menu fileMenu = new Menu("deckio");
        MenuItem addItem = new MenuItem("Add new game");
        MenuItem sdItem = new MenuItem("Steam Deck Settings");
        // MenuItem settingsItem = new MenuItem("App Settings");
        MenuItem backupsItem = new MenuItem("Backups");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exitItem = new MenuItem("Exit");
        addItem.setOnAction(e -> openAddGameWindow());
        sdItem.setOnAction(e -> openDeckWindow());
        backupsItem.setOnAction(e -> openBackups());
        exitItem.setOnAction(e -> System.exit(0));
    
        fileMenu.getItems().addAll(addItem, sdItem, backupsItem, separator, exitItem);

        Menu helpMenu = new Menu("About");
        MenuItem aboutItem = new MenuItem("About");

        aboutItem.setOnAction(e -> {
            openAboutWindow();
            System.out.println(App.timestamp() + " About menu item clicked");
        });

        MenuItem helpItem = new MenuItem("Help");
        helpItem.setOnAction(e -> {
            System.out.println(App.timestamp() + " Help menu item clicked");
            openHelpPage();
        });
    
        helpMenu.getItems().addAll(helpItem, aboutItem);
    
        menuBar.getMenus().addAll(fileMenu, helpMenu);
    }
    
    private void initializeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem addGameItem = new MenuItem("Add");

        addGameItem.setOnAction(event -> handleMenuAction("openAddGameWindow"));
        editItem.setOnAction(event -> handleMenuAction("editConfig"));
        deleteItem.setOnAction(event -> handleMenuAction("deleteConfig"));

        contextMenu.getItems().addAll(addGameItem, editItem, deleteItem);
        listView.setContextMenu(contextMenu);
    }

    private void setupListView() {
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                ListCell<String> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setContextMenu(null);
                        } else {
                            setText(item);
                            setContextMenu(listView.getContextMenu());
                        }
                    }
                };

                // Обработчики кликов на ячейках
                cell.setOnMouseClicked(event -> {
                    if (!cell.isEmpty()) {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            selectedItem = cell.getItem();
                            System.out.println(App.timestamp() + " ListView selectedItem: " + selectedItem);
                        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            if (status.getText().equals("Status: connected!")){
                            openGameWindow(cell.getItem());
                            } else {
                                showErrorAlert("Check your connection");
                            }
                    
                        }
                    }
                });

                return cell;
            }
        });
    }

    @FXML
    public void openAddGameWindow() {
        showWindow("/addgame.fxml", "Add", 700, 430);
    }

    @FXML
    public void openDeckWindow() {
        showWindow("/deck.fxml", "Steam Deck Settings", 700, 370);
    }

    @FXML
    public void openBackupsAction() {
    }

    @FXML
    public void openGameWindow(String name) {
            System.out.println(App.timestamp() + "openGameWindow()");
            Game game = new Game();
            game.init(name, sshManager);
    }

    @FXML
    public void openAboutWindow(){
        System.out.println(App.timestamp() + "openAboutWindow()");
        App.logToFile(App.timestamp() + "openAboutWindow()");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/about.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(loader.load(), 250, 80));    
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), width, height));
    
            Object controller = loader.getController();
            if (controller instanceof DeckController) {
                DeckController deckController = (DeckController) controller;
                deckController.setDeckData(this.deckData);
            }
    
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void handleMenuAction(String action) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        System.out.println("Selected item: " + selectedItem + " - Action: " + action);
            switch (action) {
                case "editConfig":
                    editConfig(selectedItem);
                    break;
                case "deleteConfig":
                    deleteConfig(selectedItem);
                    break;
                case "openAddGameWindow":
                    openAddGameWindow();
                    break;
                default:
                    System.out.println(App.timestamp() + " Unknown action: " + action);
                    break;
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
public void showErrorAlert(String errorMessage) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("An error occurred");
    alert.setContentText(errorMessage);

    alert.showAndWait();
}

    private void startWatching(String directoryPath) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(directoryPath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            executorService = Executors.newSingleThreadExecutor();
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

    public void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    private boolean deleteConfig(String selectedItem) {
        System.out.println(App.timestamp() + " StartController deleteConfig()");
        App.logToFile(App.timestamp() + " StartController deleteConfig()");
        File file = new File(String.format("configs/%s", selectedItem));
        if (file.exists()) {
            return file.delete();
        } else {
            System.out.println(App.timestamp() + " File not found: " + selectedItem);
            return false;
        }
    }

    private void editConfig(String selectedItem) {
        System.out.println(App.timestamp() + " StartController editConfig()");
        App.logToFile(App.timestamp() + " StartController editConfig()");
        EditConfigController editConfigController = new EditConfigController();
        editConfigController.setConfigName(selectedItem);
        editConfigController.openUpdateConfigWindow(selectedItem);
    }

    @FXML
    private void openBackups(){
        App.openFolderInExplorer("backups");
    }
}


