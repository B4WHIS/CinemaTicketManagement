package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.ProductOrderDAO;
import dao.TicketDAO;
import model.Orders;
import model.PaymentMethod;
import model.Product_Orders;
import model.Products;
import model.Seats;
import model.Showtimes;
import model.Tickets;

public class ConfirmationScreen extends JPanel implements ActionListener {
    private List<Product_Orders> cart;
    private JButton backButton;
    private JButton confirmButton;
    private MainFrame mainFrame;
    private int showtimeID;
    private int ticketQuantity;
    private BigDecimal ticketPrice;
    private List<Seats> selectedSeats;
    private DecimalFormat priceFormatter;

    public ConfirmationScreen(MainFrame mainFrame, int showtimeID, int ticketQuantity, BigDecimal ticketPrice, List<Seats> selectedSeats, List<Product_Orders> cart) {
        this.mainFrame = mainFrame;
        this.showtimeID = showtimeID;
        this.ticketQuantity = ticketQuantity;
        this.ticketPrice = ticketPrice;
        this.selectedSeats = selectedSeats;
        this.cart = cart;
        this.priceFormatter = new DecimalFormat("#,###");
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
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

        // Thông tin vé
        try {
            String sql = "SELECT m.title, r.roomName, s.startTime FROM Showtimes s " +
                         "JOIN Movies m ON s.movieID = m.movieID " +
                         "JOIN Rooms r ON s.roomID = r.roomID WHERE s.showtimeID = ?";
            Connection conn = mainFrame.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, showtimeID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JPanel ticketHeaderPanel = createInfoRow("Thông Tin Vé:");
                ticketHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                JLabel ticketHeader = (JLabel) ticketHeaderPanel.getComponent(0);
                ticketHeader.setFont(new Font("Times New Roman", Font.BOLD, 18));
                contentPanel.add(ticketHeaderPanel);

                contentPanel.add(createInfoRow("Tên phim: " + rs.getString("title")));
                contentPanel.add(createInfoRow("Phòng chiếu: " + rs.getString("roomName")));
                contentPanel.add(createInfoRow("Suất chiếu: " + rs.getString("startTime")));
                StringBuilder seats = new StringBuilder();
                for (Seats seat : selectedSeats) {
                    seats.append(seat.getSeatNumber()).append(", ");
                }
                contentPanel.add(createInfoRow("Ghế: " + (seats.length() > 0 ? seats.substring(0, seats.length() - 2) : "N/A")));
                contentPanel.add(createInfoRow("Số lượng vé: " + ticketQuantity));
                contentPanel.add(createInfoRow("Giá vé: " + priceFormatter.format(ticketPrice.multiply(BigDecimal.valueOf(ticketQuantity))) + " VND"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            contentPanel.add(createInfoRow("Lỗi: Không thể lấy thông tin vé"));
        }

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
                String productName = getProductName(po.getProductID());
                String productInfo = String.format("%s x%d (%,.0f VND)", productName, po.getQuantity(), po.getPrice() * po.getQuantity());
                contentPanel.add(createInfoRow(productInfo));
                cartTotal += po.getPrice() * po.getQuantity();
            }
        }

        // Tổng tiền
        double ticketTotal = ticketPrice.doubleValue() * ticketQuantity;
        double total = ticketTotal + cartTotal;
        JPanel totalPanel = createInfoRow(String.format("Tổng tiền: %,d VND", (int) total));
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
            ProductSelectionScreen pss = new ProductSelectionScreen(mainFrame, showtimeID, ticketQuantity, ticketPrice, selectedSeats, selectedSeats.get(0).getRoom());
            mainFrame.showScreen("ProductSelection", pss);
        } else if (e.getSource() == confirmButton) {
            try {
                // Lưu đơn hàng vào Orders
                OrderDAO orderDAO = new OrderDAO(mainFrame.getConnection());
                Orders order = new Orders();
                order.setUserID(mainFrame.getUser());
                double total = ticketPrice.doubleValue() * ticketQuantity + 
                              cart.stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
                order.setTotalPrice(total);
                order.setOrderDate(new Date());
                PaymentMethod pm = new PaymentMethod();
                pm.setPaymentMethodID(1);
                order.setPaymentMethod(pm);
                orderDAO.saveOrder(order);

                // Lưu vé vào Tickets
                TicketDAO ticketDAO = new TicketDAO(mainFrame.getConnection());
                for (int i = 0; i < ticketQuantity; i++) {
                    Tickets ticket = new Tickets();
                    Showtimes showtime = new Showtimes();
                    showtime.setShowTimeID(showtimeID);
                    ticket.setShowTimeID(showtime);
                    ticket.setSaleDate(LocalDateTime.now());
                    ticket.setOrderID(order);
                    ticket.setPrice(ticketPrice.doubleValue());
                    ticketDAO.saveTicket(ticket);
                }

                // Lưu đồ ăn vào Product_Orders
                if (!cart.isEmpty()) {
                    ProductOrderDAO productOrderDAO = new ProductOrderDAO(mainFrame.getConnection());
                    for (Product_Orders po : cart) {
                        po.setOrderID(order);
                        productOrderDAO.saveProductOrder(po);
                    }
                }

                JOptionPane.showMessageDialog(this, "Đặt hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showScreen("MainGUI", new mainGUI(mainFrame.getConnection(), mainFrame.getCardLayout(), mainFrame.getMainPanel(), mainFrame.getUser(), mainFrame));
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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

    private String getProductName(int productID) {
        try {
            ProductDAO productDAO = new ProductDAO(mainFrame.getConnection());
            Products product = productDAO.getProductById(productID);
            return product != null ? product.getProductName() : "Sản phẩm " + productID;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Sản phẩm " + productID;
        }
    }
}