package model;

import java.util.Date;
import java.util.List;

public class Orders {
    private int orderID;
    private Users userID;
    private double totalAmount;
    private Date orderDate;
    private PaymentMethod paymentMethod;
    private String movieTitle; // Thêm để lưu tên phim
    private String showtimeStartTime; // Thêm để lưu suất chiếu
    private String roomName; // Thêm để lưu tên phòng chiếu
    private List<Seats> seats; // Thêm để lưu danh sách ghế
    private List<Product_Orders> productOrders; // Thêm để lưu danh sách món ăn

    public Orders() {
    }
    public Orders(int orderID) {
    	this.orderID = orderID;
    }
    public Orders(int orderID, Users userID, double totalAmount, Date orderDate, PaymentMethod paymentMethod) {
        this.orderID = orderID;
        this.userID = userID;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters và Setters
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getShowtimeStartTime() {
        return showtimeStartTime;
    }

    public void setShowtimeStartTime(String showtimeStartTime) {
        this.showtimeStartTime = showtimeStartTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Seats> getSeats() {
        return seats;
    }

    public void setSeats(List<Seats> seats) {
        this.seats = seats;
    }

    public List<Product_Orders> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(List<Product_Orders> productOrders) {
        this.productOrders = productOrders;
    }
}