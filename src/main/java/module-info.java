module com.example.dddeck {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires jsch;

    opens com.example.dddeck to javafx.fxml, com.google.gson;
    exports com.example.dddeck;
}
