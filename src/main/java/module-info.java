module my.app.schedulingsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires jsch;

    opens my.app.schedulingsystem to javafx.fxml;
    opens controller to javafx.fxml;

    exports my.app.schedulingsystem;
    exports controller;
    exports model;
}