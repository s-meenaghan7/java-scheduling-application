
package controller;

import model.Contact;
import model.Appointment;
import utils.SceneChanger;
import dao.DBContacts;
import dao.DBAppointments;
import java.net.URL;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

/**
 FXML Contact Schedule screen Controller class
 @author Sean
 */
public class ContactScheduleController implements Initializable {

    @FXML private ComboBox<Contact> contactField;

    @FXML private TableView<Appointment> appointmentsTableView;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML private TableColumn<Appointment, String> appointmentDescCol;
    @FXML private TableColumn<Appointment, String> appointmentStartCol;
    @FXML private TableColumn<Appointment, String> appointmentEndCol;
    @FXML private TableColumn<Appointment, Integer> apptCustomerIdCol;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");


    /**
     Called to initialize the Contact Schedules controller class after its root element has been completely processed.
     Sets ComboBox items and sets TableColumn cell properties. LAMBDA: Both lambda expressions in this method perform the same operation. Each lambda
     expression is used to initialize their respective TableColumns with formatted LocalDateTime objects. This is necessary so that the LocalDateTime
     objects display in a more attractive and human-readable format.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        contactField.setItems(DBContacts.getAllContacts());

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentStartCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getStart().format(dtf)));
        appointmentEndCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getEnd().format(dtf)));
        apptCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

    }

    /**
     This method is called whenever the contactField ComboBox is changed. This method will filter the list of all appointments contained in the
     database and only display the appointments associated with the selected Contact. LAMBDA: The lambda expression in this method is used to filter out
     any Appointments that are not associated with the selectedContact.
     @param event the event object representing the contactField ComboBox selection being changed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void contactFieldChanged(ActionEvent event) throws IOException {
        Contact selectedContact = contactField.getSelectionModel().getSelectedItem();
        List<Appointment> myList = DBAppointments.getAllAppointments().stream()
                .filter(a -> a.getContact().equals(selectedContact))
                .collect(Collectors.toList());

        appointmentsTableView.setItems(FXCollections.observableArrayList(myList));
    }

    /**
     This method handles the user pressing the Back button, taking the user back to the main screen.
     @param event the event object representing the contactField ComboBox selection being changed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void backButtonHandler(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeSceneTo(event, "/View/MainScreen.fxml");
    }

}
