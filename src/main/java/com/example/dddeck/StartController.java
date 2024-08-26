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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartController {

    private SSHManager sshManager;
    private DeckData deckData;

    @FXML
    private ListView<String> listView;

    private ExecutorService executorService;
    private String selectedItem;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label status;



// Внутри метода initialize:
    public void initialize() {
        initializeContextMenu();
        setupListView();
        initializeMenuBar();
        String directoryPath = "configs/";
        updateListView(directoryPath);
        startWatching(directoryPath);

        DeckData deckData = new DeckData();
        deckData.loadSteamDeckSettings();

        System.out.println(App.timestamp() + "SD settings: " + deckData.getIp() + "\n" + deckData.getUser() + "\n" + deckData.getPassword() + "\n" + deckData.getPort());

        // Запуск асинхронного соединения
        Task<Void> sshTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                sshManager = new SSHManager(deckData.getIp(), deckData.getUser(), deckData.getPassword(), 22);
                sshManager.connect(); // Подключение в фоновом потоке
                return null;
            }
        };

        // Обновление интерфейса после успешного подключения
        sshTask.setOnSucceeded(event -> {
            if (sshManager.getSession() != null) {
                status.setText("Status: connected!");
            } else {
                status.setText("Status: connected!");
                while (sshManager.getSession() == null){
                    sshManager.connect();
                }
            }
        });

        // Обработка ошибок подключения
        sshTask.setOnFailed(event -> {
            Throwable exception = sshTask.getException();
            exception.printStackTrace();
            status.setText("Status: connection failed!");
        });

        // Запуск задачи в фоновом потоке
        new Thread(sshTask).start();
    }

    public DeckData getDeckData(){
        return this.deckData;
    }

    public SSHManager getSSHManager(){
        return this.sshManager;
    }

    private void initializeMenuBar() {
        // Создайте MenuBar
        Menu fileMenu = new Menu("FlyingDeck");
        MenuItem addItem = new MenuItem("Add new game");
        MenuItem sdItem = new MenuItem("Steam Deck Settings");
        MenuItem settingsItem = new MenuItem("App Settings");
        MenuItem backupsItem = new MenuItem("Backups");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exitItem = new MenuItem("Exit");
        addItem.setOnAction(e -> openAddGameWindow());
        sdItem.setOnAction(e -> openDeckWindow());
        exitItem.setOnAction(e -> System.exit(0)); // Пример действия для выхода из приложения

        fileMenu.getItems().addAll(addItem, sdItem, settingsItem, backupsItem, separator, exitItem);

        Menu helpMenu = new Menu("About");
        MenuItem helpItem = new MenuItem("About");
        helpItem.setOnAction(e -> {
            // Логика для отображения информации о приложении
            System.out.println("Help menu item clicked");
        });

        helpMenu.getItems().add(helpItem);

        // Добавьте Menu в MenuBar
        menuBar.getMenus().addAll(fileMenu, helpMenu);
    }

    // Инициализация контекстного меню для ListView
    private void initializeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");

        editItem.setOnAction(event -> handleMenuAction("editConfig"));
        deleteItem.setOnAction(event -> handleMenuAction("deleteConfig"));

        contextMenu.getItems().addAll(editItem, deleteItem);
        listView.setContextMenu(contextMenu);
    }

    // Настройка ListView
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
                            openGameWindow(cell.getItem());
                        }
                    }
                });

                return cell;
            }
        });
    }

    @FXML
    public void openAddGameWindow() {
        showWindow("/addgame.fxml", "Add game", 700, 300);
    }

    @FXML
    public void openDeckWindow() {
        showWindow("/deck.fxml", "Steam Deck Settings", 700, 300);
    }

    @FXML
    public void openBackupsAction() {
        // TODO: Реализовать действие для открытия окна резервного копирования
    }

    @FXML
    public void openGameWindow(String name) {
        System.out.println(App.timestamp() + " openGameWindow()");
        Game game = new Game();
        game.init(name, sshManager); // Передаем SSHManager в Game
    }

    private void showWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), width, height));
    
            // Получаем контроллер и передаем ему данные
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
        if (selectedItem != null) {
            switch (action) {
                case "editConfig":
                    editConfig(selectedItem);
                    break;
                case "deleteConfig":
                    deleteConfig(selectedItem);
                    break;
                default:
                    System.out.println(App.timestamp() + " Unknown action: " + action);
                    break;
            }
        } else {
            System.out.println(App.timestamp() + " No item selected - Action: " + action);
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
        // TODO: Реализовать редактирование конфига
        EditConfigController editConfigController = new EditConfigController();
        editConfigController.setConfigName(selectedItem);
        editConfigController.openUpdateConfigWindow(selectedItem);
    }
}


