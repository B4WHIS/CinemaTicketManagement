package gui;

import model.Product_Orders;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfirmationScreen extends JFrame {
    private List<Product_Orders> cart;

    public ConfirmationScreen(List<Product_Orders> cart) {
        this.cart = cart;
        initUI();
    }

    private void initUI() {
        setTitle("Xác Nhận Đặt Hàng");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Xác Nhận Đặt Hàng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel nội dung
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 0, 2)); // Giảm vgap từ 10 xuống 2
        contentPanel.setBackground(Color.WHITE);

        // Thông tin vé (dữ liệu giả lập)
        JLabel ticketHeader = new JLabel("Thông Tin Vé:");
        ticketHeader.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(ticketHeader);

        contentPanel.add(createInfoLabel("Tên phim: Phim ABC"));
        contentPanel.add(createInfoLabel("Phòng chiếu: Phòng 1"));
        contentPanel.add(createInfoLabel("Suất chiếu: 18:00, 22/04/2025"));
        contentPanel.add(createInfoLabel("Ghế: A1"));

        // Đồ ăn (dữ liệu giả lập nếu cart rỗng)
        JLabel foodHeader = new JLabel("Đồ Ăn:");
        foodHeader.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(foodHeader);

        double cartTotal = 0.0;
        if (cart.isEmpty()) {
            // Dữ liệu giả lập
            contentPanel.add(createInfoLabel("Bắp phô mai x2 (60,000)"));
            contentPanel.add(createInfoLabel("Pepsi x1 (20,000)"));
            cartTotal = 80000;
        } else {
            for (Product_Orders po : cart) {
                // Giả lập thông tin sản phẩm
                String productName = "Sản phẩm " + po.getProductID(); // Tên giả lập
                String productInfo = String.format("%s x%d (%,.0f)", productName, po.getQuantity(), po.getPrice() * po.getQuantity());
                contentPanel.add(createInfoLabel(productInfo));
                cartTotal += po.getPrice() * po.getQuantity();
            }
        }

        // Tổng tiền
        JLabel totalLabel = new JLabel(String.format("Tổng tiền: %,d", (int) cartTotal));
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        contentPanel.add(totalLabel);

        // Panel điều hướng
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("← Quay lại");
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        backButton.addActionListener(e -> {
            new ProductSelectionScreen().setVisible(true);
            dispose();
        });
        navigationPanel.add(backButton, BorderLayout.WEST);

        JButton confirmButton = new JButton("Xác nhận →");
        confirmButton.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        confirmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đặt hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        navigationPanel.add(confirmButton, BorderLayout.EAST);

        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Tạo dữ liệu giả lập cho cart để kiểm tra giao diện
            List<Product_Orders> testCart = new ArrayList<>();
            Product_Orders po1 = new Product_Orders();
            po1.setProductID(1);
            po1.setQuantity(2);
            po1.setPrice(30000);
            testCart.add(po1);

            Product_Orders po2 = new Product_Orders();
            po2.setProductID(2);
            po2.setQuantity(1);
            po2.setPrice(20000);
            testCart.add(po2);

            ConfirmationScreen confirmationScreen = new ConfirmationScreen(testCart);
            confirmationScreen.setVisible(true);
        });
    }
}