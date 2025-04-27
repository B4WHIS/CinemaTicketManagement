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
import model.Products;

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
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Lấy ProductOrderID lớn nhất và tăng lên 1
            int newProductOrderID = 1;
            String getMaxIDSql = "SELECT MAX(ProductOrderID) FROM ProductOrders WITH (UPDLOCK, HOLDLOCK)";
            try (PreparedStatement pstmt = connection.prepareStatement(getMaxIDSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxID = rs.getInt(1);
                    if (rs.wasNull()) {
                        newProductOrderID = 1; // Nếu bảng rỗng, bắt đầu từ 1
                    } else {
                        newProductOrderID = maxID + 1;
                    }
                }
            }

            productOrder.setProductOrderID(newProductOrderID);
            System.out.println("Saving ProductOrder with ProductOrderID: " + productOrder.getProductOrderID()); // Gỡ lỗi

            // Kiểm tra giá trị ProductOrderID trước khi chèn
            if (productOrder.getProductOrderID() <= 0) {
                throw new SQLException("ProductOrderID không hợp lệ: " + productOrder.getProductOrderID());
            }

            String sql = "INSERT INTO ProductOrders (ProductOrderID, OrderID, ProductID, Quantity, TotalPrice) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, productOrder.getProductOrderID());
                pstmt.setInt(2, productOrder.getOrderID().getOrderID());
                pstmt.setInt(3, productOrder.getProductID());
                pstmt.setInt(4, productOrder.getQuantity());
                pstmt.setDouble(5, productOrder.getTotalPrice());
                pstmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Lỗi khi lưu ProductOrder: " + e.getMessage(), e);
        } finally {
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
                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    po.setOrderID(order);
                    po.setProductID(rs.getInt("productID"));
                    po.setQuantity(rs.getInt("quantity"));
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

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}