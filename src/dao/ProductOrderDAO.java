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
        String query = "INSERT INTO Product_Orders (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productOrder.getOrderID().getOrderID());
            stmt.setInt(2, productOrder.getProductID());
            stmt.setInt(3, productOrder.getQuantity());
            stmt.setDouble(4, productOrder.getPrice());
            stmt.executeUpdate();
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
                    po.setPrice(rs.getDouble("price"));
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