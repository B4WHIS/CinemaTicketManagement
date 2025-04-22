package gui;

import model.Product_Orders;
import model.Products;
import service.ProductManager;
import model.Orders;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ProductSelectionScreen extends JFrame {
    private ProductManager productManager;
    private List<Product_Orders> cart; 
    private Map<Integer, JLabel> quantityLabels; 
    private JLabel cartSummaryLabel; 
    private JLabel totalLabel; 

    public ProductSelectionScreen() {
        productManager = new ProductManager();
        cart = new ArrayList<>();
        quantityLabels = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setTitle("Food Menu");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel productPanel = new JPanel(new GridLayout(0, 1, 10, 10)); 
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Food Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Hiển thị danh sách sản phẩm
        try {
            List<Products> products = productManager.readAllProducts();
            for (Products product : products) {
                JPanel productRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                productRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel nameLabel = new JLabel(String.format("%s (%s)", product.getProductName(), product.getType() != null ? product.getType() : ""));
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                nameLabel.setPreferredSize(new Dimension(200, 30));

                JLabel priceLabel = new JLabel(String.format("%,.0f", product.getPrice()));
                priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                priceLabel.setPreferredSize(new Dimension(100, 30));

                JButton minusButton = new JButton("-");
                minusButton.setFont(new Font("Arial", Font.BOLD, 16));
                minusButton.setPreferredSize(new Dimension(40, 30));

                JLabel quantityLabel = new JLabel("0");
                quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                quantityLabel.setPreferredSize(new Dimension(30, 30));
                quantityLabels.put(product.getProductID(), quantityLabel);

                JButton plusButton = new JButton("+");
                plusButton.setFont(new Font("Arial", Font.BOLD, 16));
                plusButton.setPreferredSize(new Dimension(40, 30));

                // Sự kiện cho nút "-"
                minusButton.addActionListener(e -> {
                    int quantity = Integer.parseInt(quantityLabel.getText());
                    if (quantity > 0) {
                        quantity--;
                        quantityLabel.setText(String.valueOf(quantity));
                        updateCart(product.getProductID(), quantity, product);
                    }
                });

                // Sự kiện cho nút "+"
                plusButton.addActionListener(e -> {
                    int quantity = Integer.parseInt(quantityLabel.getText());
                    quantity++;
                    quantityLabel.setText(String.valueOf(quantity));
                    updateCart(product.getProductID(), quantity, product);
                });

                productRow.add(nameLabel);
                productRow.add(priceLabel);
                productRow.add(minusButton);
                productRow.add(quantityLabel);
                productRow.add(plusButton);
                productPanel.add(productRow);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        cartSummaryLabel = new JLabel("Có 0 món đặt hàng");
        cartSummaryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        totalLabel = new JLabel("Tổng cộng: 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
        summaryPanel.add(cartSummaryLabel);
        summaryPanel.add(totalLabel);
        bottomPanel.add(summaryPanel, BorderLayout.CENTER);

        // Nút đặt hàng và quay lạilại
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        JButton backButton = new JButton("← Quay lại");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.addActionListener(e -> {
            //giả sử có màn hình trước để quay lại có thể thay thế 
            dispose();
        });

        JButton orderButton = new JButton("Đặt hàng →");
        orderButton.setFont(new Font("Arial", Font.PLAIN, 16));
        orderButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else {
                // Chuyển sang màn hình xác nhận
                new ConfirmationScreen(cart).setVisible(true);
                dispose();
            }
        });

        navigationPanel.add(backButton);
        navigationPanel.add(orderButton);
        bottomPanel.add(navigationPanel, BorderLayout.SOUTH);

        mainPanel.add(new JScrollPane(productPanel), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void updateCart(int productID, int quantity, Products product) {
        // Xóa sản phẩm khỏi giỏ nếu đã có
        cart.removeIf(po -> po.getProductID() == productID);
        if (quantity > 0) {
            Product_Orders po = new Product_Orders();
            po.setProductID(productID);
            po.setQuantity(quantity);
            po.setPrice(product.getPrice());
            cart.add(po);
        }

        // Cập nhật tóm tắt giỏ hàng
        int totalItems = cart.stream().mapToInt(Product_Orders::getQuantity).sum();
        cartSummaryLabel.setText(String.format("Có %d món đặt hàng", totalItems));

        // Cập nhật tổng tiền
        double total = cart.stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
        totalLabel.setText(String.format("Tổng cộng: %,d", (int) total));
    }

    public static void main(String[] args) {
        ProductSelectionScreen screen = new ProductSelectionScreen();
        screen.setVisible(true);
    }
}