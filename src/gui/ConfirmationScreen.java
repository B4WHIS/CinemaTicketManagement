package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
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

import dao.OrderDAO;
import dao.ProductDAO;
import dao.ProductOrderDAO;
import dao.TicketDAO;
import model.Orders;
import model.Product_Orders;
import model.Products;
import model.Rooms;
import model.Seats;
import model.Showtimes;
import model.Tickets;
import model.Users;

public class ConfirmationScreen extends JPanel {
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

    // Sửa tên constructor từ public_RANGE_SCREEN thành ConfirmationScreen
    public ConfirmationScreen(MainFrame mainFrame, Users user, Showtimes showtime, List<Product_Orders> cart,
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

        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm tiêu đề
        JLabel titleLabel = new JLabel("Xác nhận đặt vé", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
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
        btnBack.addActionListener(e -> mainFrame.showScreen("SeatScreen", null));

        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBackground(Color.GREEN);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        btnConfirm.addActionListener(e -> confirmBooking());

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

    private void confirmBooking() {
        if (selectedSeats.size() != ticketQuantity) {
            JOptionPane.showMessageDialog(this, "Số lượng ghế (" + selectedSeats.size() + ") không khớp với số lượng vé (" + ticketQuantity + ")!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

            ProductOrderDAO productOrderDAO = new ProductOrderDAO(conn);
            for (Product_Orders po : cart) {
                po.setOrderID(order);
                productOrderDAO.saveProductOrder(po);
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
            JOptionPane.showMessageDialog(this, "Đặt vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showScreen("MainScreen", null);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}