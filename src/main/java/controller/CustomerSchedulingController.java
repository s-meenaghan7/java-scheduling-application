
package controller;

import dao.DBCustomers;
import dao.DBAppointments;
import model.Customer;
import model.Appointment;
import utils.SceneChanger;
import java.net.URL;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 FXML Customer Scheduling Screen Controller class
 @author Sean
 */
public class CustomerSchedulingController implements Initializable {

    @FXML private ComboBox<Customer> customerField;

    @FXML private TableView<Appointment> appointmentsTableView;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML private TableColumn<Appointment, String> appointmentContactCol;
    @FXML private TableColumn<Appointment, String> appointmentStartCol;
    @FXML private TableColumn<Appointment, String> appointmentEndCol;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    /**
     Called to initialize the Customer Scheduling controller class after its root element has been completely processed.
     Sets ComboBox items and sets TableColumn cell properties. LAMBDA: Both lambda expressions in this method perform the same operation. Each lambda
     expression is used to initialize their respective TableColumns with formatted LocalDateTime objects. This is necessary so that the LocalDateTime
     objects display in a more attractive and human-readable format.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerField.setItems(DBCustomers.getAllCustomers());

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentStartCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getStart().format(dtf)));
        appointmentEndCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getEnd().format(dtf)));

    }

    /**
     This method is called whenever the user changes the selection of the customerField ComboBox. The method loops through a list of all appointments
     contained in the database, checking for appointments that have the same Customer ID as the selected customer from the ComboBox. The selected customer's
     appointments are then displayed in a TableView.
     @param event the event object representing the customerField ComboBox selection being changed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void customerFieldChanged(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerField.getSelectionModel().getSelectedItem();

        List<Appointment> allAppointments = DBAppointments.getAllAppointments();
        List<Appointment> customerAppointments = new ArrayList<>();

        for (Appointment appointment : allAppointments) {
            int customerId = appointment.getCustomerId();
            if (selectedCustomer != null) { // if null: do nothing
                if (customerId == selectedCustomer.getId())
                    customerAppointments.add(appointment);
            }
        }

        appointmentsTableView.setItems(FXCollections.observableArrayList(customerAppointments));
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
