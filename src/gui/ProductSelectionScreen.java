package gui;

import model.Product_Orders;
import model.Products;
import service.ProductManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSelectionScreen extends JFrame implements ActionListener {
    private List<Product_Orders> cart;
    private Map<Integer, JLabel> quantityLabels;
    private Map<JButton, Products> minusButtonProductMap;
    private Map<JButton, Products> plusButtonProductMap;
    private Map<JButton, JLabel> buttonQuantityLabelMap;
    private JLabel totalLabel;
    private JButton backButton;
    private JButton orderButton;

    public ProductSelectionScreen() {
        cart = new ArrayList<>();
        quantityLabels = new HashMap<>();
        minusButtonProductMap = new HashMap<>();
        plusButtonProductMap = new HashMap<>();
        buttonQuantityLabelMap = new HashMap<>();

        initUI();
    }

    private void initUI() {
        setTitle("Food Menu");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Food Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel sản phẩm (dạng lưới)
        JPanel productPanel = new JPanel();
        productPanel.setBackground(Color.WHITE);
        productPanel.setLayout(new GridLayout(0, 4, 20, 20)); // 4 cột, khoảng cách 20px

        // Dữ liệu giả lập (dựa trên hình ảnh)
        List<Products> products = new ArrayList<>();
        products.add(new Products(1, "Mì Ý", 50000, "", null));
        products.add(new Products(2, "Trà sữa vị trà", 30000, "", null));
        products.add(new Products(3, "Món Việt", 45000, "", null));
        products.add(new Products(4, "Mì nước", 40000, "", null));
        products.add(new Products(5, "Cơm vị Xì", 35000, "", null));
        products.add(new Products(6, "Bánh mì", 25000, "", null));
        products.add(new Products(7, "Sinh tố nước ép", 30000, "", null));
        products.add(new Products(8, "Burger thịt", 55000, "", null));
        products.add(new Products(9, "Bò ăn chảnh", 70000, "", null));
        products.add(new Products(10, "Món Đông Tế", 40000, "", null));
        products.add(new Products(11, "Món Nhẹ", 25000, "", null));
        products.add(new Products(12, "Món Hàn", 50000, "", null));

        // Comment phần kết nối cơ sở dữ liệu
        /*
        // Lấy danh sách sản phẩm từ cơ sở dữ liệu
        List<Products> products;
        try {
            ProductManager productManager = new ProductManager();
            products = productManager.readAllProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể lấy danh sách sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            products = new ArrayList<>();
        }
        */

        for (Products product : products) {
            JPanel productCard = new JPanel();
            productCard.setLayout(new BorderLayout(5, 5));
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            productCard.setPreferredSize(new Dimension(200, 300));
        
            // Hình ảnh sản phẩm (nền xám vì chưa có hình)
            JLabel imageLabel = new JLabel("", JLabel.CENTER);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.GRAY);
            imageLabel.setPreferredSize(new Dimension(150, 150));
            productCard.add(imageLabel, BorderLayout.CENTER);
        
            // Panel thông tin sản phẩm
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
        
            // Tên sản phẩm
            JLabel nameLabel = new JLabel(product.getProductName(), JLabel.CENTER);
            nameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(nameLabel);
        
            // Giá sản phẩm
            JLabel priceLabel = new JLabel(String.format("%,.0f VND", product.getPrice()), JLabel.CENTER);
            priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(priceLabel);
        
            // Panel số lượng
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
            quantityLabel.setHorizontalAlignment(JLabel.CENTER);
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
            JOptionPane.showMessageDialog(this, product.getProductName() + " đã được thêm vào giỏ hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == backButton) {
            dispose();
        } else if (e.getSource() == orderButton) {
            new ConfirmationScreen(cart).setVisible(true);
            dispose();
        }
    }
    private void updateTotal() {
        double total = cart.stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
        totalLabel.setText(String.format("Tổng tiền: %,d", (int) total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductSelectionScreen().setVisible(true));
    }
}