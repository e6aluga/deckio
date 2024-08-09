package com.example.dddeck;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.util.Callback;



public class SampleController {
@FXML
private ListView<String> listView;

@FXML
public void initialize(){
    ContextMenu contextMenu = new ContextMenu();

    MenuItem menuItem1 = new MenuItem("Edit");
    MenuItem menuItem2 = new MenuItem("Delete");
    // MenuItem menuItem3 = new MenuItem("Func3");

    menuItem1.setOnAction(event -> handleMenuAction("edit_config"));
    menuItem2.setOnAction(event -> handleMenuAction("delete_config"));
    //menuItem3.setOnAction(event -> handleMenuAction("Func 3"));

    contextMenu.getItems().addAll(menuItem1, menuItem2);

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
            return cell;
        };
    });
    listView.getItems().addAll("Item 1", "Item 2", "Item 3");
}

@FXML
private void openAddGameWindow(){
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

private void handleMenuAction(String function) {
    String selectedItem = listView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
        System.out.println("Selected item: " + selectedItem + " - Action: " + function);
    } else {
        System.out.println("No item selected - Action: " + function);
    }
}


}
