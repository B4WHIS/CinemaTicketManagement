package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import model.Product_Orders;

public class ConfirmationScreen extends JPanel implements ActionListener { // B: Changed from JFrame to JPanel
    private List<Product_Orders> cart;
    private JButton backButton;
    private JButton confirmButton;
    private MainFrame mainFrame; // B: Added MainFrame reference

    public ConfirmationScreen(MainFrame mainFrame, List<Product_Orders> cart) { // B: Added MainFrame parameter
        this.mainFrame = mainFrame; // B: Store MainFrame
        this.cart = cart;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); // B: Replaced JFrame setup with JPanel layout
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Xác Nhận Đặt Hàng", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

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
                String productName = "Sản phẩm " + po.getProductID();
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

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            // B: Replaced new JFrame with navigation to ProductSelectionScreen
            mainFrame.showScreen("ProductSelection", new ProductSelectionScreen(mainFrame));
        } else if (e.getSource() == confirmButton) {
            // B: Replaced dispose() with navigation to mainGUI and clear cart
            JOptionPane.showMessageDialog(this, "Đặt hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.clearCart(); // B: Clear cart after confirmation
            mainFrame.showScreen("MainGUI", new mainGUI(mainFrame.getConnection(), mainFrame.getCardLayout(), mainFrame.getMainPanel(), mainFrame.getUser(), mainFrame));
        }
    }

    private JPanel createInfoRow(String text) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        rowPanel.add(label);

        return rowPanel;
    }
}