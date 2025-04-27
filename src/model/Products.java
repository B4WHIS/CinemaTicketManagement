package model;

public class Products {
    private int productID;
    private String productName;
    private double price;
    private String image; // Trường image là String để lưu đường dẫn hình ảnh

    // Constructor mặc định
    public Products() {
    }

    // Constructor đầy đủ
    public Products(int productID, String productName, double price, String image) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.image = image;
    }

    // Getters và Setters
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}