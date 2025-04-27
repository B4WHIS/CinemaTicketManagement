package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class ProductOrderDAO {
    private Connection connection;

    public ProductOrderDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveProductOrder(Product_Orders productOrder) throws SQLException {
        // Bắt đầu giao dịch
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Chèn bản ghi mà không bao gồm ProductOrderID (vì nó là cột identity)
            String sql = "INSERT INTO ProductOrders (OrderID, ProductID, Quantity, TotalPrice) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, productOrder.getOrderID().getOrderID());
                pstmt.setInt(2, productOrder.getProductID());
                pstmt.setInt(3, productOrder.getQuantity());
                pstmt.setDouble(4, productOrder.getTotalPrice());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Không thể chèn bản ghi vào bảng ProductOrders.");
                }

                // Lấy ProductOrderID vừa tạo
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedProductOrderID = rs.getInt(1);
                        productOrder.setProductOrderID(generatedProductOrderID);
                    } else {
                        throw new SQLException("Không thể lấy ProductOrderID sau khi chèn bản ghi.");
                    }
                }
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
        String query = "SELECT po.*, p.productName, p.price AS productPrice, p.image " +
                      "FROM ProductOrders po " +
                      "JOIN Products p ON po.productID = p.productID " +
                      "WHERE po.orderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product_Orders po = new Product_Orders();
                    po.setProductOrderID(rs.getInt("ProductOrderID"));
                    po.setOrderID(new Orders(rs.getInt("OrderID")));
                    po.setProductID(rs.getInt("ProductID"));
                    po.setQuantity(rs.getInt("Quantity"));
                    po.setTotalPrice(rs.getDouble("TotalPrice"));
                    Products product = new Products();
                    product.setProductID(rs.getInt("productID"));
                    product.setProductName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("productPrice"));
                    product.setImage(rs.getString("image")); // Lấy đường dẫn hình ảnh dưới dạng String
                    po.setProduct(product);
                    productOrders.add(po);
                }
            }
        }
        return productOrders;
    }
}