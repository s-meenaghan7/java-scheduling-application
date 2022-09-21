package dao;

import model.FirstLevelDivision;
import utils.DBConnection;
import utils.DBQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 This class is responsible for data access methods of FirstLevelDivision object information from a MySQL database.
 @author Sean
 */
public class DBDivisions {

    private static ObservableList<FirstLevelDivision> allDivisions = FXCollections.observableArrayList();

    /**
     This method is responsible for querying the database for FirstLevelDivision record information from the first_level_divisions table, creating a
     FirstLevelDivision object for each record found, and storing this information in the program's memory.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void setAllDivisions() throws SQLException {
        // Get the Connection object to connect w/ DB
        Connection conn = DBConnection.getConnection();
        allDivisions.clear();

        // SQL to select all from first_level_divisions
        String sql = "SELECT Division_ID, Division, COUNTRY_ID FROM first_level_divisions";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            // Assign each column value to a variable to load into the Customer constructor
            int divId = rs.getInt("Division_ID");
            String divName = rs.getString("Division");
            int countryId = rs.getInt("COUNTRY_ID");

            allDivisions.add(new FirstLevelDivision(divId, divName, countryId));
        }
    }

    /**
     This method is responsible for returning all the FirstLevelDivision object data stored in the program's memory.
     @return the ObservableList of FirstLevelDivision objects
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions() {
        return allDivisions;
    }

}
