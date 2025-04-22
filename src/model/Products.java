package model;

public class Products {
    private int productID;
    private String productName;
    private double price;
    private String type;
    private byte[] image;

    public Products() {
        
    }

    public Products(int productID, String productName, double price, String type, byte[] image) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.type = type;
        this.image = image;
    }
    
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    
}
