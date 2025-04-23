package gui;
import javax.swing.*;

import model.Product_Orders;
import model.Products;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int userID; 
    private int showtimeID; 
    private int ticketID; 
    private ArrayList<Product_Orders> cart; 

    public MainFrame() {
        // Khởi tạo các biến
        userID = 0;
        showtimeID = 0;
        ticketID = 0;
        cart = new ArrayList<>();

        // Khởi tạo CardLayout và mainPanel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Thêm mainPanel vào JFrame
        add(mainPanel);

        // Thiết lập JFrame
        setTitle("Cinema Ticket Management");
        setSize(600, 500); // Kích thước lớn hơn để phù hợp với các màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Hiển thị màn hình đăng nhập khi khởi động
        showLogin();
    }

    // Phương thức hiển thị màn hình đăng nhập
    public void showLogin() {
        Login_GUI loginFrame = new Login_GUI();
        mainPanel.add(loginFrame.getContentPane(), "Login");
        cardLayout.show(mainPanel, "Login");
    }

    // Getter và Setter cho userID
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    // Getter và Setter cho showtimeID
    public int getShowtimeID() {
        return showtimeID;
    }

    public void setShowtimeID(int showtimeID) {
        this.showtimeID = showtimeID;
    }

    // Getter và Setter cho ticketID
    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    // Getter và Setter cho giỏ hàng
    public ArrayList<Product_Orders> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Product_Orders> cart) {
        this.cart = cart;
    }

    // Phương thức thêm sản phẩm vào giỏ hàng
    public void addToCart(Product_Orders productOrder) {
        cart.add(productOrder);
    }

    // Phương thức xóa giỏ hàng sau khi thanh toán
    public void clearCart() {
        cart.clear();
    }

    // Phương thức hiển thị các màn hình khác (sẽ được gọi từ các màn hình con)
    public void showScreen(String screenName, JPanel screenPanel) {
        mainPanel.add(screenPanel, screenName);
        cardLayout.show(mainPanel, screenName);
    }

    // Main method để chạy ứng dụng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}