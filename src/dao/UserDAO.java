package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbs.connectDB;
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
                    // Log để kiểm tra giá trị fullName
                    System.out.println("User FullName from DB in UserDAO: " + user.getFullName());
                    System.out.println("User UserName from DB in UserDAO: " + user.getUserName());
                } else {
                    System.out.println("No user found for username in UserDAO: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in loginUser: " + e.getMessage());
            throw e;
        }
        return user;
    }

    // Helper method to create User object from ResultSet
    private Users createUserFromResultSet(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setUserID(rs.getInt("userID"));
        user.setUserName(rs.getString("username"));
        String fullName = rs.getString("fullName"); // Sửa từ FULLNAME thành fullName (viết thường)
        System.out.println("FullName directly from ResultSet in UserDAO: " + fullName); // Log kiểm tra
        user.setFullName(fullName);
        user.setPasswordHash(rs.getString("passwordHash"));
        user.setRoleID(rs.getInt("roleID"));
        return user;
    }
}