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
        String sql = "INSERT INTO ProductOrders (OrderID, ProductID, Quantity, TotalPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productOrder.getOrderID().getOrderID());
            stmt.setInt(2, productOrder.getProductID());
            stmt.setInt(3, productOrder.getQuantity());
            stmt.setDouble(4, productOrder.getTotalPrice());
            stmt.executeUpdate();
        }
    }

    public List<Product_Orders> getProductOrdersByOrder(int orderID) throws SQLException {
        List<Product_Orders> productOrders = new ArrayList<>();
        String sql = "SELECT po.ProductOrderID, po.ProductID, po.Quantity, po.TotalPrice, p.ProductName " +
                     "FROM ProductOrders po " + // Sửa Product_Orders thành ProductOrders
                     "JOIN Products p ON po.ProductID = p.ProductID " +
                     "WHERE po.OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
                    product.setProductID(rs.getInt("ProductID"));
                    product.setProductName(rs.getString("ProductName"));
                    po.setProduct(product);
                    productOrders.add(po);
                }
            }
        }
        return productOrders;
    }
}