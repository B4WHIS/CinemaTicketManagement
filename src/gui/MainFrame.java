package gui;

import java.awt.CardLayout;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
        this.userID = user.getUserID();
        this.showtimeID = 0;
        this.ticketID = 0;
        this.cart = new ArrayList<>();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        add(mainPanel);

        setTitle("Cinema Ticket Management");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void showMainGUI() {
        StaffGUI mainGuiPanel = new StaffGUI(connection, cardLayout, mainPanel, user, this);
        mainPanel.add(mainGuiPanel, "MainGUI");
        cardLayout.show(mainPanel, "MainGUI");
    }

    //B: Thêm phương thức showMainGUI2 để hiển thị giao diện cho admin (roleID = 1)
    public void showMainGUI2() {
        AdminGUI mainGui2Panel = new AdminGUI(connection, cardLayout, mainPanel, user, this);
        mainPanel.add(mainGui2Panel, "MainGUI2");
        cardLayout.show(mainPanel, "MainGUI2");
    }

    public void addToCart(List<Product_Orders> newCart) {
        cart.addAll(newCart);
    }

    public void clearCart() {
        cart.clear();
    }

    public ArrayList<Product_Orders> getCart() {
        return cart;
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

    public void showScreen(String screenName, JPanel screenPanel) {
        mainPanel.add(screenPanel, screenName);
        cardLayout.show(mainPanel, screenName);
    }

    public Connection getConnection() {
        return connection;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Users getUser() {
        return user;
    }
}