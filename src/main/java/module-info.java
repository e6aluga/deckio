module com.example.dddeck {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires jsch;
    requires java.desktop; // Add this line to include java.awt

    opens com.example.dddeck to javafx.fxml, com.google.gson;
    exports com.example.dddeck;
}
