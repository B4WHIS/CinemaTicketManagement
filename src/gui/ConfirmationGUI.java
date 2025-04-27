package gui;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.ProductOrderDAO;
import dao.TicketDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.Orders;
import model.Product_Orders;
import model.Products;
import model.Rooms;
import model.Seats;
import model.Showtimes;
import model.Tickets;
import model.Users;

public class ConfirmationGUI extends JPanel {
    private MainFrame mainFrame;
    private Users user;
    private Showtimes showtime;
    private List<Product_Orders> cart;
    private List<Seats> selectedSeats;
    private int ticketQuantity;
    private BigDecimal ticketPrice;
    private Rooms room;

    private JLabel lblMovieTitle;
    private JLabel lblShowtime;
    private JLabel lblRoom;
    private JLabel lblSeats;
    private JLabel lblCart;
    private JLabel lblTotalAmount;
    private JButton btnConfirm;
    private JButton btnBack;

    public ConfirmationGUI(MainFrame mainFrame, Users user, Showtimes showtime, List<Product_Orders> cart,
                           List<Seats> selectedSeats, int ticketQuantity, BigDecimal ticketPrice, Rooms room) {
        this.mainFrame = mainFrame;
        this.user = user;
        this.showtime = showtime;
        this.cart = cart;
        this.selectedSeats = selectedSeats;
        this.ticketQuantity = ticketQuantity;
        this.ticketPrice = ticketPrice;
        this.room = room;

        // Lấy thông tin sản phẩm cho từng mục trong cart
        loadProductDetails();

        // Thiết lập giao diện
        initializeUI();

        // Gán sự kiện cho các nút
        setupButtonListeners();
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(700, 600));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm tiêu đề
        JLabel titleLabel = new JLabel("Xác nhận đặt vé", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel chính chứa thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        lblMovieTitle = new JLabel("Phim: " + showtime.getMovie().getTitle());
        lblShowtime = new JLabel("Suất chiếu: " + showtime.getStartTime().toString());
        lblRoom = new JLabel("Phòng chiếu: " + (room != null ? room.getRoomName() : "Không xác định"));
        lblSeats = new JLabel("Ghế: " + getSelectedSeatsString());
        lblCart = new JLabel("Món ăn: " + getCartString());
        lblTotalAmount = new JLabel("Tổng tiền: " + getFormattedTotalAmount());

        // Panel chứa các nút điều hướng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(null);

        btnBack = new JButton("Trở về");
        btnBack.setBackground(Color.RED);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBackground(Color.GREEN);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        buttonPanel.add(btnBack);
        buttonPanel.add(btnConfirm);

        infoPanel.add(lblMovieTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblShowtime);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblRoom);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblSeats);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblCart);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblTotalAmount);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(buttonPanel);

        add(infoPanel, BorderLayout.CENTER);
    }

    private void setupButtonListeners() {
        btnBack.addActionListener(new BackButtonListener());
        btnConfirm.addActionListener(new ConfirmButtonListener());
    }

    // ActionListener cho nút "Trở về"
    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Quay lại ProductSelectionGUI
            ProductSelectionGUI pss = new ProductSelectionGUI(mainFrame, showtime.getShowTimeID(), ticketQuantity, ticketPrice, selectedSeats, room);
            mainFrame.showScreen("ProductSelection", pss);
        }
    }

    // ActionListener cho nút "Xác nhận"
    private class ConfirmButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedSeats.size() != ticketQuantity) {
                JOptionPane.showMessageDialog(ConfirmationGUI.this, "Số lượng ghế (" + selectedSeats.size() + ") không khớp với số lượng vé (" + ticketQuantity + ")!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Orders order = new Orders();
            order.setUserID(user);
            order.setTotalAmount(calculateTotalAmount());
            order.setOrderDate(new java.util.Date());
            order.setPaymentMethod(new model.PaymentMethod(1, "Cash"));

            Connection conn = mainFrame.getConnection();
            try {
                conn.setAutoCommit(false);

                OrderDAO orderDAO = new OrderDAO(conn);
                orderDAO.saveOrder(order);
                if (order.getOrderID() <= 0) {
                    throw new SQLException("orderID không hợp lệ sau khi lưu đơn hàng: " + order.getOrderID());
                }
                System.out.println("Order saved with orderID: " + order.getOrderID());

             // Lưu Product_Orders trực tiếp
                if (!cart.isEmpty()) {
                    // Lấy ProductOrderID lớn nhất hiện có và tăng lên 1
                    int nextProductOrderID;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT MAX(ProductOrderID) FROM Product_Orders");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nextProductOrderID = rs.getInt(1) + 1; // Tăng ProductOrderID lên 1
                        } else {
                            nextProductOrderID = 1; // Nếu bảng rỗng, bắt đầu từ 1
                        }
                    }

                    // Lưu trực tiếp vào bảng Product_Orders
                    String query = "INSERT INTO Product_Orders (ProductOrderID, orderID, productID, quantity, price) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(query)) {
                        for (Product_Orders po : cart) {
                            po.setOrderID(order);
                            ps.setInt(1, nextProductOrderID++); // Gán ProductOrderID và tăng lên cho bản ghi tiếp theo
                            ps.setInt(2, po.getOrderID().getOrderID());
                            ps.setInt(3, po.getProductID());
                            ps.setInt(4, po.getQuantity());
                            ps.setDouble(5, po.getTotalPrice()); // Sử dụng price thay vì TotalPrice
                            ps.executeUpdate();
                        }
                    }
                }
                TicketDAO ticketDAO = new TicketDAO(conn);
                for (int i = 0; i < ticketQuantity; i++) {
                    Tickets ticket = new Tickets();
                    Showtimes showtimeObj = new Showtimes();
                    showtimeObj.setShowTimeID(showtime.getShowTimeID());
                    ticket.setShowTimeID(showtimeObj);
                    ticket.setSaleDate(LocalDateTime.now());
                    ticket.setOrderID(order);
                    ticket.setPrice(ticketPrice.doubleValue());
                    ticket.setSeatID(selectedSeats.get(i));
                    System.out.println("Saving ticket with orderID: " + order.getOrderID());
                    ticketDAO.saveTicket(ticket);
                }

                conn.commit();
                JOptionPane.showMessageDialog(ConfirmationGUI.this, "Đặt vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.clearCart();

                // Chuyển về màn hình chính (MainGUI hoặc MainGUI2 dựa trên roleID của user)
                if (user.getRoleID() == 1) {
                    mainFrame.showMainGUI2(); // AdminGUI
                } else {
                    mainFrame.showMainGUI(); // StaffGUI
                }
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(ConfirmationGUI.this, "Lỗi khi lưu đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void loadProductDetails() {
        ProductDAO productDAO = new ProductDAO(mainFrame.getConnection());
        try {
            for (Product_Orders po : cart) {
                if (po.getProduct() == null) {
                    Products product = productDAO.getProductById(po.getProductID());
                    po.setProduct(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedSeatsString() {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return "Không có ghế nào được chọn";
        }
        StringBuilder sb = new StringBuilder();
        for (Seats seat : selectedSeats) {
            sb.append(seat.getSeatNumber()).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }

    private String getCartString() {
        if (cart == null || cart.isEmpty()) {
            return "Không có món ăn nào được chọn";
        }
        StringBuilder sb = new StringBuilder();
        for (Product_Orders po : cart) {
            String productName = (po.getProduct() != null && po.getProduct().getProductName() != null)
                                ? po.getProduct().getProductName()
                                : "Unknown Product";
            sb.append(productName)
              .append(" x")
              .append(po.getQuantity())
              .append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }

    private double calculateTotalAmount() {
        double ticketTotal = ticketPrice.doubleValue() * ticketQuantity;
        double productTotal = cart.stream().mapToDouble(Product_Orders::getTotalPrice).sum();
        return ticketTotal + productTotal;
    }

    private String getFormattedTotalAmount() {
        return String.format("%,.2f", calculateTotalAmount());
    }
}