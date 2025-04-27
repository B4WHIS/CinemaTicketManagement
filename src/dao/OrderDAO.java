package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dbs.connectDB;
import model.Orders;
import model.PaymentMethod;
import model.Users;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        this.connection = connectDB.getConnection();
    }

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveOrder(Orders order) throws SQLException {
        // Bắt đầu giao dịch
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Chèn bản ghi mà không bao gồm OrderID (vì nó là cột identity)
            String sql = "INSERT INTO Orders (UserID, TotalAmount, OrderDate, PaymentMethodID) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getUserID().getUserID());
                pstmt.setDouble(2, order.getTotalAmount());
                pstmt.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
                pstmt.setInt(4, order.getPaymentMethod().getPaymentMethodID());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Không thể chèn bản ghi vào bảng Orders.");
                }

                // Lấy OrderID vừa tạo
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedOrderID = rs.getInt(1);
                        order.setOrderID(generatedOrderID);
                    } else {
                        throw new SQLException("Không thể lấy OrderID sau khi chèn bản ghi.");
                    }
                }
            }

            // Commit giao dịch
            connection.commit();
        } catch (SQLException e) {
            // Rollback nếu có lỗi
        	connection.rollback();
            throw new SQLException("Lỗi khi lưu Order: " + e.getMessage(), e);
        } finally {
            // Khôi phục trạng thái autoCommit
        	connection.setAutoCommit(autoCommit);
        }
    }

    public List<Orders> getOrdersByUser(int userID) throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String query = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderDate, o.PaymentMethodID, " +
                      "u.UserName, pm.PaymentMethodName " +
                      "FROM Orders o " +
                      "JOIN Users u ON o.UserID = u.UserID " +
                      "JOIN PaymentMethods pm ON o.PaymentMethodID = pm.PaymentMethodID " +
                      "WHERE o.UserID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("OrderID"));
                    
                    Users user = new Users();
                    user.setUserID(rs.getInt("UserID"));
                    user.setUserName(rs.getString("UserName"));
                    order.setUserID(user);
                    
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setOrderDate(new Date(rs.getTimestamp("OrderDate").getTime()));
                    
                    PaymentMethod pm = new PaymentMethod();
                    pm.setPaymentMethodID(rs.getInt("PaymentMethodID"));
                    pm.setPaymentMethodName(rs.getString("PaymentMethodName"));
                    order.setPaymentMethod(pm);
                    
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public Orders getOrderById(int orderID) throws SQLException {
        String query = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderDate, o.PaymentMethodID, " +
                      "u.UserName, pm.PaymentMethodName " +
                      "FROM Orders o " +
                      "JOIN Users u ON o.UserID = u.UserID " +
                      "JOIN PaymentMethods pm ON o.PaymentMethodID = pm.PaymentMethodID " +
                      "WHERE o.OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("OrderID"));
                    
                    Users user = new Users();
                    user.setUserID(rs.getInt("UserID"));
                    user.setUserName(rs.getString("UserName"));
                    order.setUserID(user);
                    
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setOrderDate(new Date(rs.getTimestamp("OrderDate").getTime()));
                    
                    PaymentMethod pm = new PaymentMethod();
                    pm.setPaymentMethodID(rs.getInt("PaymentMethodID"));
                    pm.setPaymentMethodName(rs.getString("PaymentMethodName"));
                    order.setPaymentMethod(pm);
                    
                    return order;
                }
            }
        }
        return null;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}