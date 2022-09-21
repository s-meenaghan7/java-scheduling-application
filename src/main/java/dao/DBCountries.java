package dao;

import model.Country;
import utils.DBConnection;
import utils.DBQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 This class is responsible for data access methods of Country object information from a MySQL database.
 @author Sean
 */
public class DBCountries {

    private static ObservableList<Country> allCountries = FXCollections.observableArrayList();

    /**
     This method is responsible for querying the database for Country record information from the countries table, creating a Country object for each
     record found, and storing this information in the program's memory.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void setAllCountries() throws SQLException {
        // Get the Connection object to connect w/ DB
        Connection conn = DBConnection.getConnection();
        allCountries.clear();

        //SQL to select all from countries
        String sql = "SELECT Country_ID, Country FROM countries";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            // Assign each column value to a variable to load into the Customer constructor
            int countryId = rs.getInt("Country_ID");
            String name = rs.getString("Country");

            allCountries.add(new Country(countryId, name));
        }

    }

    /**
     Returns all Country objects stored in the program's memory.
     @return the ObservableList of Country objects that is returned.
     */
    public static ObservableList<Country> getAllCountries() {
        return allCountries;
    }

}
