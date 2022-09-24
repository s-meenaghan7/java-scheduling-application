
package controller;

import dao.DBAppointments;
import dao.DBCustomers;
import model.Appointment;
import model.Customer;
import model.FinanceCustomer;
import model.ProjectCustomer;
import model.User;
import utils.SceneChanger;
import java.time.LocalDateTime;
import java.io.IOException;
import java.sql.SQLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.Utils;

/**
 FXML Main Screen Controller class
 @author Sean
 */
public class MainController implements Initializable {

    @FXML private Label userInfo;
    @FXML private Label aptConfirmationText;
    @FXML private Label customerConfirmationText;

    @FXML private RadioButton allAptButton;
    @FXML private RadioButton monthlyAptButton;
    @FXML private RadioButton weeklyAptButton;
    @FXML private final ToggleGroup aptViewToggleGroup = new ToggleGroup();

    @FXML private TextField appointmentSearchField;
    @FXML private TableView<Appointment> appointmentsTableView;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML private TableColumn<Appointment, String> appointmentDescCol;
    @FXML private TableColumn<Appointment, String> appointmentLocationCol;
    @FXML private TableColumn<Appointment, String> appointmentContactCol;
    @FXML private TableColumn<Appointment, String> appointmentStartCol;
    @FXML private TableColumn<Appointment, String> appointmentEndCol;
    @FXML private TableColumn<Appointment, Integer> appointmentCustomerIdCol; // FK

    @FXML private RadioButton allCustomerButton;
    @FXML private RadioButton projectCustomerButton;
    @FXML private RadioButton financialCustomerButton;
    @FXML private final ToggleGroup customerViewToggleGroup = new ToggleGroup();

