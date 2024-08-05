module com.example.dddeck {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.dddeck to javafx.fxml;
    exports com.example.dddeck;
}
