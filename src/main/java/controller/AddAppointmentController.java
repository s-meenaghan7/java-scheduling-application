
package controller;

import dao.DBContacts;
import dao.DBCustomers;
import dao.DBAppointments;
import model.Contact;
import model.Customer;
import model.Appointment;
import model.FinanceCustomer;
import model.ProjectCustomer;
import model.User;
import utils.SceneChanger;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;
import java.io.IOException;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;

/**
 FXML Add Appointment Screen Controller class
 @author Sean
 */
public class AddAppointmentController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;

    @FXML private ComboBox<Contact> contactField;
    @FXML private ComboBox<Customer> customerField;

    // Date
    @FXML private DatePicker aptDatePicker;

    // Start and End Times
    @FXML private ComboBox<LocalTime> startTimePicker;
    @FXML private ComboBox<LocalTime> endTimePicker;

    @FXML private Label errorText;
    @FXML private Label timeZoneInfo;

    /**
     Called to initialize the Add Appointment controller class after its root element has been completely processed.
     Initializes combo boxes with their data and populates an informative text label.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        startTimePicker.setItems(initializeTimes());
        endTimePicker.setItems(initializeTimes());

        customerField.setItems(DBCustomers.getAllCustomers());

        timeZoneInfo.setText("Times input below are in your local timezone: " + ZoneId.systemDefault().toString());

    }

    /**
     This method is responsible for populating the Contacts ComboBox with only the Contacts
     that provide the consultation services to the selected Customer, based on the Customer's subclass.
     @param event
     @throws IOException
     */
    public void customerChangedHandler(ActionEvent event) throws IOException {
        List<Contact> contacts = new ArrayList<>();

        for (Contact c : DBContacts.getAllContacts()) {
            if ((customerField.getSelectionModel().getSelectedItem() instanceof ProjectCustomer) && (c.getType().equals("project")))
                contacts.add(c);
            else if ((customerField.getSelectionModel().getSelectedItem() instanceof FinanceCustomer) && (c.getType().equals("finance")))
                contacts.add(c);
        }

        ObservableList<Contact> filteredContacts = FXCollections.observableArrayList(contacts);
        contactField.setItems(filteredContacts);
    }

    /**
     Handles the save button being pressed. The validateFields() method is called to check that the required fields are appropriately filled out by the user.
     Another method, validateAppointmentTime(), is called to ensure the appointment is 1) within office hours and 2) not overlapping with other appointment times.
     Given these methods return true, the saveButtonHandler continues executing, creating a new appointment and saving this appointment to the database.
     Finally the program returns to the main screen.
     @param event The event representing the Save button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public void saveButtonHandler(ActionEvent event) throws IOException, SQLException {
        if (!validateFields()) return; // if fields !valid, do not continue.

        // create start and end dates/times
        LocalDate date = aptDatePicker.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();

        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        if (!validateAppointmentTime(start, end)) return;

        // create newAppointment object from fields
        Appointment newAppointment = new Appointment(titleField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                contactField.getValue(),
                start, end,
                customerField.getValue().getId(),
                User.user.getId());

        DBAppointments.addAppointment(newAppointment);

        SceneChanger sc = new SceneChanger();
        sc.changeSceneTo(event, "/View/MainScreen.fxml");
    }

    /**
     Handles the action of the Cancel button being pressed. Simply returns the user to the main screen.
     @param event the event object representing the cancel button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void cancelButtonHandler(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeSceneTo(event, "/View/MainScreen.fxml");
    }

    /**
     This method is responsible for initializing the ComboBoxes that handle time data on this screen. An ObservableList of LocalTimes is created for the office hours of 08:00 to 22:00.
     These times are then translated into ZonedDateTime objects, giving them timezone context (EST). The actual times are translated to be of the same instant from the user's timezone.
     For example, if the user is in PST and the open and close times for the office are 08:00 and 22:00 EST respectively, then the user would see these times as 05:00 and 19:00.
     The times are then added to the list of LocalTimes in fifteen minute increments.
     @return timeList the LocalTime ObservableList object that is returned.
     */
    public ObservableList<LocalTime> initializeTimes() {
        // LocalTime is in military time
        ObservableList<LocalTime> timeList = FXCollections.observableArrayList();
        LocalDate anyday = LocalDate.now();
        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        LocalDateTime open = LocalDateTime.of(anyday, openTime).atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime close = LocalDateTime.of(anyday, closeTime).atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        while (open.isBefore(close.plusSeconds(1))) {
            timeList.add(open.toLocalTime());
            open = open.plusMinutes(15);
        }

        return timeList;
    }

    /**
     This helper method checks if a parameter String s is a number or contains numbers. Used by the validateFields method to check the TextFields of the screen.
     @param s the String to check for if it is a number or contains numbers.
     @return true the return value returned if none of the parameter's characters are digits.
     */
    public boolean isNumber(String s) {
        //loop through the input String's characters
        for (int i = 0; i < s.length(); i++) {

            if (Character.isDigit(s.charAt(i)) == false) return false;

        }
        //returns true only if none of the String's characters are digits
        return true;
    }

    /**
     This method checks each field on this screen, from top to bottom, ensuring a proper data value has been entered by the user.
     If a field is blank or contains unsuitable data, a text label will populate to alert the user to any errors to correct. Only returns true if all fields are valid.
     @return true the value to return if all fields on screen are valid. Otherwise, returns false.
     */
    public boolean validateFields() {
        // title field
        if (titleField.getText().isBlank() ||
                isNumber(titleField.getText())) {

            errorText.setText("Title field input invalid.");
            return false;
            // description
        } else if (descriptionField.getText().isBlank() ||
                isNumber(descriptionField.getText())) {

            errorText.setText("Description field input invalid.");
            return false;
            // Location
        } else if (locationField.getText().isBlank() ||
                isNumber(locationField.getText())) {

            errorText.setText("Location field input invalid.");
            return false;

        } else if (contactField.getSelectionModel().isEmpty()) {
            errorText.setText("Please select a contact.");
            return false;
        } else if (customerField.getSelectionModel().isEmpty()) {
            errorText.setText("Please select a customer.");
            return false;
        } else if (aptDatePicker.getValue() == null || aptDatePicker.getValue().isBefore(LocalDate.now())) {
            errorText.setText("Select a valid date. Past dates are not allowed.");
            return false;
        } else if (startTimePicker.getSelectionModel().isEmpty()) {
            errorText.setText("Select an appropriate start time.");
            return false;
        } else if (endTimePicker.getSelectionModel().isEmpty() ||
                endTimePicker.getSelectionModel().getSelectedItem().equals(startTimePicker.getSelectionModel().getSelectedItem()) ||
                endTimePicker.getSelectionModel().getSelectedItem().isBefore(startTimePicker.getSelectionModel().getSelectedItem())) {
            errorText.setText("Select an appropriate end time. End time must be in the future relative to the start time.");
            return false;
        }
        errorText.setText("");
        return true; // all fields valid
    }

    /**
     This method checks that the appointment time entered by the user is 1) within office hours and 2) does not overlap with existing appointments in the database.
     If either of these errors are detected, a custom alert message is generated, alerting the user to the error.
     @param start the LocalDateTime object representing the start of a potential new appointment.
     @param end the LocalDateTime object representing the end of a potential new appointment.
     @return true the return value returned if the appointment times entered do not fall outside business hours or overlap with existing appointments in the database. If either error occurs, returns false.
     */
    public boolean validateAppointmentTime(LocalDateTime start, LocalDateTime end) {
        // office hours
        LocalTime officeOpenTime = LocalTime.of(8, 0);
        LocalTime officeCloseTime = LocalTime.of(22, 0);

        // office hours Zoned to EST. Date info matches start/end date info
        ZonedDateTime officeOpenZDT = LocalDateTime.of(start.toLocalDate(), officeOpenTime).atZone(ZoneId.of("America/New_York"));
        ZonedDateTime officeCloseZDT = LocalDateTime.of(end.toLocalDate(), officeCloseTime).atZone(ZoneId.of("America/New_York"));

        // take start/end parameters and translate to eastern time, at the same instant, to compare with office hours
        ZonedDateTime startZoned = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime endZoned = end.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));

        // Convert back to LocalDateTime for comparison below. Retains the instant from above.
        LocalDateTime newStart = startZoned.toLocalDateTime();
        LocalDateTime newEnd = endZoned.toLocalDateTime();

        // 1) check if the appointment is outside business hours. If yes, throw alert and return false; else, continue.
        if ((newStart.isBefore(officeOpenZDT.toLocalDateTime()) && (!newStart.isEqual(officeOpenZDT.toLocalDateTime()))) ||
                (newEnd.isAfter(officeCloseZDT.toLocalDateTime()) && (!newEnd.isEqual(officeCloseZDT.toLocalDateTime())) )) {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Add Appointment Error");
            alert.setHeaderText("Error: Appointment Time Out of Range");
            alert.setContentText("Appointments cannot be made outside the business hours of 8:00 AM and 10:00 PM EST. Please adjust your times.");
            alert.show();
            return false;
        }

        // 2) check if the appointment overlaps with another

        List<Appointment> allAppointments = DBAppointments.getAllAppointments();
        for (Appointment appointment : allAppointments) {
            // acquire each appointments start and end LocalDateTime objects, translate to EDT time
            ZonedDateTime aptStartZDT = appointment.getStart().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime aptEndZDT = appointment.getEnd().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));

            // convert appointment start and end times back to LocalDateTime
            LocalDateTime aptStart = aptStartZDT.toLocalDateTime();
            LocalDateTime aptEnd = aptEndZDT.toLocalDateTime();

            // newStart and newEnd should both be before aptStart or both be after aptEnd
            if (newStart.isBefore(aptStart) && (newEnd.isBefore(aptStart) || newEnd.isEqual(aptStart)) ||
                    ((newStart.isAfter(aptEnd) || newStart.isEqual(aptEnd)) && newEnd.isAfter(aptEnd))) {
                // do nothing, continue looping

            } else { // if one of the above conditions did not eval to true, throw this alert
                Alert alert = new Alert(AlertType.ERROR);
                alert.setResizable(true);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Add Appointment Error");
                alert.setHeaderText("Error: Overlapping Appointment Times");
                alert.setContentText("The appointment time entered, " + newStart.toLocalTime() + " to " + newEnd.toLocalTime() + " overlaps with an existing appointment: " + aptStart.toLocalTime() + " to " + aptEnd.toLocalTime());
                alert.show();
                return false;
            }

        }
        return true; // passed all tests
    }
}
