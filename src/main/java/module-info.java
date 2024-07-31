module com.deck {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.deck to javafx.fxml;
    exports com.deck;
}
