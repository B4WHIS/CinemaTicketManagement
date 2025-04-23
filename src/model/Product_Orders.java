package model;

public class Product_Orders {
    private int productOrderID;
    private Orders orderID;
    private int productID;
    private int quantity;
    private double price;

    public Product_Orders() {
        
    }

    public Product_Orders(int productOrderID, Orders orderID, int productID, int quantity, double price) {
        this.productOrderID = productOrderID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }
    

    public Orders getOrderID() {
        return orderID;
    }

    public void setOrderID(Orders orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductOrderID() {
        return productOrderID;
    }

    public void setProductOrderID(int productOrderID) {
        this.productOrderID = productOrderID;
    }
}
