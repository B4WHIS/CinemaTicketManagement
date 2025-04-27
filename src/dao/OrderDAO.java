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
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Kiểm tra xem cột OrderID có phải là IDENTITY không
            boolean isIdentity = false;
            String checkIdentitySql = "SELECT IDENT_SEED('Orders_Old') AS IdentitySeed"; // Sửa tên bảng thành Orders_Old
            try (PreparedStatement checkStmt = connection.prepareStatement(checkIdentitySql);
                 ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getObject("IdentitySeed") != null) {
                    isIdentity = true;
                }
            }

            int newOrderID = 0;
            if (!isIdentity) {
                // Nếu không phải IDENTITY, lấy OrderID lớn nhất và tăng lên 1
                String getMaxIDSql = "SELECT MAX(OrderID) FROM Orders_Old WITH (UPDLOCK, HOLDLOCK)"; // Sửa tên bảng
                try (PreparedStatement pstmt = connection.prepareStatement(getMaxIDSql);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        newOrderID = rs.getInt(1) + 1;
                    } else {
                        newOrderID = 1; // Nếu bảng rỗng, bắt đầu từ 1
                    }
                }
                order.setOrderID(newOrderID);
            }

            // Sử dụng đúng tên bảng Orders_Old
            String sql;
            if (isIdentity) {
                // Không bao gồm OrderID trong INSERT nếu là IDENTITY
                sql = "INSERT INTO Orders_Old (UserID, TotalAmount, OrderDate, PaymentMethodID) VALUES (?, ?, ?, ?)";
            } else {
                // Bao gồm OrderID nếu không phải IDENTITY
                sql = "INSERT INTO Orders_Old (OrderID, UserID, TotalAmount, OrderDate, PaymentMethodID) VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                int paramIndex = 1;
                if (!isIdentity) {
                    pstmt.setInt(paramIndex++, order.getOrderID());
                }
                pstmt.setInt(paramIndex++, order.getUserID().getUserID());
                pstmt.setDouble(paramIndex++, order.getTotalAmount());
                pstmt.setTimestamp(paramIndex++, new Timestamp(order.getOrderDate().getTime()));
                pstmt.setInt(paramIndex++, order.getPaymentMethod().getPaymentMethodID());
                pstmt.executeUpdate();

                if (isIdentity) {
                    // Lấy OrderID được tạo tự động
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            order.setOrderID(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Không thể lấy OrderID sau khi insert");
                        }
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    // Sửa tên bảng thành Orders_Old
    public List<Orders> getOrdersByUser(int userID) throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String query = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderDate, o.PaymentMethodID, " +
                      "u.UserName, pm.PaymentMethodName " +
                      "FROM Orders_Old o " +
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

    // Sửa tên bảng thành Orders_Old
    public Orders getOrderById(int orderID) throws SQLException {
        String query = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderDate, o.PaymentMethodID, " +
                      "u.UserName, pm.PaymentMethodName " +
                      "FROM Orders_Old o " +
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