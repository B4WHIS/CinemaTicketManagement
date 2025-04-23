package gui;

import model.Product_Orders;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ConfirmationScreen extends JFrame implements ActionListener {
    private List<Product_Orders> cart;
    private JButton backButton;
    private JButton confirmButton;

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
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel nội dung
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Thông tin vé (dữ liệu giả lập)
        JPanel ticketHeaderPanel = createInfoRow("Thông Tin Vé:");
        ticketHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JLabel ticketHeader = (JLabel) ticketHeaderPanel.getComponent(0);
        ticketHeader.setFont(new Font("Times New Roman", Font.BOLD, 18));
        contentPanel.add(ticketHeaderPanel);

        contentPanel.add(createInfoRow("Tên phim: Phim ABC"));
        contentPanel.add(createInfoRow("Phòng chiếu: Phòng 1"));
        contentPanel.add(createInfoRow("Suất chiếu: 18:00, 22/04/2025"));
        contentPanel.add(createInfoRow("Ghế: A1"));

        // Đồ ăn
        JPanel foodHeaderPanel = createInfoRow("Đồ Ăn:");
        foodHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        JLabel foodHeader = (JLabel) foodHeaderPanel.getComponent(0);
        foodHeader.setFont(new Font("Times New Roman", Font.BOLD, 18));
        contentPanel.add(foodHeaderPanel);

        double cartTotal = 0.0;
        if (cart.isEmpty()) {
            contentPanel.add(createInfoRow("Không có món nào"));
        } else {
            for (Product_Orders po : cart) {
                // Giả lập thông tin sản phẩm
                String productName = "Sản phẩm " + po.getProductID(); // Tên giả lập
                String productInfo = String.format("%s x%d (%,.0f)", productName, po.getQuantity(), po.getPrice() * po.getQuantity());
                contentPanel.add(createInfoRow(productInfo));
                cartTotal += po.getPrice() * po.getQuantity();
            }
        }

        // Tổng tiền
        JPanel totalPanel = createInfoRow(String.format("Tổng tiền: %,d", (int) cartTotal));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentPanel.add(totalPanel);

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

        confirmButton = new JButton("Xác nhận →");
        confirmButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        confirmButton.addActionListener(this);
        navigationPanel.add(confirmButton, BorderLayout.EAST);

        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            new ProductSelectionScreen().setVisible(true);
            dispose();
        } else if (e.getSource() == confirmButton) {
            JOptionPane.showMessageDialog(this, "Đặt hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private JPanel createInfoRow(String text) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Giới hạn chiều cao

        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        rowPanel.add(label);

        return rowPanel;
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