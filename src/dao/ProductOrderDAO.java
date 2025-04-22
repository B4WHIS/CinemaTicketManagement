package dao;

import model.Product_Orders;
import model.Orders;
import model.Products;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ProductOrderDAO {
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:1433/databaseName=CinemaTickerManagement"; 
        String user = "sa";
        String password = "sapassword"; 
        return DriverManager.getConnection(url, user, password);
    }

    // Lưu chi tiết đơn hàng mới
    public void saveProductOrder(Product_Orders productOrder) throws SQLException {
        String query = "INSERT INTO Product_Orders (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
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
        String query = "SELECT po.*, p.productName, p.price AS productPrice, p.type, p.image " +
                      "FROM Product_Orders po " +
                      "JOIN Products p ON po.productID = p.productID " +
                      "WHERE po.orderID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
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
}