    @FXML private TextField customerSearchField;
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, Integer> customerIdCol; // PK
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> customerPhoneCol;
    @FXML private TableColumn<Customer, String> customerAddressCol;
    @FXML private TableColumn<Customer, String> customerDivisionCol;
    @FXML private TableColumn<Customer, String> customerCountryCol;
    @FXML private TableColumn<Customer, Integer> customerZipCol;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    /**
     Called to initialize the Main screen controller class after its root element has been completely processed.
     Sets TableColumn cell properties, queries the database for TableView information and sets the information to be displayed in the TableViews.
     LAMBDA: Both lambda expressions in this method perform the same operation. Each lambda
     expression is used to initialize their respective TableColumns with formatted LocalDateTime objects. This is necessary so that the LocalDateTime
     objects display in a more attractive and human-readable format.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        userInfo.setText("User: " + User.user.getUsername());

        // columnName.setCellValueFactory(new PropertyValueFactory<>("variableName"));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentStartCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getStart().format(dtf)));
        appointmentEndCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getEnd().format(dtf)));
        appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("zipCode"));

        try {
            // these stay here, they must run after UPDATING appointments or customers and returning to this screen
            DBAppointments.setAllAppointments();
            DBCustomers.setAllCustomers();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        FilteredList<Appointment> filteredAppointments = new FilteredList<>(DBAppointments.getAllAppointments(), p -> true);
        appointmentSearchHandler(appointmentsTableView, appointmentSearchField, filteredAppointments);

        FilteredList<Customer> filteredCustomers = new FilteredList<>(DBCustomers.getAllCustomers(), p -> true);
        customerSearchHandler(customersTableView, customerSearchField, filteredCustomers);

    }

    /**
     This method is called whenever the user presses the Refresh button on the main screen. The purpose of this button is to allow the user to
     re-establish a connection with the database in the event that the connection times out.
     @param event the event object representing the Refresh button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void refreshButton(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "MainScreen.fxml");
    }

    /**
     This method is called whenever the user presses the Appointments report button, simply changing to the Appointments Report screen.
     @param event the event object representing the Appointments Report button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void appointmentsReportButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "AppointmentsReportScreen.fxml");
    }

    /**
     This method is called whenever the user presses the Contact Scheduling report button, simply changing to the Contact Scheduling Report screen.
     @param event the event object representing the Contact Scheduling Report button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void contactSchedulingButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "ContactScheduleScreen.fxml");
    }

    /**
     This method is called whenever the user presses the Customer Scheduling report button, simply changing to the Customer Scheduling Report screen.
     @param event the event object representing the Customer Scheduling Report button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void customerSchedulingButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "CustomerSchedulingScreen.fxml");
    }

    /**
     This method is responsible for filtering the Appointments TableView, dependent on the radio button selection associated with the Appointments TableView.
     By default, all appointments are displayed. The user may also select the Appointments this Month radio button, showing the user appointments made for
     the current month. Finally, the user may select the Appointments this Week button, showing only appointments for the current week, defined as the between
     the previous Sunday (or current day, if the day happens to be Sunday) and the following Saturday (or current day, if the day happens to be Saturday).
     LAMBDA: The lambda expression in this method is necessary so that any Appointments that are not of the current Month are filtered out. The second
     lambda expression works similarly, but instead filters out any Appointments that are not of the current week, Sunday through Saturday.
     */
    public void appointmentRadioButtonChanged() {

        if (this.aptViewToggleGroup.getSelectedToggle().equals(this.allAptButton)) {
            FilteredList<Appointment> filteredAppointments = new FilteredList<>(DBAppointments.getAllAppointments(), p -> true);
            appointmentSearchHandler(appointmentsTableView, appointmentSearchField, filteredAppointments);

        } else if (this.aptViewToggleGroup.getSelectedToggle().equals(this.monthlyAptButton)) {
            List<Appointment> aptList = DBAppointments.getAllAppointments().stream()
                    .filter(apt -> apt.getStart().getMonth().equals(LocalDateTime.now().getMonth()) &&
                            apt.getStart().getYear() == LocalDateTime.now().getYear()).collect(Collectors.toList());

            FilteredList<Appointment> monthlyAptList = new FilteredList<>(FXCollections.observableArrayList(aptList), p -> true);
            appointmentSearchHandler(appointmentsTableView, appointmentSearchField, monthlyAptList);

        } else if (this.aptViewToggleGroup.getSelectedToggle().equals(this.weeklyAptButton)) {
            LocalDate currentWeekStart = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toLocalDate();
            LocalDate currentWeekEnd = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).toLocalDate();

            // Filter the appointments so that they are greater or equal to the weekBegin, but lesser or equal to the weekEnd
            List<Appointment> aptList = DBAppointments.getAllAppointments().stream()
                    .filter(apt -> (apt.getStart().toLocalDate().isAfter(currentWeekStart) &&
                            apt.getStart().toLocalDate().isBefore(currentWeekEnd)) ||
                            apt.getStart().toLocalDate().isEqual(currentWeekStart) ||
                            apt.getStart().toLocalDate().isEqual(currentWeekEnd))
                    .collect(Collectors.toList());

            FilteredList<Appointment> weeklyAptList = new FilteredList(FXCollections.observableArrayList(aptList), p -> true);
            appointmentSearchHandler(appointmentsTableView, appointmentSearchField, weeklyAptList);
        }
    }

    /**
     This method is responsible for filtering the CustomersTableView, showing either a) all the customers in the database, b) only ProjectCustomers in the database,
     or c) only FinanceCustomers in the database.
     */
    public void custRadioButtonChanged() {

        if (this.customerViewToggleGroup.getSelectedToggle().equals(this.allCustomerButton)) {
            FilteredList<Customer> allCustomers = new FilteredList<>(DBCustomers.getAllCustomers(), p -> true);
            customerSearchHandler(customersTableView, customerSearchField, allCustomers);

        } else if (this.customerViewToggleGroup.getSelectedToggle().equals(this.projectCustomerButton)) {
            FilteredList<ProjectCustomer> proCustomers = new FilteredList<>(DBCustomers.getAllProjectCustomers(), p -> true);
            customerSearchHandler(customersTableView, customerSearchField, proCustomers);

        } else if (this.customerViewToggleGroup.getSelectedToggle().equals(this.financialCustomerButton)) {
            FilteredList<FinanceCustomer> finCustomers = new FilteredList<>(DBCustomers.getAllFinanceCustomers(), p -> true);
            customerSearchHandler(customersTableView, customerSearchField, finCustomers);

        }

    }

    public void appointmentSearchHandler(TableView<Appointment> tableView, TextField searchField, FilteredList<Appointment> filteredData) {
        //Listener allows search field to automatically narrow search results to the predicate
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(appointment -> {
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();
            if (Utils.isNumber(newValue)) {
                Appointment current = DBAppointments.lookupAppointment(appointment.getAppointmentId());

                /*
                if (String.valueOf(current.getAppointmentId()).contains(lowerCaseValue))
                    return true; // filter matches appointmentId
                 */
                return String.valueOf(current.getAppointmentId()).contains(lowerCaseValue); // filter matches appointmentId

            } else if (!Utils.isNumber(newValue)) {
                ObservableList<Appointment> appointments = DBAppointments.lookupAppointment(appointment.getTitle());

                for (Appointment a : appointments) {
                    if (a.getTitle().toLowerCase().startsWith(lowerCaseValue))
                        return true;
                }
            }
            return false;
        }));

        // Wrap the FilteredList in a SortedList
        SortedList<Appointment> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // Set sorted and filtered data to tableView
        tableView.setItems(sortedData);
    }

    public <T extends Customer> void customerSearchHandler(TableView<Customer> tableView, TextField searchField, FilteredList<T> filteredData) {
        //Listener allows search field to automatically narrow search results to the predicate
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(customer -> {
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();
            if (Utils.isNumber(newValue)) {
                Customer current = DBCustomers.lookupCustomer(customer.getId());

                /*
                if (String.valueOf(current.getId()).contains(lowerCaseValue))
                    return true; // filter matches appointmentId
                 */
                return String.valueOf(current.getId()).contains(lowerCaseValue); // filter matches appointmentId

            } else if (!Utils.isNumber(newValue)) {
                ObservableList<Customer> customers = DBCustomers.lookupCustomer(customer.getName());

                for (Customer c : customers) {
                    if (c.getName().toLowerCase().startsWith(lowerCaseValue))
                        return true;
                }
            }
            return false;
        }));

        // Wrap the FilteredList (filteredData) in a SortedList (sortedData)
        SortedList<Customer> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // Set sorted and filtered data to tableView
        tableView.setItems(sortedData);
    }

    /**
     This method is called whenever the user selects the Add button on the Appointments Table pane, simply changing to the Add Appointment screen.
     @param event the event object representing the Add Appointment button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void addAppointmentButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "AddAptScreen.fxml");
    }

    /**
     This method is called when the user presses the Update button on the Appointments Table pane. This method first checks that the user is selecting
     an appointment from the Appointments TableView. If a selection has not been made, an alert is generated to inform the user. With a selection made,
     the initializeFields() method is called from the Update Appointment Screen controller class, to pre-populate the data fields on the next screen.
     The method then loads the Update Appointment Screen.
     @param event the event object representing the Update Appointment button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void updateAppointmentButtonHandler(ActionEvent event) throws IOException {
        // check if a selection has been made or not
        if (!appointmentsTableView.getSelectionModel().isEmpty()) {
            // store selected appointment to public static selectedApt from UpdateAppointmentController
            UpdateAppointmentController.selectedApt = appointmentsTableView.getSelectionModel().getSelectedItem();

            //create FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/UpdateAptScreen.fxml"));
            Parent newScene = loader.load();

            //access the controller from the loader and call initializeFields() method to pass data into fields
            UpdateAppointmentController controller = loader.getController();
            controller.initializeFields(event);

            //use part of changeSceneTo() method to switch scenes
            Scene scene = new Scene(newScene);

            // this line gets the stage information
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            window.setScene(scene);
            window.show();
        } else { // if no selection is made, display error alert.

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Update Appointment");
            alert.setHeaderText("Update Appointment");
            alert.setContentText("To update an appointment, select an appointment first, then click Update.");
            alert.show();

        }

    }

    /**
     This method is called if the user clicks the Delete button on the Appointments Table pane. The method first checks that a selection has been made on
     the Appointments Table pane. With no selection made, an alert is generated to inform the user of this. If a selection has been made, the user is asked
     to confirm that they would like to delete the selected appointment. The user may confirm and delete the appointment, or cancel the operation by
     pressing Cancel.
     @param event the event object representing the Delete Appointment button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     @throws SQLException  the potential SQLException that must be caught or declared to be thrown.
     */
    public void deleteAppointmentButtonHandler(ActionEvent event) throws SQLException {
        if (!appointmentsTableView.getSelectionModel().isEmpty()) {
            aptConfirmationText.setText("");
            Appointment appointmentToDelete = appointmentsTableView.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText("Are you sure you want to delete this appointment from the database?\n" +
                    "Appointment ID: " + appointmentToDelete.getAppointmentId() +"\nTitle: " + appointmentToDelete.getTitle());
            alert.setContentText("This action is permanent and cannot be undone.");

            ButtonType delete = new ButtonType("Delete");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(delete, cancel);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == delete) {
                DBAppointments.deleteAppointment(appointmentToDelete);

                Alert delAlert = new Alert(AlertType.INFORMATION);
                delAlert.setResizable(true);
                delAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                delAlert.setTitle("Delete Appointment");
                delAlert.setHeaderText("Successfully deleted Appointment ID: " + appointmentToDelete.getAppointmentId());
                delAlert.show();

            } else if (result.get() == cancel) aptConfirmationText.setText("Appointment was not deleted");

        } else {

            aptConfirmationText.setText("");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText("Delete Appointment");
            alert.setContentText("To delete an appointment, select an appointment first, then click Delete.");
            alert.show();

        }
    }

    /**
     This method is called when the user selects the Add button on the Customers Table pane, simply changing the screen to the Add Customer screen.
     @param event the event object representing the Add Customer button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void addCustomerButtonHandler(ActionEvent event) throws IOException {
        SceneChanger.changeSceneTo(event, "AddCustomerScreen.fxml");
    }

    /**
     This method is called when the user selects the Update button on the Customers Table pane. The method checks that the user has made a selection on the
     Customers TableView. With no selection made, an alert is generated informing the user. With a selection made, the initializeFields() method from the
     Update customer controller class is called, pre-populating the data fields on the Update Customer screen. The screen is then changed to the Update
     Customer screen.
     @param event the event object representing the Update Customer button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void updateCustomerButtonHandler(ActionEvent event) throws IOException {
        // check if a selection has been made or not
        if (!customersTableView.getSelectionModel().isEmpty()) {
            // store selected customer to a public static selectedCustomer from UpdateCustomerController
            UpdateCustomerController.selectedCustomer = customersTableView.getSelectionModel().getSelectedItem();

            if (customersTableView.getSelectionModel().getSelectedItem().getClass() == ProjectCustomer.class) {
                // todo: why did I leave these empty?
            } else if (customersTableView.getSelectionModel().getSelectedItem().getClass() == FinanceCustomer.class) {

            }

            //create FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UpdateCustomerScreen.fxml"));
            Parent newScene = loader.load();

            //access the controller from the loader and call initializeFields() method to pass data into fields
            UpdateCustomerController controller = loader.getController();
            controller.initializeFields();

            //use part of changeSceneTo() method to switch scenes
            Scene scene = new Scene(newScene);

            // this line gets the stage information
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } else { // if no selection is made, display error alert.

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Update Customer");
            alert.setHeaderText("Update Customer");
            alert.setContentText("To update a customer, select a customer first, then click Update.");
            alert.show();

        }

    }

    /**
     This method is called when the Delete button on the Customers Table pane is pressed. The method first checks that the user has made a selection from the
     Customers TableView. With no selection made, an alert is generated, informing the user of this. With a selection made, the user is asked to confirm that
     they would like to delete the selected Customer. If the user confirms they would like to delete the selected Customer, the method checks the database's
     appointments, checking that the customer has no associated Appointments in the database. If the customer has associated appointments on record, an alert
     is generated to inform the user that the customer cannot be deleted until all associated appointments are first deleted. With no associated appointments
     for the selected customer, the user may successfully delete the customer from the database.
     @param event the event object representing the Delete Customer button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public void deleteCustomerButtonHandler(ActionEvent event) throws IOException, SQLException {
        if (!customersTableView.getSelectionModel().isEmpty()) {
            Customer customerToDelete = customersTableView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Are you sure you want to delete " + customerToDelete.getName() + " from the database?");
            alert.setContentText("This action is permanent and cannot be undone.");

            ButtonType delete = new ButtonType("Delete");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(delete, cancel);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == delete) {
                if (!DBCustomers.deleteCustomer(customerToDelete)) {
                    Alert alertt = new Alert(AlertType.ERROR);
                    alertt.setResizable(true);
                    alertt.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alertt.setTitle("Customer delete error");
                    alertt.setHeaderText("Customer " + customerToDelete.getName() + " has pending appointments.");
                    alertt.setContentText("Customer records cannot be deleted until any pending appointments associated with them are removed.");
                    alertt.show();
                } else {
                    customerConfirmationText.setText("Customer deleted successfully");
                }

            } else if (result.get() == cancel) customerConfirmationText.setText("Customer was not deleted");

        } else {

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Delete Customer");
            alert.setContentText("To delete a customer, select a customer first, then click Delete.");
            alert.show();

        }
    }

}
