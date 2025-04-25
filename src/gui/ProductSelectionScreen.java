package gui;

import model.Product_Orders;
import model.Products;
import service.ProductManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSelectionScreen extends JPanel implements ActionListener {
    private MainFrame mainFrame;
    private Connection connection;
    private int numberOfTickets;
    private double ticketPrice;
    private Map<Integer, JLabel> quantityLabels;
    private Map<JButton, Products> minusButtonProductMap;
    private Map<JButton, Products> plusButtonProductMap;
    private Map<JButton, JLabel> buttonQuantityLabelMap;
    private JLabel totalLabel;
    private JButton backButton;
    private JButton orderButton;

    public ProductSelectionScreen(MainFrame mainFrame, Connection connection, int numberOfTickets, double ticketPrice) {
        this.mainFrame = mainFrame;
        this.connection = connection;
        this.numberOfTickets = numberOfTickets;
        this.ticketPrice = ticketPrice;
        this.quantityLabels = new HashMap<>();
        this.minusButtonProductMap = new HashMap<>();
        this.plusButtonProductMap = new HashMap<>();
        this.buttonQuantityLabelMap = new HashMap<>();

        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Food Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel sản phẩm
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);

        // Lấy danh sách sản phẩm từ cơ sở dữ liệu
        List<Products> products;
        try {
            ProductManager productManager = new ProductManager();
            products = productManager.readAllProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể lấy danh sách sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            products = new ArrayList<>(); // Danh sách rỗng nếu lỗi
        }

        for (Products product : products) {
            JPanel productRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
            productRow.setBackground(Color.WHITE);
            productRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            // Tên và loại sản phẩm
            JLabel nameLabel = new JLabel(String.format("%s (%s)", product.getProductName(), product.getType() != null ? product.getType() : ""));
            nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            nameLabel.setPreferredSize(new Dimension(250, 30));

            // Giá sản phẩm
            JLabel priceLabel = new JLabel(String.format("%,.0f", product.getPrice()));
            priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            priceLabel.setPreferredSize(new Dimension(100, 30));

            // Nút giảm số lượng
            JButton minusButton = new JButton("-");
            minusButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
            minusButton.setPreferredSize(new Dimension(40, 40));
            minusButton.setBackground(Color.WHITE);
            minusButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            minusButton.addActionListener(this);
            minusButtonProductMap.put(minusButton, product);

            // Nhãn số lượng
            JLabel quantityLabel = new JLabel("0");
            quantityLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            quantityLabel.setPreferredSize(new Dimension(30, 30));
            quantityLabels.put(product.getProductID(), quantityLabel);
            buttonQuantityLabelMap.put(minusButton, quantityLabel);

            // Nút tăng số lượng
            JButton plusButton = new JButton("+");
            plusButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
            plusButton.setPreferredSize(new Dimension(40, 40));
            plusButton.setBackground(Color.WHITE);
            plusButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            plusButton.addActionListener(this);
            plusButtonProductMap.put(plusButton, product);
            buttonQuantityLabelMap.put(plusButton, quantityLabel);

            productRow.add(nameLabel);
            productRow.add(priceLabel);
            productRow.add(minusButton);
            productRow.add(quantityLabel);
            productRow.add(plusButton);
            productPanel.add(productRow);

            // Cập nhật số lượng ban đầu từ giỏ hàng của MainFrame (nếu có)
            Product_Orders existingOrder = mainFrame.getCart().stream()
                    .filter(po -> po.getProductID() == product.getProductID())
                    .findFirst()
                    .orElse(null);
            if (existingOrder != null) {
                quantityLabel.setText(String.valueOf(existingOrder.getQuantity()));
            }
        }

        // Panel tổng tiền
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("Tổng tiền: 0");
        totalLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        summaryPanel.add(totalLabel);

        // Cập nhật tổng tiền ban đầu
        updateTotal();

        // Panel điều hướng
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        backButton = new JButton("← Quay lại");
        backButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        backButton.addActionListener(this);
        navigationPanel.add(backButton, BorderLayout.WEST);

        orderButton = new JButton("Đặt hàng →");
        orderButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        orderButton.setBackground(Color.WHITE);
        orderButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        orderButton.addActionListener(this);
        navigationPanel.add(orderButton, BorderLayout.EAST);

        // Panel chứa cả tổng tiền và điều hướng
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(summaryPanel);
        southPanel.add(navigationPanel);

        mainPanel.add(new JScrollPane(productPanel), BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Xử lý nút "-"
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
        }
        // Xử lý nút "+"
        else if (plusButtonProductMap.containsKey(e.getSource())) {
            JButton plusButton = (JButton) e.getSource();
            Products product = plusButtonProductMap.get(plusButton);
            JLabel quantityLabel = buttonQuantityLabelMap.get(plusButton);
            int quantity = Integer.parseInt(quantityLabel.getText());
            quantity++;
            quantityLabel.setText(String.valueOf(quantity));
            updateCart(product.getProductID(), quantity, product);
            JOptionPane.showMessageDialog(this, product.getProductName() + " đã được thêm vào giỏ hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        // Xử lý nút "Quay lại"
        else if (e.getSource() == backButton) {
            mainFrame.showScreen("DetailFilm", new DetailFilm_GUI(mainFrame, connection, 1)); // Giả sử movieID = 1
        }
        // Xử lý nút "Đặt hàng"
        else if (e.getSource() == orderButton) {
            ConfirmationScreen confirmationScreen = new ConfirmationScreen(mainFrame, connection, numberOfTickets, ticketPrice, mainFrame.getCart());
            mainFrame.showScreen("Confirmation", confirmationScreen);
        }
    }

    private void updateCart(int productID, int quantity, Products product) {
        // Xóa sản phẩm khỏi giỏ hàng của MainFrame
        mainFrame.getCart().removeIf(po -> po.getProductID() == productID);

        // Thêm lại nếu số lượng > 0
        if (quantity > 0) {
            Product_Orders po = new Product_Orders();
            po.setProductID(productID);
            po.setQuantity(quantity);
            po.setPrice(product.getPrice());
            mainFrame.addToCart(po);
        }

        // Cập nhật tổng tiền
        updateTotal();
    }

    private void updateTotal() {
        double total = mainFrame.getCart().stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
        totalLabel.setText(String.format("Tổng tiền: %,d", (int) total));
    }
}