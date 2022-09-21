package my.app.schedulingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.DBConnection;

import java.io.IOException;
import java.util.Optional;

public class SchedulingSystem extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        setConfirmApplicationExit(stage);

        Parent root = FXMLLoader.load(SchedulingSystem.class.getResource("LoginScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        /*
        todo: create a way for the user to choose whether to connect locally or remotely
         for now, connection defaults to local.
         */
        DBConnection.startLocalConnection();
        launch();
        DBConnection.closeConnection();
    }

    private static void setConfirmApplicationExit(Stage stage) {
        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Exit Concordant Scheduling?");
            alert.setHeaderText("Are you sure you would like to exit the program?");

            ButtonType exit = new ButtonType("Exit");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(exit, cancel);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == exit)
                stage.close();
        });
    }
}