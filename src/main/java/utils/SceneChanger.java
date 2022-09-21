package utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.app.schedulingsystem.SchedulingSystem;

import java.io.IOException;

public class SceneChanger {

    public static void changeSceneTo(ActionEvent event, String FXMLPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingSystem.class.getResource(FXMLPath));
        Scene scene = new Scene(fxmlLoader.load());

        // this line gets the stage information
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

}
