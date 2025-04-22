package dao;

import model.Products;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:1433/databaseName=CinemaTickerManagement"; 
        String user = "sa";
        String password = "sapassword"; 
        return DriverManager.getConnection(url, user, password);
    }

    // Lấy tất cả sản phẩm
    public List<Products> getAllProducts() throws SQLException {
        List<Products> products = new ArrayList<>();
        String query = "SELECT * FROM Products";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products();
                product.setProductID(rs.getInt("productID"));
                product.setProductName(rs.getString("productName"));
                product.setPrice(rs.getDouble("price"));
                product.setType(rs.getString("type"));
                product.setImage(rs.getBytes("image"));
                products.add(product);
            }
        }
        return products;
    }

    // Lấy sản phẩm theo ID
    public Products getProductById(int id) throws SQLException {
        String query = "SELECT * FROM Products WHERE productID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Products product = new Products();
                    product.setProductID(rs.getInt("productID"));
                    product.setProductName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("price"));
                    product.setType(rs.getString("type"));
                    product.setImage(rs.getBytes("image"));
                    return product;
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    // Thêm sản phẩm mới
    public void addProduct(Products product) throws SQLException {
        String query = "INSERT INTO Products (productName, price, type, image) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getType());
            stmt.setBytes(4, product.getImage());
            stmt.executeUpdate();
        }
    }

    // Cập nhật sản phẩm
    public void updateProduct(Products product) throws SQLException {
        String query = "UPDATE Products SET productName = ?, price = ?, type = ?, image = ? WHERE productID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getType());
            stmt.setBytes(4, product.getImage());
            stmt.setInt(5, product.getProductID());
            stmt.executeUpdate();
        }
    }

    // Xóa sản phẩm
    public void deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM Products WHERE productID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}