package dao;

import model.User;
import utils.DBQuery;
import utils.DBConnection;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 This class is responsible for data access methods of User object information from a MySQL database.
 @author Sean
 */
public class DBUsers {

    private static ObservableList<User> allUsers = FXCollections.observableArrayList();

    /**
     This method is responsible for retrieving all User information from the users table in the application database, saving all current user information into the program's memory for use elsewhere.
     @throws SQLException the potential SQLException that must be caught or declared to be thrown.
     */
    public static void setAllUsers() throws SQLException {

        // Get the Connection object to connect w/ DB
        Connection conn = DBConnection.getConnection();

        // SQL Statement for usernames and passwords
        String selectStatement = "SELECT User_ID, User_Name, Password FROM users;";

        // Prepared Statement
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Prepared Statement executed and returned to a ResultSet
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            // create user object based on current result in the set
            allUsers.add(new User(rs.getInt("User_ID"), rs.getString("User_Name"), rs.getString("Password")));
        }
    }

    /**
     This method retrieves a list of all Users contained in the database.
     @return allUsers the list of all Users contained in the database.
     */
    public static ObservableList<User> getAllUsers() {
        return allUsers;
    }

}
