package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import model.Users;

public class UserDAO {

    // Thêm người dùng mới 
    public void addUser(Users user) throws SQLException {
        String query = "INSERT INTO Users (username, password, roleID) VALUES (?, ?, ?)";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPasswordHash());
            stmt.setInt(3, user.getRoleID());
            stmt.executeUpdate();
        }
    }

    // Lấy tất cả người dùng
    public List<Users> getAllUsers() throws SQLException {
        List<Users> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getInt("userID"));
                user.setUserName(rs.getString("username"));
                user.setPasswordHash(rs.getString("password"));
                user.setRoleID(rs.getInt("roleID"));
                users.add(user);
            }
        }
        return users;
    }

    // Lấy người dùng theo ID
    public Users getUserById(int userID) throws SQLException {
        String query = "SELECT * FROM Users WHERE userID = ?";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getInt("userID"));
                    user.setUserName(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password"));
                    user.setRoleID(rs.getInt("roleID"));
                    return user;
                }
            }
        }
        return null;
    }

    // Đăng nhập người dùng 
    public Users loginUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getInt("userID"));
                    user.setUserName(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password"));
                    user.setRoleID(rs.getInt("roleID"));
                    return user;
                }
            }
        }
        return null;
    }

    // Cập nhật người dùng
    public void updateUser(Users user) throws SQLException {
        String query = "UPDATE Users SET username = ?, password = ?, roleID = ? WHERE userID = ?";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPasswordHash());
            stmt.setInt(3, user.getRoleID());
            stmt.setInt(4, user.getUserID());
            stmt.executeUpdate();
        }
    }

    // Xóa người dùng 
    public void deleteUser(int userID) throws SQLException {
        String query = "DELETE FROM Users WHERE userID = ?";
        try (Connection conn = SQLServerConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        }
    }
}
