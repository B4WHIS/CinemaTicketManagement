package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import model.Orders;
import model.Showtimes;
import model.Tickets;

public class TicketDAO {

    // Thêm một vé mới
    public void saveTicket(Tickets ticket) throws SQLException {
        String query = "INSERT INTO Tickets (showtimeID, saleDate, orderID, price) VALUES (?, ?, ?, ?)";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticket.getShowTimeID().getShowTimeID()); // Lấy showTimeID từ đối tượng Showtimes
            ps.setObject(2, ticket.getSaleDate());
            ps.setInt(3, ticket.getOrderID().getOrderID()); // Lấy orderID từ đối tượng Orders
            ps.setDouble(4, ticket.getPrice());
            ps.executeUpdate();
        }
    }

    // Lấy tất cả vé (Read - danh sách)
    public List<Tickets> getAllTickets() throws SQLException {
        List<Tickets> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tickets ticket = new Tickets();
                ticket.setTicketID(rs.getInt("ticketID"));

                Showtimes showtime = new Showtimes();
                showtime.setShowTimeID(rs.getInt("showtimeID"));
                ticket.setShowTimeID(showtime);

                ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));

                Orders order = new Orders();
                order.setOrderID(rs.getInt("orderID"));
                ticket.setOrderID(order);

                ticket.setPrice(rs.getDouble("price"));
                tickets.add(ticket);
            }
        }
        return tickets; // Thêm return
    }

    // Lấy vé theo ID (Read - chi tiết)
    public Tickets getTicketById(int ticketID) throws SQLException {
        String query = "SELECT * FROM Tickets WHERE ticketID = ?"; // Sửa FORM thành FROM
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticketID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tickets ticket = new Tickets();
                    ticket.setTicketID(rs.getInt("ticketID"));

                    Showtimes showtime = new Showtimes();
                    showtime.setShowTimeID(rs.getInt("showtimeID"));
                    ticket.setShowTimeID(showtime);

                    ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));

                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    ticket.setOrderID(order);

                    ticket.setPrice(rs.getDouble("price"));
                    return ticket;
                }
            }
        }
        return null;
    }

    // Lấy danh sách vé theo đơn hàng (orderID)
    public List<Tickets> getTicketByOrder(int orderID) throws SQLException {
        List<Tickets> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets WHERE orderID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tickets ticket = new Tickets();
                    ticket.setTicketID(rs.getInt("ticketID"));

                    Showtimes showtime = new Showtimes();
                    showtime.setShowTimeID(rs.getInt("showtimeID"));
                    ticket.setShowTimeID(showtime);

                    ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));

                    Orders order = new Orders();
                    order.setOrderID(rs.getInt("orderID"));
                    ticket.setOrderID(order);

                    ticket.setPrice(rs.getDouble("price"));
                    tickets.add(ticket);
                }
            }
        }
        return tickets; // Thêm return
    }

    // Kiểm tra trạng thái ghế
    public boolean isSeat(int showtimeID, int seatID) throws SQLException {
        // Lưu ý: Cột seatID không tồn tại trong class Tickets. Cần kiểm tra lại yêu cầu.
        String query = "SELECT COUNT(*) FROM Tickets WHERE showtimeID = ? AND seatID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, showtimeID);
            ps.setInt(2, seatID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Cập nhật vé 
    public void updateTicket(Tickets ticket) throws SQLException {
        String query = "UPDATE Tickets SET showtimeID = ?, saleDate = ?, orderID = ?, price = ? WHERE ticketID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticket.getShowTimeID().getShowTimeID()); // Lấy showTimeID từ đối tượng Showtimes
            ps.setObject(2, ticket.getSaleDate());
            ps.setInt(3, ticket.getOrderID().getOrderID()); // Lấy orderID từ đối tượng Orders
            ps.setDouble(4, ticket.getPrice());
            ps.setInt(5, ticket.getTicketID());
            ps.executeUpdate();
        }
    }

    // Xóa vé
    public void deleteTicket(int ticketID) throws SQLException {
        String query = "DELETE FROM Tickets WHERE ticketID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticketID);
            ps.executeUpdate();
        }
    }
}