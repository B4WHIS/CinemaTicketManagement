package dbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.IntStream;

public class connectDB {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
            	String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTicketManagement;integratedSecurity=true";
             
                connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
