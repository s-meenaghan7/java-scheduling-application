package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class initializes a connection to a database.
 * @author Sean
 */
public class DBConnection {

    // Driver and connection interface references
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    // JDBC URL parts
    private static final String PROTOCOL = "jdbc";
    private static final String VENDOR = ":mysql://";
    private static final String HOST = "localhost";
    private static final String PORT = ":3306/";
    private static final String DATABASE = "consultant_scheduling"; // your database name here

    private static final String LOCAL_JDBC_URL = PROTOCOL + VENDOR + HOST + PORT + DATABASE + "?autoReconnect=true";

    // DB username & password. Specify your database credentials here.
    // // Used for both remote and local because you will only use one or the other
    private static final String USERNAME = "root";
    private static final String PASSWORD = "donotgogentle";

    /**
     * This method starts a connection with a local MySQL database
     * @return Returns the connection object
     */
    public static Connection startLocalConnection() {
        System.out.println("Attempting connection with local database...");

        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            conn = DriverManager.getConnection(LOCAL_JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connection successful.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * This method starts a remote connection to an AWS RDS MySQL database instance using SSH tunnneling.
     * @return Returns the connection object
     */
    public static Connection startRemoteConnection() {
        System.out.println("Attempting connection with remote database...");
        // JSCH_JDBC_URL created inside method call because the SSHConnection.assignedPort variable must be created first;
        // therefore, JDBC_URL cannot be static and initialized at runtime
        final String JSCH_JDBC_URL = PROTOCOL + VENDOR + HOST + JSChConnection.assignedPort + "/" + DATABASE + "?autoReconnect=true";

        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            conn = DriverManager.getConnection(JSCH_JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connection successful.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
            JSChConnection.disconnectSession();
        }

        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed");
        } catch (SQLException e) {}

    }

    public static Connection getConnection() {
        return conn;
    }

}
