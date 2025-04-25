package gui;

import java.awt.CardLayout;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Product_Orders;
import model.Users;


public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int userID; 
    private int showtimeID; 
    private int ticketID; 
    private ArrayList<Product_Orders> cart; 
    private Connection connection;
    private Users user;

    public MainFrame(Connection connection, Users user) {
        this.connection = connection;
        this.user = user;
        this.userID = user.getUserID(); // Lấy userID từ đối tượng Users
        this.showtimeID = 0;
        this.ticketID = 0;
        this.cart = new ArrayList<>();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        add(mainPanel);

        setTitle("Cinema Ticket Management");
        setSize(1500, 1000); // Kích thước 1500x1000 cho MainFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Hiển thị mainGUI ngay khi mở MainFrame
        showMainGUI();
    }

    private void showMainGUI() {
        mainGUI mainGuiPanel = new mainGUI(connection, cardLayout, mainPanel, user);
        mainPanel.add(mainGuiPanel, "MainGUI");
        cardLayout.show(mainPanel, "MainGUI");
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getShowtimeID() {
        return showtimeID;
    }

    public void setShowtimeID(int showtimeID) {
        this.showtimeID = showtimeID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public ArrayList<Product_Orders> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Product_Orders> cart) {
        this.cart = cart;
    }

    public void addToCart(Product_Orders productOrder) {
        cart.add(productOrder);
    }

    public void clearCart() {
        cart.clear();
    }

    public void showScreen(String screenName, JPanel screenPanel) {
        mainPanel.add(screenPanel, screenName);
        cardLayout.show(mainPanel, screenName);
    }
}