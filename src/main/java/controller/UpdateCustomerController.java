
package controller;

import dao.DBCountries;
import dao.DBCustomers;
import dao.DBDivisions;
import model.Country;
import model.Customer;
import model.FinanceCustomer;
import model.FirstLevelDivision;
import model.ProjectCustomer;
import utils.SceneChanger;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 FXML Update Customer screen Controller class
 @author Sean
 */
public class UpdateCustomerController implements Initializable {

    @FXML private TextField customerIdField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField postalField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<Country> countryField;
    @FXML private ComboBox<FirstLevelDivision> divisionField;

    @FXML private ToggleGroup customerTypeToggleGroup = new ToggleGroup();
    @FXML private RadioButton projectRadioButton;
    @FXML private RadioButton financeRadioButton;

    @FXML private TextField customerSpecialField;
    @FXML private Label customerSpecialLabel;

    @FXML private Label errorTextField;

    public static Customer selectedCustomer;

    /**
     Called to initialize the Update customer screen controller class after its root element has been completely processed.
     Sets up selections for the screens ComboBoxes. LAMBDA: The lambda expression in this method is use to filter the divisionField ComboBox to only
     contain FirstLevelDivision objects that are associated with the selectedCustomer's Country.
     @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.projectRadioButton.setToggleGroup(customerTypeToggleGroup);
        this.financeRadioButton.setToggleGroup(customerTypeToggleGroup);

        countryField.setItems(DBCountries.getAllCountries());

        List<FirstLevelDivision> divs = DBDivisions.getAllDivisions().stream()
                .filter(d -> d.getCountryId() == selectedCustomer.getCountry().getId())
                .collect(Collectors.toList());

        ObservableList<FirstLevelDivision> filteredDivs = FXCollections.observableArrayList(divs);
        divisionField.setItems(filteredDivs);

    }

    /**
     This method handles changing input fields per the users choice of whether the new customer
     will be a ProjectCustomer or FinanceCustomer.
     */
    public void radioButtonChanged() {
        if (this.customerTypeToggleGroup.getSelectedToggle().equals(this.projectRadioButton)) {
            customerSpecialField.clear();
            errorTextField.setText("");
            customerSpecialLabel.setText("Project Name");
            customerSpecialField.setPromptText("Project Name");
        }
        else if (this.customerTypeToggleGroup.getSelectedToggle().equals(this.financeRadioButton)) {
            customerSpecialField.clear();
            errorTextField.setText("");
            customerSpecialLabel.setText("Portfolio includes cryptocurrency?");
            customerSpecialField.setPromptText("Enter 'Yes' or 'No'");
        }
    }

    /**
     This method is responsible for filtering the selection of First Level divisions (states, provinces, etc) in the divisionField.
     Selection of a given country will filter the divisionField to only show First level divisions associated with it. For example, choosing "U.S"
     for country will populate the divisionField with U.S states, but no Canadian provinces will be displayed. LAMBDA: This lambda expression is
     responsible for filtering the FirstLevelDivision ComboBox to only contain FirstLevelDivision objects that are associated with the chosen Country
     from the countryField ComboBox.
     @param event the event representing the change or action made to the ComboBox countryField.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void countryChangedHandler(ActionEvent event) throws IOException {

        List<FirstLevelDivision> divs = DBDivisions.getAllDivisions().stream()
                .filter(d -> d.getCountryId() == countryField.getSelectionModel().getSelectedItem().getId())
                .collect(Collectors.toList());

        ObservableList<FirstLevelDivision> filteredDivs = FXCollections.observableArrayList(divs);
        divisionField.setItems(filteredDivs);

    }

    /**
     This method handles the Save button for this screen being pressed. First, the validateNewCustomer method ensures that all the appropriate data fields
     have been completed appropriately. Next, the new Customer object is generated from the data fields filled out by the user. This customer is then
     updated in the database, and the screen changes back to the main screen.
     @param event the event object representing the Save button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public void saveButtonHandler(ActionEvent event) throws IOException, SQLException {
        if (!validNewCustomer()) return; // if fields !valid, do not continue.

        // create new customer object based on the fields
        if (customerTypeToggleGroup.getSelectedToggle().equals(projectRadioButton)) {
            ProjectCustomer updatedProCustomer = new ProjectCustomer(Integer.parseInt(customerIdField.getText()),
                    nameField.getText(),
                    addressField.getText(),
                    postalField.getText(),
                    phoneField.getText(),
                    countryField.getSelectionModel().getSelectedItem(),
                    divisionField.getSelectionModel().getSelectedItem(),
                    customerSpecialField.getText());

            DBCustomers.updateProjectCustomer(updatedProCustomer);
            cancelButtonHandler(event);

        } else if (customerTypeToggleGroup.getSelectedToggle().equals(financeRadioButton)) {
            FinanceCustomer updatedFinCustomer = new FinanceCustomer(Integer.parseInt(customerIdField.getText()),
                    nameField.getText(),
                    addressField.getText(),
                    postalField.getText(),
                    phoneField.getText(),
                    countryField.getSelectionModel().getSelectedItem(),
                    divisionField.getSelectionModel().getSelectedItem(),
                    customerSpecialField.getText().toLowerCase().charAt(0) == 'y');

            DBCustomers.updateFinanceCustomer(updatedFinCustomer);
            cancelButtonHandler(event);
        }

    }

    /**
     This button handles the Cancel button being pressed, sending the user from this screen back to the main screen.
     @param event the event object presenting the Cancel button being pressed.
     @throws IOException the potential IOException that must be caught or declared to be thrown.
     */
    public void cancelButtonHandler(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeSceneTo(event, "/View/MainScreen.fxml");
    }

