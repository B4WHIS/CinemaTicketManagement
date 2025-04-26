package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dao.ProductDAO;
import dao.Showtimes_DAO;
import model.Product_Orders;
import model.Products;
import model.Rooms;
import model.Seats;
import model.Showtimes;
import model.Users;

public class ProductSelectionScreen extends JPanel implements ActionListener {
    private List<Product_Orders> cart;
    private Map<Integer, JLabel> quantityLabels;
    private Map<JButton, Products> minusButtonProductMap;
    private Map<JButton, Products> plusButtonProductMap;
    private Map<JButton, JLabel> buttonQuantityLabelMap;
    private JLabel totalLabel;
    private JButton backButton;
    private JButton orderButton;
    private MainFrame mainFrame;
    private int showtimeID;
    private int ticketQuantity;
    private BigDecimal ticketPrice;
    private List<Seats> selectedSeats;
    private ProductDAO productDAO;
    private Rooms room;

    public ProductSelectionScreen(MainFrame mainFrame, int showtimeID, int ticketQuantity, BigDecimal ticketPrice, List<Seats> selectedSeats, Rooms room) {
        this.mainFrame = mainFrame;
        this.showtimeID = showtimeID;
        this.ticketQuantity = ticketQuantity;
        this.ticketPrice = ticketPrice;
        this.selectedSeats = selectedSeats;
        this.room = room;
        this.cart = new ArrayList<>();
        this.quantityLabels = new HashMap<>();
        this.minusButtonProductMap = new HashMap<>();
        this.plusButtonProductMap = new HashMap<>();
        this.buttonQuantityLabelMap = new HashMap<>();
        this.productDAO = new ProductDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Food Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel sản phẩm
        JPanel productPanel = new JPanel();
        productPanel.setBackground(Color.WHITE);
        productPanel.setLayout(new GridLayout(0, 4, 20, 20));

        // Lấy sản phẩm từ ProductDAO
        List<Products> products;
        try {
            products = productDAO.getAllProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Products product : products) {
            JPanel productCard = new JPanel();
            productCard.setLayout(new BorderLayout(5, 5));
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            productCard.setPreferredSize(new Dimension(200, 300));

            JLabel imageLabel = new JLabel("", JLabel.CENTER);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.GRAY);
            imageLabel.setPreferredSize(new Dimension(150, 150));
            productCard.add(imageLabel, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel(product.getProductName(), JLabel.CENTER);
            nameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(nameLabel);

            JLabel priceLabel = new JLabel(String.format("%,.0f VND", product.getPrice()), JLabel.CENTER);
            priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(priceLabel);

            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            quantityPanel.setBackground(Color.WHITE);

            JButton minusButton = new JButton("-");
            minusButton.setFont(new Font("Arial", Font.BOLD, 12));
            minusButton.setPreferredSize(new Dimension(45, 45));
            minusButton.setBackground(new Color(0xE0E0E0));
            minusButton.addActionListener(this);
            minusButtonProductMap.put(minusButton, product);

            JLabel quantityLabel = new JLabel("0", JLabel.CENTER);
            quantityLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
            quantityLabel.setPreferredSize(new Dimension(30, 30));
            quantityLabels.put(product.getProductID(), quantityLabel);
            buttonQuantityLabelMap.put(minusButton, quantityLabel);

            JButton plusButton = new JButton("+");
            plusButton.setFont(new Font("Arial", Font.BOLD, 12));
            plusButton.setPreferredSize(new Dimension(45, 45));
            plusButton.setBackground(new Color(0xE0E0E0));
            plusButton.addActionListener(this);
            plusButtonProductMap.put(plusButton, product);
            buttonQuantityLabelMap.put(plusButton, quantityLabel);

            quantityPanel.add(minusButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(plusButton);
            infoPanel.add(quantityPanel);

            productCard.add(infoPanel, BorderLayout.SOUTH);
            productPanel.add(productCard);
        }

        // Panel tổng tiền
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("Tổng tiền: 0");
        totalLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        summaryPanel.add(totalLabel);

        updateTotal();

        // Panel điều hướng
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        backButton = new JButton("← Quay lại");
        backButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        navigationPanel.add(backButton, BorderLayout.WEST);

        orderButton = new JButton("Đặt hàng →");
        orderButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        orderButton.setBackground(Color.GREEN);
        orderButton.setForeground(Color.WHITE);
        orderButton.addActionListener(this);
        navigationPanel.add(orderButton, BorderLayout.EAST);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(summaryPanel);
        southPanel.add(navigationPanel);

        add(new JScrollPane(productPanel), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (minusButtonProductMap.containsKey(e.getSource())) {
            JButton minusButton = (JButton) e.getSource();
            Products product = minusButtonProductMap.get(minusButton);
            JLabel quantityLabel = buttonQuantityLabelMap.get(minusButton);
            int quantity = Integer.parseInt(quantityLabel.getText());
            if (quantity > 0) {
                quantity--;
                quantityLabel.setText(String.valueOf(quantity));
                updateCart(product.getProductID(), quantity, product);
            }
        } else if (plusButtonProductMap.containsKey(e.getSource())) {
            JButton plusButton = (JButton) e.getSource();
            Products product = plusButtonProductMap.get(plusButton);
            JLabel quantityLabel = buttonQuantityLabelMap.get(plusButton);
            int quantity = Integer.parseInt(quantityLabel.getText());
            quantity++;
            quantityLabel.setText(String.valueOf(quantity));
            updateCart(product.getProductID(), quantity, product);
            JOptionPane.showMessageDialog(this, product.getProductName() + " đã được thêm!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == backButton) {
            try {
                Seat_GUI seatPanel = new Seat_GUI(room, showtimeID, ticketQuantity, ticketPrice, mainFrame);
                mainFrame.showScreen("SeatGUI", seatPanel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi quay lại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == orderButton) {
            Users user = mainFrame.getUser();
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Showtimes_DAO showtimeDAO = new Showtimes_DAO(mainFrame.getConnection());
            Showtimes showtime;
            try {
                showtime = showtimeDAO.getShowtimeByID(showtimeID);
                if (showtime == null) {
                    throw new IllegalStateException("Không tìm thấy suất chiếu với ID: " + showtimeID);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin suất chiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Truyền thêm đối tượng Rooms vào ConfirmationScreen
            ConfirmationScreen cs = new ConfirmationScreen(
                mainFrame, 
                user, 
                showtime, 
                cart, 
                selectedSeats, 
                ticketQuantity, 
                ticketPrice,
                room // Thêm đối tượng Rooms
            );
            mainFrame.showScreen("Confirmation", cs);
        }
    }

    private void updateCart(int productID, int quantity, Products product) {
        cart.removeIf(po -> po.getProductID() == productID);
        if (quantity > 0) {
            Product_Orders po = new Product_Orders();
            po.setProductID(productID);
            po.setQuantity(quantity);
            po.setTotalPrice(product.getPrice() * quantity);
            po.setProduct(product);
            cart.add(po);
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(po -> po.getTotalPrice()).sum();
        total += ticketPrice.doubleValue() * ticketQuantity;
        totalLabel.setText(String.format("Tổng tiền: %,d VND", (int) total));
    }
}