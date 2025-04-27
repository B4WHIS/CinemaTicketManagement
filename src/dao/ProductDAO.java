package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Products;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = connectDB.getConnection();
    }

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    // Lấy tất cả sản phẩm
    public List<Products> getAllProducts() throws SQLException {
        List<Products> products = new ArrayList<>();
        String query = "SELECT productID, productName, price, image FROM Products";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Products product = new Products();
                product.setProductID(rs.getInt("productID"));
                product.setProductName(rs.getString("productName"));
                product.setPrice(rs.getDouble("price"));
                product.setImage(rs.getString("image")); // Lấy đường dẫn hình ảnh dưới dạng String
                products.add(product);
            }
        }
        return products;
    }

    // Lấy sản phẩm theo ID
    public Products getProductById(int id) throws SQLException {
        String query = "SELECT productID, productName, price, image FROM Products WHERE productID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Products product = new Products();
                    product.setProductID(rs.getInt("productID"));
                    product.setProductName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("price"));
                    product.setImage(rs.getString("image")); // Lấy đường dẫn hình ảnh dưới dạng String
                    return product;
                }
            }
        }
        return null;
    }

    // Thêm sản phẩm mới
    public void addProduct(Products product) throws SQLException {
        String query = "INSERT INTO Products (productName, price, image) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getImage()); // Lưu đường dẫn hình ảnh dưới dạng String
            stmt.executeUpdate();
        }
    }

    // Cập nhật sản phẩm
    public void updateProduct(Products product) throws SQLException {
        String query = "UPDATE Products SET productName = ?, price = ?, image = ? WHERE productID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getImage()); // Cập nhật đường dẫn hình ảnh dưới dạng String
            stmt.setInt(4, product.getProductID());
            stmt.executeUpdate();
        }
    }

    // Xóa sản phẩm
    public void deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM Products WHERE productID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}