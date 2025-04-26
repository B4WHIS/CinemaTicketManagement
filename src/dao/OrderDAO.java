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

    // Lưu đơn hàng mới
    public void saveOrder(Orders order) throws SQLException {
        String query = "INSERT INTO Orders (userID, totalPrice, orderDate, paymentMethodID) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserID().getUserID());
            stmt.setDouble(2, order.getTotalPrice());
            stmt.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
            stmt.setInt(4, order.getPaymentMethod().getPaymentMethodID());
            stmt.executeUpdate();

            // Lấy orderID được tạo tự động
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setOrderID(rs.getInt(1));
                }
            }
        }
    }

    // Lấy danh sách đơn hàng theo userID
    public List<Orders> getOrdersByUser(int userID) throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String query = "SELECT o.orderID, o.userID, o.totalPrice, o.orderDate, o.paymentMethodID, " +
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
                    
                    order.setTotalPrice(rs.getDouble("totalPrice"));
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

    // Lấy đơn hàng theo orderID
    public Orders getOrderById(int orderID) throws SQLException {
        String query = "SELECT o.orderID, o.userID, o.totalPrice, o.orderDate, o.paymentMethodID, " +
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
                    
                    order.setTotalPrice(rs.getDouble("totalPrice"));
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