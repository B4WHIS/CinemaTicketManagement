package service;

import dao.ProductDAO;
import model.Products;
import java.sql.SQLException;
import java.util.List;

public class ProductManager {
    private ProductDAO productDAO;

  
    public ProductManager() {
        this.productDAO = new ProductDAO();
    }

    // Create
    public void createProduct(Products product) throws SQLException {
        // kiem tra du lieu dau vao
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        productDAO.addProduct(product);
    }

    // Read: Lấy sản phẩm theo ID
    public Products readProduct(int productID) throws SQLException {
        if (productID <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        Products product = productDAO.getProductById(productID);
        if (product == null) {
            throw new SQLException("Product with ID " + productID + " not found");
        }
        return product;
    }

    // Read: Lấy tất cả sản phẩm
    public List<Products> readAllProducts() throws SQLException {
        return productDAO.getAllProducts();
    }

    // Update: Cập nhật sản phẩm
    public void updateProduct(Products product) throws SQLException {
        // Kiểm tra dữ liệu đầu vào
        if (product.getProductID() <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        if (productDAO.getProductById(product.getProductID()) == null) {
            throw new SQLException("Product with ID " + product.getProductID() + " not found");
        }

        productDAO.updateProduct(product);
    }

    // Delete
    public void deleteProduct(int productID) throws SQLException {
        if (productID <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        if (productDAO.getProductById(productID) == null) {
            throw new SQLException("Product with ID " + productID + " not found");
        }

        productDAO.deleteProduct(productID);
    }
}