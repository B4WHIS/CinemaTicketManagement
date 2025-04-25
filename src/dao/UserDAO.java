package dao;

import dbs.connectDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Users;

public class UserDAO {
    private final Connection con;

    public UserDAO() {
        this.con = connectDB.getConnection();
    }

    public UserDAO(Connection conn) {
        this.con = conn;
    }

    private void ensureConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng.");
        }
    }

    // Thêm người dùng mới
    public void addUser(Users user) throws SQLException {
        ensureConnection();
        String query = "INSERT INTO Users (username, password, roleID) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPasswordHash());
            stmt.setInt(3, user.getRoleID());
            stmt.executeUpdate();
        }
    }

    // Lấy tất cả người dùng
    public List<Users> getAllUsers() throws SQLException {
        ensureConnection();
        List<Users> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        }
        return users;
    }

    // Lấy người dùng theo ID
    public Users getUserById(int userID) throws SQLException {
        ensureConnection();
        String query = "SELECT * FROM Users WHERE userID = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Đăng nhập người dùng (gọi stored procedure sp_LoginUser)
    public Users loginUser(String username, String password) throws SQLException {
        ensureConnection();

        Users user = null;
        String query = "{CALL sp_LoginUser(?, ?)}";
        try (CallableStatement stmt = con.prepareCall(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = createUserFromResultSet(rs);
                }
            }
        }
        return user;
    }

    // Cập nhật người dùng
    public void updateUser(Users user) throws SQLException {
        ensureConnection();
        String query = "UPDATE Users SET username = ?, password = ?, roleID = ? WHERE userID = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPasswordHash());
            stmt.setInt(3, user.getRoleID());
            stmt.setInt(4, user.getUserID());
            stmt.executeUpdate();
        }
    }

    // Xóa người dùng
    public void deleteUser(int userID) throws SQLException {
        ensureConnection();
        String query = "DELETE FROM Users WHERE userID = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        }
    }

    // Helper method to create User object from ResultSet
    private Users createUserFromResultSet(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setUserID(rs.getInt("userID"));
        user.setUserName(rs.getString("username"));
        user.setPasswordHash(rs.getString("password")); // Lưu ý: Đảm bảo cột trong DB là passwordHash
        user.setRoleID(rs.getInt("roleID"));
        return user;
    }

    // Đóng kết nối
    public void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}