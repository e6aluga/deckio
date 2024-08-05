package com.example.dddeck;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.util.Callback;



public class SampleController {

    @FXML
    private ListView<Label> listView_configs;

    @FXML
    private Label label;

    // @FXML
    // protected void handleButtonAction(ActionEvent event) {
    //     label.setText("Button clicked!");
    // }
    @FXML
    public void initialize(){

        MenuItem menuItem1 = new MenuItem("Func1");
        MenuItem menuItem2 = new MenuItem("Func2");
        MenuItem menuItem3 = new MenuItem("Func3");
    
        menuItem1.setOnAction(event -> handleMenuAction("Function 1"));
        menuItem2.setOnAction(event -> handleMenuAction("Function1"));
        menuItem3.setOnAction(event -> handleMenuAction("Function1"));

        

        listView_configs.setOnMouseClicked(event -> {
            Label selectedItem = listView_configs.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {

                // showAlertConfig(selectedItem.getText());
            }
        });
    }
    // Алерт на нажатие на item в списке конфигов
    // private void showAlertConfig(String itemText){
    //     Alert alert = new Alert(AlertType.INFORMATION);
    //         alert.setTitle("Item function");
    //         alert.setHeaderText("Selected item: " + itemText);
    //         alert.setContentText("Function 1\nFunction2\nFunction3");
    //         alert.showAndWait();
    // }
    
    private void showContextMenuConfig(String itemText){
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(itemText);
    }

        // Метод для обработки действий контекстного меню
        private void handleMenuAction(String function) {
            String selectedItem = listView_configs.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Selected item: " + selectedItem + " - Action: " + function);
            } else {
                System.out.println("No item selected - Action: " + function);
            }
        }

}
