package controller;

import java.io.*;

import dao.*;
import model.User;
import model.Appointment;
import utils.PasswordManager;
import utils.SceneChanger;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

/**
 FXML Login Screen Controller class
 @author Sean
 */
public class LoginController implements Initializable {

    private boolean userNotFound = false; // todo: this needs to change or go away once submitButtonHandler has been refactored
    @FXML private Label loginLabel;
    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Label errorLabel;
    @FXML private Label zoneIdLabel;
    @FXML private Button submitButton;

    /**
     Called to initialize the Login screen controller class after its root element has been completely processed.
     First screen to initialize in this program. Queries the database for table information so the program can process and present this information.
     This method is also responsible for translation all login page information into French, if the user's default Locale/language is set to France/French.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            DBUsers.setAllUsers();
            DBContacts.setAllContacts();
            DBCountries.setAllCountries();
            DBDivisions.setAllDivisions();
            DBCustomers.setAllCustomers();
            DBAppointments.setAllAppointments();
        } catch (SQLException e) { e.printStackTrace(); }

        errorLabel.setText("");

        loginLabel.setText("Login");
        userField.setPromptText(userField.getPromptText());
        passField.setPromptText(passField.getPromptText());
        submitButton.setText("Submit");

        zoneIdLabel.setText(ZoneId.systemDefault().toString());
    }

    /**
     This method is called when the user presses the Submit button, attempting to login to the Scheduling application.
     A connection to the database is established and the database is queried for User information (username, password).
     The results of the query contain the database's Users. This data is checked against the user's supplied credentials, to determine if this is a valid
     user log on and will grant or deny the user access to the application.
     @param event the event object representing the Submit button being pressed by the user.
     */
    public void submitButtonHandler(ActionEvent event) throws NoSuchAlgorithmException {
        // User login entries
        String inputUsername = userField.getText();
        String inputPassword = PasswordManager.getHash(passField.getText());

        // todo: get the username specified. If not found, throw an error. If found, check the passwords to see if it matches the hash
        // above would be better than looping through the entire list of users
        DBUsers.getAllUsers().forEach(user -> {
            if (inputUsername.equals(user.getUsername()) && inputPassword.equals(user.getPassword())) {
                try {
                    userNotFound = true;
                    appendUserLog(userNotFound);
                    User.user = user;
                    checkForAppointment();
                    SceneChanger.changeSceneTo(event, "MainScreen.fxml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        if (userNotFound) return;

        // only executes if inputUsername and inputPassword do not have a match in the allUsers list.
        try { appendUserLog(userNotFound); } catch (IOException ee) { ee.printStackTrace(); } // record unsuccessful log in attempt
        errorLabel.setText("Error");
    }

    /**
     This method generates an alert when a user successfully logs into the Database. This alert will either tell the user if there is or is not
     an appointment upcoming within the next 15 minutes (relative to the user's local time).
     */
    public void checkForAppointment() {
        List<Appointment> appointments = DBAppointments.getAllAppointments();
        LocalDateTime loginTime = LocalDateTime.now();
        boolean upcoming = false;

        for (Appointment appointment : appointments) {

            if (loginTime.isAfter(appointment.getStart().minusMinutes(15)) &&
                    loginTime.isBefore(appointment.getStart())) {

                upcoming = true;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setResizable(true);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Scheduling");
                alert.setHeaderText("Welcome");
                alert.setContentText("There is an appointment within 15 minutes:\n\nAppointment ID: " + appointment.getAppointmentId() +
                        "\nDate: " + appointment.getStart().toLocalDate() +
                        "\nTime: " + appointment.getStart().toLocalTime() + " - " + appointment.getEnd().toLocalTime());
                alert.show();
            }
        }

        if (!upcoming) {
            // alert for no upcoming appointments
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Scheduling");
            alert.setHeaderText("Welcome");
            alert.setContentText("There are no appointments occurring within the next 15 minutes.");
            alert.show();
        }
    }

    /**
     This method is responsible for writing to a text file all user login attempts, successful or not. The method will write data to a text file named
     login_activity.txt about whether the login attempt was successful or not, the supplied username, and the date and time of when it occurred.
     */
    public void appendUserLog(boolean success) throws IOException {

        String filename = "login_activity.txt", attempt;
        String loginState = (success) ? "Successful" : "Unsuccessful";
        String access = (success) ? "Access granted" : "Access denied";

        FileWriter fw = new FileWriter(filename, true);
        PrintWriter outputFile = new PrintWriter(fw);

        attempt = loginState + " login attempt | Supplied username: " + userField.getText() + " | Occurred: " + LocalDate.now() + " " + LocalTime.now() + " | " + access;
        outputFile.println(attempt);
        outputFile.close();
    }
}
