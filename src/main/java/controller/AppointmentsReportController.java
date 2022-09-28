
package controller;

import model.Appointment;
import dao.DBAppointments;
import utils.SceneChanger;
import java.net.URL;
import java.io.IOException;
import java.time.Month;
import java.util.List;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

/**
 FXML Appointment Report Screen Controller class
 @author Sean
 */
public class AppointmentsReportController implements Initializable {

    @FXML private final ObservableList<Month> monthsList = FXCollections.observableArrayList();

    public static final String TYPE_COLUMN_MAP_KEY = "type";
    public static final String TOTAL_COLUMN_MAP_KEY = "amount";

    @FXML private ComboBox<Month> monthField;

    @FXML private Label totalAppointmentsLabel;

    @FXML private TableView appointmentReportTableView;
    @FXML private TableColumn<HashMap, String> appointmentTypeTableColumn;
    @FXML private TableColumn<HashMap, String> totalAppointmentsTableColumn;

    /**
     Called to initialize the Appointments Report controller class after its root element has been completely processed.
     Prepares TableColumns to receive HashMap values and initializes the values of a ComboBox holding the enumerated type Month.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        appointmentTypeTableColumn.setCellValueFactory(new MapValueFactory(TYPE_COLUMN_MAP_KEY));
        totalAppointmentsTableColumn.setCellValueFactory(new MapValueFactory(TOTAL_COLUMN_MAP_KEY));

        for (int i = 1; i <= 12; ++i)
            monthsList.add(Month.of(i));

        monthField.setItems(monthsList);

    }

    /**
     This method handles the Back button being pressed by the user, sending the user back to the main screen.
     @param event the event object representing the Back button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void backButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "MainScreen.fxml");
    }

    /**
     This method changes the data displayed in a text label and a table. When the month field is changed, a list of all appointments held in the database
     is filtered to only include appointments of the chosen month. In the text label, information on the total number of appointments for the month are
     displayed to the user. In the table, each unique 'type' of appointment is displayed in a column, with the number of appointments for this type
     (for the chosen month only) is displayed in the column adjacent.
     @param event the event object representing the Months ComboBox selection being changed.
     */
    public void monthFieldChanged(ActionEvent event) {

        Month selectedMonth = monthField.getSelectionModel().getSelectedItem();

        List<Appointment> totalAppointmentsList = DBAppointments.getAllAppointments().stream()
                .filter(apt -> apt.getStart().getMonth().equals(selectedMonth))
                .collect(Collectors.toList());

        int appointmentsThisMonth = 0;
        appointmentsThisMonth = totalAppointmentsList.stream().map(a -> 1).reduce(appointmentsThisMonth, Integer::sum);

        totalAppointmentsLabel.setText("Total Appointments for " + selectedMonth.toString() + ": " + appointmentsThisMonth); // set totalAppointments text Label

        appointmentReportTableView.setItems(initializeMapData(totalAppointmentsList)); // show TableView breaking down totalAppointments by type and amount for selected month
    }

    /**
     This method helps the monthFieldChanged() method initialize TableView data for a TableView of HashMaps.
     Each TableColumn is set to only display data of a particular key for each HashMap. Using stream methods, the type of each appointment is attained,
     and the total number of appointments with this same type is stored in a variable total. These values are then put into the HashMap with
     keys that differentiate the two types of data (the type of appointment, and the total number of these appointments). Finally, the ObservableList of
     HashMaps is returned containing only the distinct (unique) type values in the list, to prevent duplicates from appearing in the TableView.
     @param totalAppointmentsList the list of all appointments for a given month.
     @return ObservableList the ObservableList of HashMaps that is returned from this method, to be displayed in a TableView.
     */
    public ObservableList<HashMap> initializeMapData(List<Appointment> totalAppointmentsList) {

        ObservableList<HashMap> mapList = FXCollections.observableArrayList();

        totalAppointmentsList.stream().map(appointment -> {
            // Get the type of each appointment
            HashMap<String, String> row = new HashMap<>();
            String selectedType = appointment.getLocation();
            int total = 0;

            // find how many appointments have the same type in the list, iterate total
            total = totalAppointmentsList.stream().filter(apt -> (apt.getLocation().equals(selectedType))).map(a -> 1).reduce(total, Integer::sum);
            // puts the type and total of the current appointment in the current map, which is then added to the reportList
            row.put(TYPE_COLUMN_MAP_KEY, selectedType);
            row.put(TOTAL_COLUMN_MAP_KEY, String.valueOf(total));
            return row;

        }).forEachOrdered(mapList::add);

        ObservableList<HashMap> finalReport = FXCollections.observableArrayList(mapList.stream().distinct().collect(Collectors.toList()));

        return finalReport;
    }
}
