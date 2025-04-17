package service;

import java.sql.SQLException;
import java.util.List;

import dao.UserDAO;
import model.Users;

public class UserManager {
    private UserDAO userDAO;

    // Constructor
    public UserManager() {
        this.userDAO = new UserDAO();
    }

    // Lấy tất cả người dùng (Read - danh sách)
    public List<Users> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    // Lấy người dùng theo ID (Read - chi tiết)
    public Users getUser(int userID) throws SQLException {
        Users user = userDAO.getUserById(userID);
        if (user == null) {
            throw new SQLException("Không tìm thấy người dùng với ID: " + userID);
        }
        return user;
    }

    // Đăng nhập người dùng (Read - đăng nhập)
    public Users loginUser(String username, String password) throws SQLException {
        Users user = userDAO.loginUser(username, password);
        if (user == null) {
            throw new SQLException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
        return user;
    }

    // Cập nhật người dùng (Update)
    public void updateUser(Users user) throws SQLException {
        // Kiểm tra logic nghiệp vụ
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        userDAO.updateUser(user);
    }
}