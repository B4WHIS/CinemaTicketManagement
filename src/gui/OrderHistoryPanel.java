package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import model.Orders;
import model.Product_Orders;
import model.Seats;
import service.OrderManager;

public class OrderHistoryPanel extends JPanel {
    private OrderManager orderManager;
    private JTable orderTable;
    private JScrollPane scrollPane;

    public OrderHistoryPanel(Connection connection) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        try {
            orderManager = new OrderManager(connection);
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải lịch sử đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrders() {
        try {
            List<Orders> orders = orderManager.getAllOrders();
            if (orders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có đơn hàng nào trong cơ sở dữ liệu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] columnNames = {"Mã đơn hàng", "Phim", "Suất chiếu", "Phòng chiếu", "Ghế", "Món ăn", "Tổng tiền"};
            Object[][] data = new Object[orders.size()][7];

            for (int i = 0; i < orders.size(); i++) {
                Orders order = orders.get(i);
                data[i][0] = order.getOrderID();
                data[i][1] = order.getMovieTitle() != null ? order.getMovieTitle() : "Không xác định";
                data[i][2] = order.getShowtimeStartTime() != null ? order.getShowtimeStartTime() : "Không xác định";
                data[i][3] = order.getRoomName() != null ? order.getRoomName() : "Không xác định";
                
                StringBuilder seatsStr = new StringBuilder();
                List<Seats> seats = order.getSeats();
                if (seats != null && !seats.isEmpty()) {
                    for (Seats seat : seats) {
                        seatsStr.append(seat.getSeatNumber()).append(", ");
                    }
                    data[i][4] = seatsStr.length() > 0 ? seatsStr.substring(0, seatsStr.length() - 2) : "Không có ghế";
                } else {
                    data[i][4] = "Không có ghế";
                }

                StringBuilder productsStr = new StringBuilder();
                List<Product_Orders> productOrders = order.getProductOrders();
                if (productOrders != null && !productOrders.isEmpty()) {
                    for (Product_Orders po : productOrders) {
                        String productName = po.getProduct() != null ? po.getProduct().getProductName() : "Unknown";
                        productsStr.append(productName).append(" x").append(po.getQuantity()).append(", ");
                    }
                    data[i][5] = productsStr.length() > 0 ? productsStr.substring(0, productsStr.length() - 2) : "Không có món ăn";
                } else {
                    data[i][5] = "Không có món ăn";
                }

                data[i][6] = String.format("%,.0f", order.getTotalAmount());
            }

            orderTable = new JTable(data, columnNames);
            scrollPane = new JScrollPane(orderTable);
            add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}