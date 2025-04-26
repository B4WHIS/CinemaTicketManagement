package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        // Bắt đầu giao dịch để tránh xung đột đa luồng
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Lấy OrderID lớn nhất hiện tại
            int newOrderID = 1;
            String getMaxIDSql = "SELECT MAX(OrderID) FROM Orders WITH (UPDLOCK, HOLDLOCK)";
            try (PreparedStatement pstmt = connection.prepareStatement(getMaxIDSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    newOrderID = rs.getInt(1) + 1;
                }
            }

            // Gán OrderID cho đối tượng
            order.setOrderID(newOrderID);

            // Chèn bản ghi với OrderID
            String sql = "INSERT INTO Orders (OrderID, UserID, TotalAmount, OrderDate, PaymentMethodID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, order.getOrderID());
                pstmt.setInt(2, order.getUserID().getUserID());
                pstmt.setDouble(3, order.getTotalAmount());
                pstmt.setTimestamp(4, new Timestamp(order.getOrderDate().getTime()));
                pstmt.setInt(5, order.getPaymentMethod().getPaymentMethodID());
                pstmt.executeUpdate();
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
        String query = "SELECT o.orderID, o.userID, o.TotalAmount, o.orderDate, o.paymentMethodID, " +
                      "u.userName, pm.paymentMethodName " +
                      "FROM Orders o " +
                      "JOIN Users u ON o.userID = u.userID " +
                      "JOIN PaymentMethods pm ON o.paymentMethodID = pm.paymentMethodID " +
                      "WHERE o.userID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    
                    Users user = new Users();
                    user.setUserID(rs.getInt("userID"));
                    user.setUserName(rs.getString("userName"));
                    order.setUserID(user);
                    
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setOrderDate(new Date(rs.getTimestamp("orderDate").getTime()));
                    
                    PaymentMethod pm = new PaymentMethod();
                    pm.setPaymentMethodID(rs.getInt("paymentMethodID"));
                    pm.setPaymentMethodName(rs.getString("paymentMethodName"));
                    order.setPaymentMethod(pm);
                    
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public Orders getOrderById(int orderID) throws SQLException {
        String query = "SELECT o.orderID, o.userID, o.TotalAmount, o.orderDate, o.paymentMethodID, " +
                      "u.userName, pm.paymentMethodName " +
                      "FROM Orders o " +
                      "JOIN Users u ON o.userID = u.userID " +
                      "JOIN PaymentMethods pm ON o.paymentMethodID = pm.paymentMethodID " +
                      "WHERE o.orderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    
                    Users user = new Users();
                    user.setUserID(rs.getInt("userID"));
                    user.setUserName(rs.getString("userName"));
                    order.setUserID(user);
                    
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setOrderDate(new Date(rs.getTimestamp("orderDate").getTime()));
                    
                    PaymentMethod pm = new PaymentMethod();
                    pm.setPaymentMethodID(rs.getInt("paymentMethodID"));
                    pm.setPaymentMethodName(rs.getString("paymentMethodName"));
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