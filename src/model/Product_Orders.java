package model;

public class Product_Orders {
    private int productOrderID;
    private Orders orderID;
    private int productID;
    private int quantity;
    private double totalPrice;
    private Products product; 

    public Product_Orders() {
    }

    public Product_Orders(int productOrderID, Orders orderID, int productID, int quantity, double totalPrice) {
        this.productOrderID = productOrderID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

	public int getProductOrderID() {
		return productOrderID;
	}

	public void setProductOrderID(int productOrderID) {
		this.productOrderID = productOrderID;
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

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Products getProduct() {
		return product;
	}

	public void setProduct(Products product) {
		this.product = product;
	}

   
    
}