package dao;

import model.User;
import model.Contact;
import model.Appointment;
import utils.DBQuery;
import utils.DBConnection;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 This class is responsible for data access methods of Appointment object information from a MySQL database.
 @author Seantest
 */
public class DBAppointments {

    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    /**
     This method is responsible for querying the database for appointment record information from the appointments table, creating an Appointment
     object for each record found, and storing this information in the program's memory.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void setAllAppointments() throws SQLException {
        // Get the Connection object to connect w/ DB
        Connection conn = DBConnection.getConnection();
        allAppointments.clear();

        // SQL Statement for finding all appointments and their relevant information
        String selectStatement = "SELECT Appointment_ID, Title, Description, Location, contacts.Contact_Name, Start, End, appointments.Customer_ID, appointments.User_ID "
                + "FROM appointments JOIN contacts ON appointments.Contact_ID=contacts.Contact_ID JOIN customers ON appointments.Customer_ID=customers.Customer_ID";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            // Assign each column value to a variable to load into the Appointment constructor
            int apptId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String contactName = rs.getString("Contact_Name");

            Contact contact = DBContacts.getAllContacts().stream().filter(c -> c.getName().equals(contactName)).findAny().orElseThrow();

            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            int customerId = rs.getInt("Customer_ID");
            int userId = rs.getInt("User_ID");

            // create a new Appointment object, using the above variables to define its fields
            Appointment newAppointment = new Appointment(apptId, title, description, location, contact, start, end, customerId, userId);

            allAppointments.add(newAppointment);
        }
    }

    /**
     This method is responsible for returning an ObservableList of all the Appointments stored in the program's memory, which were queried from the database.
     @return ObservableList of Appointment objects
     */
    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public static Appointment lookupAppointment(int appointmentId) {
        Appointment appointment = null;

        for (Appointment a : allAppointments) {
            if (a.getAppointmentId() == appointmentId) appointment = a;
        }

        return appointment;
    }

    public static ObservableList<Appointment> lookupAppointment(String appointmentName) {
        ObservableList<Appointment> matches = FXCollections.observableArrayList();
        String aptNameLowerCase = appointmentName.toLowerCase();

        allAppointments.stream().filter(a -> (a.getTitle().toLowerCase().contains(aptNameLowerCase)))
                .forEachOrdered(a -> matches.add(a));

        return matches;
    }

    /**
     This method is responsible for a SQL INSERT INTO operation to store a new Appointment object into the database.
     @param newAppointment the new Appointment object to be added to the database
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void addAppointment(Appointment newAppointment) throws SQLException {
        allAppointments.add(newAppointment);

        // convert start/end LocalDateTime objects into ZonedDateTime for the same instant but with UTC timezone
        ZonedDateTime startZoned = newAppointment.getStart().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endZoned = newAppointment.getEnd().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        // convert ZonedDateTime back to LocalDateTime for insertion to DB
        LocalDateTime startTime = startZoned.toLocalDateTime();
        LocalDateTime endTime = endZoned.toLocalDateTime();

        // connect to the DB
        Connection conn = DBConnection.getConnection();

        // INSERT INTO appointments
        String sql = "INSERT INTO appointments(Title, Description, Location, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                "VALUES(" + "'" + newAppointment.getTitle() + "', "
                + "'" + newAppointment.getDescription() + "', "
                + "'" + newAppointment.getLocation() + "', "
                + "'" + startTime + "', "
                + "'" + endTime + "', "
                + "now(), " // Create_Date
                + "'" + User.user.getUsername() + "', "
                + "now(), " // Last_Update
                + "'" + User.user.getUsername() + "', "
                + newAppointment.getCustomerId() + ", "
                + newAppointment.getUserId() + ", "
                + newAppointment.getContact().getId() + ")";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed, add customer to DBCustomers allCustomers
        ps.execute();
    }

    /**
     This method is responsible for sending a SQL UPDATE command to the database to update a current Appointment record.
     @param appointment the Appointment object being updated in the database.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void updateAppointment(Appointment appointment) throws SQLException {
        // the updated appointment object is passed in as parameter appointment
        // when this method finishes running, the screen will be changed back to the mainscreen and the setAppointments method will run again.

        // convert start/end LocalDateTime objects into ZonedDateTime for the same instant but with UTC timezone
        ZonedDateTime startZoned = appointment.getStart().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endZoned = appointment.getEnd().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        // convert ZonedDateTime back to LocalDateTime for insertion to DB
        LocalDateTime startTime = startZoned.toLocalDateTime();
        LocalDateTime endTime = endZoned.toLocalDateTime();

        Connection conn = DBConnection.getConnection();

        String sql = "UPDATE appointments " +
                "SET Title=" + "'" + appointment.getTitle() + "', " +
                "Description=" + "'" + appointment.getDescription() + "', " +
                "Location=" + "'" + appointment.getLocation() + "', " +
                "Start=" + "'" + startTime + "', " +
                "End=" + "'" + endTime + "', " +
                "Last_Update=now(), " +
                "Last_Updated_By=" + "'" + User.user.getUsername() + "', " +
                "Customer_ID=" + appointment.getCustomerId() + ", " +
                "User_ID=" + appointment.getUserId() + ", " +
                "Contact_ID=" + appointment.getContact().getId() + " " +
                "WHERE Appointment_ID=" + appointment.getAppointmentId();

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed, add customer to DBCustomers allCustomers
        ps.execute();

    }

    /**
     This method is responsible for sending a SQL DELETE FROM statement to the database to delete the specified appointment record.
     @param appointment the Appointment object being deleted from the database.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void deleteAppointment(Appointment appointment) throws SQLException {
        // remove the appointment from the ObservableArrayList
        allAppointments.remove(appointment);

        // connect to the DB to remove the customer from the DB
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM appointments WHERE Appointment_ID=" + appointment.getAppointmentId();

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
    }
}
