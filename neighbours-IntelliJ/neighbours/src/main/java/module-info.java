module application.neighbours {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens application to javafx.fxml;
    exports application;
}