    /**
     This method is called whenever the Update Customer screen is loading. This method populates the Update Customer screen data fields with information
     retrieved from the user's previously selected Customer. These values are pre-populated into their respective data fields to allow the user to make
     changes to as many or little of the fields as desired.
     */
    public void initializeFields() {

        customerIdField.setText(Integer.toString(selectedCustomer.getId()));
        nameField.setText(selectedCustomer.getName());
        addressField.setText(selectedCustomer.getAddress());
        postalField.setText(selectedCustomer.getZipCode());
        phoneField.setText(selectedCustomer.getPhoneNum());
        countryField.getSelectionModel().select(selectedCustomer.getCountry());
        divisionField.getSelectionModel().select(selectedCustomer.getDivision());

        // works in IMS app, so it should work here as well
        if (selectedCustomer instanceof ProjectCustomer) {
            projectRadioButton.setSelected(true);
            radioButtonChanged();
            customerSpecialField.setText(((ProjectCustomer) selectedCustomer).getProjectName());

        } else if (selectedCustomer instanceof FinanceCustomer) {
            financeRadioButton.setSelected(true);
            radioButtonChanged();
            customerSpecialField.setText(((FinanceCustomer) selectedCustomer).includesCrypto() ? "Yes" : "No");

        }

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
     This method ensures the user has correctly completed all data fields for the new Customer object being created on this screen.
     The method contains several 'checks,' each checking a data field to ensure it contains appropriate data for that field. If any of these
     checks do not pass, the user will be notified by a text label and this method will return false. If all data fields are correctly filled out,
     this method returns true.
     @return true the return value returned by this method if all customer data fields are valid. Otherwise, returns false.
     */
    public boolean validNewCustomer() {
        // check that the nameField is valid input
        if (nameField.getText().isBlank() ||
                isNumber(nameField.getText()) ||
                nameField.getText().trim().length() > 50 ||
                (!nameField.getText().trim().matches("^[a-zA-Z\\s]+$"))) { // spaces only allowed

            errorTextField.setText("Name field input invalid");
            return false;

        } else if (addressField.getText().isBlank() ||
                addressField.getText().trim().length() > 100 ||
                isNumber(addressField.getText())) {

            errorTextField.setText("Address field input invalid");
            return false;

        } else if (postalField.getText().isBlank() ||
                postalField.getText().trim().length() > 50) {
            errorTextField.setText("Postal code field input invalid");
            return false;
        } else if (phoneField.getText().isBlank() ||
                phoneField.getText().trim().length() > 50) {
            errorTextField.setText("Phone number field input invalid");
            return false;
        } else if (countryField.getSelectionModel().isEmpty()) {
            errorTextField.setText("Please select a country");
            return false;
        } else if (divisionField.getSelectionModel().isEmpty()) {
            errorTextField.setText("Please select a state/province");
            return false;
        } else if (customerTypeToggleGroup.getSelectedToggle().equals(this.projectRadioButton)) {
            if (customerSpecialField.getText().length() > 50 ||
                    customerSpecialField.getText().isBlank() ||
                    isNumber(customerSpecialField.getText())) {
                errorTextField.setText("Please enter valid project name less than 50 characters");
                return false;
            }
        } else if (customerTypeToggleGroup.getSelectedToggle().equals(this.financeRadioButton)) {
            if (customerSpecialField.getText().trim().equalsIgnoreCase("yes") ||
                    customerSpecialField.getText().trim().equalsIgnoreCase("no")) {
                // do nothing
            } else {
                errorTextField.setText("Please only specify 'yes' OR 'no' about cryptocurrency");
                return false;
            }
        }
        return true; // all fields valid
    }
}