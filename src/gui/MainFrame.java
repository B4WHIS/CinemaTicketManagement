package gui;

import java.awt.CardLayout;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List; // B: Added import for List
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

    // Constructor và các phương thức khác giữ nguyên
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

        showMainGUI();
    }

    private void showMainGUI() {
        mainGUI mainGuiPanel = new mainGUI(connection, cardLayout, mainPanel, user, this);
        mainPanel.add(mainGuiPanel, "MainGUI");
        cardLayout.show(mainPanel, "MainGUI");
    }

    // Sửa phương thức addToCart
    public void addToCart(List<Product_Orders> newCart) { // B: Changed parameter from Product_Orders to List<Product_Orders>
        cart.addAll(newCart); // B: Add all items from newCart to cart
    }

    // Các phương thức khác giữ nguyên
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