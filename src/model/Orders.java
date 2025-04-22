package model;

import java.util.Date;

public class Orders {
    private int orderID;
    private Users userID;
    private double totalPrice;
    private Date orderDate;
    private PaymentMethod paymentMethod;

    public Orders() {
        
    }
    public Orders(int orderID, Users userID, double totalPrice, Date orderDate, PaymentMethod paymentMethod) {
        this.orderID = orderID;
        this.userID = userID;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
    }
    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    public Users getUserID() {
        return userID;
    }
    public void setUserID(Users userID) {
        this.userID = userID;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
