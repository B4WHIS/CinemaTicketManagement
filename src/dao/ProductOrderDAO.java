package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Orders;
import model.Product_Orders;

public class ProductOrderDAO {
    private Connection connection;

    public ProductOrderDAO() {
        this.connection = connectDB.getConnection();
    }

    public ProductOrderDAO(Connection connection) {
        this.connection = connection;
    }

    // Lưu chi tiết đơn hàng mới
    public void saveProductOrder(Product_Orders productOrder) throws SQLException {
        // Bắt đầu giao dịch để tránh xung đột đa luồng
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Lấy ProductOrderID lớn nhất hiện tại
            int newProductOrderID = 1;
            String getMaxIDSql = "SELECT MAX(ProductOrderID) FROM ProductOrders WITH (UPDLOCK, HOLDLOCK)";
            try (PreparedStatement pstmt = connection.prepareStatement(getMaxIDSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    newProductOrderID = rs.getInt(1) + 1;
                }
            }

            // Gán ProductOrderID cho đối tượng
            productOrder.setProductOrderID(newProductOrderID);

            // Chèn bản ghi với ProductOrderID
            String sql = "INSERT INTO ProductOrders (ProductOrderID, OrderID, ProductID, Quantity, TotalPrice) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, productOrder.getProductOrderID());
                pstmt.setInt(2, productOrder.getOrderID().getOrderID());
                pstmt.setInt(3, productOrder.getProductID());
                pstmt.setInt(4, productOrder.getQuantity());
                pstmt.setDouble(5, productOrder.getTotalPrice());
                pstmt.executeUpdate();
            }

            // Commit giao dịch
            connection.commit();
        } catch (SQLException e) {
            // Rollback nếu có lỗi
            connection.rollback();
            throw new SQLException("Lỗi khi lưu ProductOrder: " + e.getMessage(), e);
        } finally {
            // Khôi phục trạng thái autoCommit
        	connection.setAutoCommit(autoCommit);
        }
    }

    // Lấy danh sách chi tiết đơn hàng theo orderID
    public List<Product_Orders> getProductOrdersByOrder(int orderID) throws SQLException {
        List<Product_Orders> productOrders = new ArrayList<>();
        // B: Sửa truy vấn SQL để không lấy cột 'type' không tồn tại
        String query = "SELECT po.*, p.productName, p.price AS productPrice, p.image " +
                      "FROM Product_Orders po " +
                      "JOIN Products p ON po.productID = p.productID " +
                      "WHERE po.orderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product_Orders po = new Product_Orders();
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    po.setOrderID(order);
                    po.setProductID(rs.getInt("productID"));
                    po.setQuantity(rs.getInt("quantity"));
                    po.setTotalPrice(rs.getDouble("price"));
                    productOrders.add(po);
                }
            }
        }
        return productOrders;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}