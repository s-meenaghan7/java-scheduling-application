package dao;

import model.Contact;
import utils.DBConnection;
import utils.DBQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

/**
 This class is responsible for data access methods of Contact object information from a MySQL database.
 @author Sean
 */
public class DBContacts {

    private static ObservableList<Contact> allContacts = FXCollections.observableArrayList();

    /**
     This method is responsible for querying the database for Contact record information from the contacts table, creating a Contact object for each
     record found, and storing this information in the program's memory.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void setAllContacts() throws SQLException {
        // Get the Connection object to connect w/ DB
        Connection conn = DBConnection.getConnection();
        allContacts.clear();

        //SQL to select all from contacts
        String sql = "SELECT * FROM contacts";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            int contactId = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            String contactType = rs.getString("Type");

            allContacts.add(new Contact(contactId, contactName, contactEmail, contactType));
        }
    }

    /**
     Returns all Contact objects stored in the program's memory.
     @return the ObservableList of Contact objects returned.
     */
    public static ObservableList<Contact> getAllContacts() {
        return allContacts;
    }

}
