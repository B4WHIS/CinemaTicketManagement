package dbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTicketManagement;encrypt=false;trustServerCertificate=true";
                String user = "sa";
                String pwd = "sapassword";
                connection = DriverManager.getConnection(url, user, pwd);
                System.out.println("Kết nối thành công!");
            } catch (SQLException e) {
                System.err.println("Lỗi kết nối: " + e.getMessage());
                throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu", e);
            }
        }
        return connection;
    }

    // Phương thức đóng kết nối
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Đã đóng kết nối.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
}
