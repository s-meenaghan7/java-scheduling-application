package utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author Sean
 */
public class DBQuery {

    private static PreparedStatement statement; // PreparedStatement reference

    // Create statement object
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        statement = conn.prepareStatement(sqlStatement);
    }

    // Return Statement object
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }

}
