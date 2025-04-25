package dbs;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Lấy kết nối từ connectDB
            connection = connectDB.getConnection();
            
            // Kiểm tra xem kết nối có hợp lệ không
            if (connection != null && !connection.isClosed()) {
                System.out.println("Kết nối tới cơ sở dữ liệu thành công!");
                System.out.println("Thông tin kết nối: " + connection.getMetaData().getURL());
                System.out.println("Database: " + connection.getCatalog());
            } else {
                System.out.println("Kết nối không hợp lệ!");
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra kết nối: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng kết nối sau khi kiểm tra
            connectDB.closeConnection();
        }
    }
}