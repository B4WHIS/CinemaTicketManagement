package gui;

import model.Product_Orders;
import model.Products;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSelectionScreen extends JPanel implements ActionListener { // B: Changed from JFrame to JPanel
    private List<Product_Orders> cart;
    private Map<Integer, JLabel> quantityLabels;
    private Map<JButton, Products> minusButtonProductMap;
    private Map<JButton, Products> plusButtonProductMap;
    private Map<JButton, JLabel> buttonQuantityLabelMap;
    private JLabel totalLabel;
    private JButton backButton;
    private JButton orderButton;
    private MainFrame mainFrame; // B: Added MainFrame reference

    public ProductSelectionScreen(MainFrame mainFrame) { // B: Added MainFrame parameter
        this.mainFrame = mainFrame; // B: Store MainFrame
        cart = new ArrayList<>();
        quantityLabels = new HashMap<>();
        minusButtonProductMap = new HashMap<>();
        plusButtonProductMap = new HashMap<>();
        buttonQuantityLabelMap = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); // B: Replaced JFrame setup with JPanel layout
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Food Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel sản phẩm
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);

        // Dữ liệu giả lập cho danh sách sản phẩm
        List<Products> products = new ArrayList<>();
        products.add(new Products(1, "Bắp phô mai", 30000, "lớn", null));
        products.add(new Products(2, "Bắp", 50000, "nhỏ", null));
        products.add(new Products(3, "Pepsi", 20000, "", null));
        products.add(new Products(4, "Coca", 20000, "", null));
        products.add(new Products(5, "Combo1 (Bắp + Nước)", 60000, "", null));
        products.add(new Products(6, "Combo2", 80000, "", null));

        // Số lượng ban đầu
        int[] initialQuantities = {0, 0, 2, 2, 0, 0};

        for (int i = 0; i < products.size(); i++) {
            Products product = products.get(i);
            int initialQuantity = initialQuantities[i];

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
            JLabel quantityLabel = new JLabel(String.valueOf(initialQuantity));
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

            // Cập nhật giỏ hàng với số lượng ban đầu
            if (initialQuantity > 0) {
                Product_Orders po = new Product_Orders();
                po.setProductID(product.getProductID());
                po.setQuantity(initialQuantity);
                po.setPrice(product.getPrice());
                cart.add(po);
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

        add(new JScrollPane(productPanel), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
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
            // B: Replaced dispose() with navigation to SeatGUI
            // Note: Seat_GUI needs Rooms, showtimeID, MainFrame parameters
            // You need to provide these from MainFrame
            // mainFrame.showScreen("SeatGUI", new Seat_GUI(/* Rooms, showtimeID, mainFrame */));
            JOptionPane.showMessageDialog(this, "Cần cung cấp tham số cho Seat_GUI!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
        // Xử lý nút "Đặt hàng"
        else if (e.getSource() == orderButton) {
            // B: Replaced new JFrame with navigation to ConfirmationScreen
            mainFrame.addToCart(cart); // B: Save cart to MainFrame
            mainFrame.showScreen("Confirmation", new ConfirmationScreen(mainFrame, cart));
        }
    }

    private void updateCart(int productID, int quantity, Products product) {
        cart.removeIf(po -> po.getProductID() == productID);
        if (quantity > 0) {
            Product_Orders po = new Product_Orders();
            po.setProductID(productID);
            po.setQuantity(quantity);
            po.setPrice(product.getPrice());
            cart.add(po);
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
        totalLabel.setText(String.format("Tổng tiền: %,d", (int) total));
    }
}