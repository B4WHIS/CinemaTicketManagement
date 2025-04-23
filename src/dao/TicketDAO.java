package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Orders;
import model.Showtimes;
import model.Tickets;

public class TicketDAO {
    private final Connection con;

    public TicketDAO() {
        this.con = connectDB.getConnection();
    }

    public TicketDAO(Connection conn) {
        this.con = conn;
    }

    // Thêm một vé mới
    public void saveTicket(Tickets ticket) throws SQLException {
        String query = "INSERT INTO Tickets (showtimeID, saleDate, orderID, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticket.getShowTimeID().getShowTimeID());
            ps.setObject(2, ticket.getSaleDate());
            ps.setInt(3, ticket.getOrderID().getOrderID());
            ps.setDouble(4, ticket.getPrice());
            ps.executeUpdate();
        }
    }

    // Lấy tất cả vé
    public List<Tickets> getAllTickets() throws SQLException {
        List<Tickets> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    // Lấy vé theo ID
    public Tickets getTicketById(int ticketID) throws SQLException {
        String query = "SELECT * FROM Tickets WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticketID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createTicketFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Lấy danh sách vé theo đơn hàng
    public List<Tickets> getTicketByOrder(int orderID) throws SQLException {
        List<Tickets> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets WHERE orderID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(createTicketFromResultSet(rs));
                }
            }
        }
        return tickets;
    }

    // Kiểm tra trạng thái ghế
    // Note: seatID column doesn't exist in Tickets table - verify database schema
    public boolean isSeat(int showtimeID, int seatID) throws SQLException {
        String query = "SELECT COUNT(*) FROM Tickets WHERE showtimeID = ? AND seatID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
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
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticket.getShowTimeID().getShowTimeID());
            ps.setObject(2, ticket.getSaleDate());
            ps.setInt(3, ticket.getOrderID().getOrderID());
            ps.setDouble(4, ticket.getPrice());
            ps.setInt(5, ticket.getTicketID());
            ps.executeUpdate();
        }
    }

    // Xóa vé
    public void deleteTicket(int ticketID) throws SQLException {
        String query = "DELETE FROM Tickets WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticketID);
            ps.executeUpdate();
        }
    }

    // Helper method to create Ticket object from ResultSet
    private Tickets createTicketFromResultSet(ResultSet rs) throws SQLException {
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