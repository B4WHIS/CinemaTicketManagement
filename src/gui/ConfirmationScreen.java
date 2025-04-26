package gui;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.*;

import dao.OrderDAO;
import dao.ProductOrderDAO;
import dao.TicketDAO;
import model.Orders;
import model.Product_Orders;
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

    private JLabel lblMovieTitle;
    private JLabel lblShowtime;
    private JLabel lblSeats;
    private JLabel lblTotalAmount;
    private JButton btnConfirm;
    private JButton btnCancel;

    public ConfirmationScreen(MainFrame mainFrame, Users user, Showtimes showtime, List<Product_Orders> cart,
                              List<Seats> selectedSeats, int ticketQuantity, BigDecimal ticketPrice) {
        this.mainFrame = mainFrame;
        this.user = user;
        this.showtime = showtime;
        this.cart = cart;
        this.selectedSeats = selectedSeats;
        this.ticketQuantity = ticketQuantity;
        this.ticketPrice = ticketPrice;

        // Đặt kích thước cố định cho JPanel
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
        lblSeats = new JLabel("Ghế: " + getSelectedSeatsString());
        lblTotalAmount = new JLabel("Tổng tiền: " + getFormattedTotalAmount());

        btnConfirm = new JButton("Xác nhận");
        btnConfirm.setBackground(Color.GREEN);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> confirmBooking());

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> mainFrame.showScreen("SeatScreen", null)); // Quay lại màn hình chọn ghế

        infoPanel.add(lblMovieTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblShowtime);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblSeats);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblTotalAmount);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(btnConfirm);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(btnCancel);

        add(infoPanel, BorderLayout.CENTER);
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

    private double calculateTotalAmount() {
        double ticketTotal = ticketPrice.doubleValue() * ticketQuantity;
        double productTotal = cart.stream().mapToDouble(Product_Orders::getTotalPrice).sum();
        return ticketTotal + productTotal;
    }

    private String getFormattedTotalAmount() {
        return String.format("%,.2f", calculateTotalAmount());
    }

    private void confirmBooking() {
        Orders order = new Orders();
        order.setUserID(user);
        order.setTotalAmount(calculateTotalAmount()); // Sử dụng giá trị double trực tiếp
        order.setOrderDate(new java.util.Date());
        order.setPaymentMethod(new model.PaymentMethod(1, "Cash")); // Giả sử mặc định

        Connection conn = mainFrame.getConnection();
        try {
            conn.setAutoCommit(false);

            // Lưu đơn hàng
            OrderDAO orderDAO = new OrderDAO(conn);
            orderDAO.saveOrder(order);
            if (order.getOrderID() <= 0) {
                throw new SQLException("orderID không hợp lệ sau khi lưu đơn hàng: " + order.getOrderID());
            }
            System.out.println("Order saved with orderID: " + order.getOrderID());

            // Lưu sản phẩm trong đơn hàng
            ProductOrderDAO productOrderDAO = new ProductOrderDAO(conn);
            for (Product_Orders po : cart) {
                po.setOrderID(order);
                productOrderDAO.saveProductOrder(po);
            }

            // Lưu vé
            TicketDAO ticketDAO = new TicketDAO(conn);
            if (selectedSeats == null || selectedSeats.size() < ticketQuantity) {
                throw new IllegalStateException("Không đủ ghế đã chọn cho số lượng vé.");
            }
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
            mainFrame.showScreen("MainScreen", null); // Quay về màn hình chính sau khi đặt vé thành công
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