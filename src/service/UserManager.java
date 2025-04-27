package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbs.connectDB;
import model.Users;

public class UserManager {
    private Connection connection;

    public UserManager(Connection connection) {
        this.connection = connection;
    }

    public Users loginUser(String username, String password) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = connectDB.getConnection();
            } catch (RuntimeException e) {
                throw new SQLException("Không thể lấy lại kết nối tới cơ sở dữ liệu: " + e.getMessage(), e);
            }
        }

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Kết nối tới cơ sở dữ liệu không hợp lệ (null hoặc đã đóng).");
        }

        Users user = null;
        String query = "{CALL sp_LoginUser(?, ?)}";
        
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new Users();
                user.setUserID(rs.getInt("userID"));
                user.setUserName(rs.getString("username"));
                user.setPasswordHash(rs.getString("passwordHash")); // Sửa thành passwordHash
                user.setRoleID(rs.getInt("roleID"));
            }
        }
        return user;
    }
}