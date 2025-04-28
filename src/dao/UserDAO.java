package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import các lớp cần thiết để làm việc với cơ sở dữ liệu
import dbs.connectDB;
import model.Users;

public class UserDAO {
    private final Connection con; 

    //LLấy kết nối từ connectDB
    public UserDAO() {
        this.con = connectDB.getConnection();
    }

    //CCho phép truyền vào một Connection từ bên ngoài
    public UserDAO(Connection conn) {
        this.con = conn;
    }

    //Kiểm tra xem kết nối
    private void ensureConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng.");
        }
    }

    // Phương thức để người dùng đăng nhập bằng username và password
    public Users loginUser(String username, String password) throws SQLException {
        ensureConnection(); // Kiểm tra kết nối

        Users user = null;
        String query = "{CALL sp_LoginUser(?, ?)}";
        
        try (CallableStatement stmt = con.prepareCall(query)) {
        
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Nếu user tồn tại -> tạo Users từ ResultSet
                    user = createUserFromResultSet(rs);
                   
                } else {
        
                    System.out.println("Không tìm thấy người dùng nào có tên đăng nhập là: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in loginUser: " + e.getMessage());
            throw e;
        }

        
        return user;
    }

    //Tạo Users từ dữ liệu ResultSet
    private Users createUserFromResultSet(ResultSet rs) throws SQLException {
        Users user = new Users();

        user.setUserID(rs.getInt("userID"));
        user.setUserName(rs.getString("username"));
        String fullName = rs.getString("fullName");
        user.setFullName(fullName);
        user.setPasswordHash(rs.getString("passwordHash"));
        
        user.setRoleID(rs.getInt("roleID"));

        return user; 
    }
}
