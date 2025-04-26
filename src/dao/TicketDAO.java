package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Orders;
import model.Showtimes;
import model.Tickets;
import model.Seats;

public class TicketDAO {
    private final Connection con;

    public TicketDAO() {
        this.con = connectDB.getConnection();
    }

    public TicketDAO(Connection conn) {
        this.con = conn;
    }

    public void saveTicket(Tickets ticket) throws SQLException {
        // Lấy TicketID lớn nhất hiện tại
        String getMaxIdSql = "SELECT MAX(TicketID) FROM Tickets";
        int newTicketId = 1; // Giá trị mặc định nếu bảng rỗng
        try (PreparedStatement maxStmt = con.prepareStatement(getMaxIdSql)) {
            try (ResultSet rs = maxStmt.executeQuery()) {
                if (rs.next()) {
                    newTicketId = rs.getInt(1) + 1;
                }
            }
        }

        ticket.setTicketID(newTicketId);

        String sql = "INSERT INTO Tickets (TicketID, showTimeID, saleDate, orderID, seatID, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getTicketID());
            stmt.setInt(2, ticket.getShowTimeID().getShowTimeID());
            stmt.setTimestamp(3, Timestamp.valueOf(ticket.getSaleDate()));
            int orderId = ticket.getOrderID().getOrderID();
            System.out.println("Inserting ticket with orderID: " + orderId);
            stmt.setInt(4, orderId);
            if (ticket.getSeatID() != null) {
                stmt.setInt(5, ticket.getSeatID().getSeatID());
            } else {
                throw new SQLException("seatID không được null.");
            }
            stmt.setDouble(6, ticket.getPrice());
            stmt.executeUpdate();
            System.out.println("Ticket saved successfully with TicketID: " + ticket.getTicketID());
        }
    }

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

    public boolean isSeat(int showtimeID, int seatID) throws SQLException {
        String query = "SELECT COUNT(*) FROM Tickets WHERE showTimeID = ? AND seatID = ?";
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

    public void updateTicket(Tickets ticket) throws SQLException {
        String query = "UPDATE Tickets SET showTimeID = ?, saleDate = ?, orderID = ?, seatID = ?, price = ? WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticket.getShowTimeID().getShowTimeID());
            ps.setObject(2, ticket.getSaleDate());
            ps.setInt(3, ticket.getOrderID().getOrderID());
            if (ticket.getSeatID() != null) {
                ps.setInt(4, ticket.getSeatID().getSeatID());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setDouble(5, ticket.getPrice());
            ps.setInt(6, ticket.getTicketID());
            ps.executeUpdate();
        }
    }

    public void deleteTicket(int ticketID) throws SQLException {
        String query = "DELETE FROM Tickets WHERE ticketID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, ticketID);
            ps.executeUpdate();
        }
    }

    private Tickets createTicketFromResultSet(ResultSet rs) throws SQLException {
        Tickets ticket = new Tickets();
        ticket.setTicketID(rs.getInt("ticketID"));
        
        Showtimes showtime = new Showtimes();
        showtime.setShowTimeID(rs.getInt("showTimeID"));
        ticket.setShowTimeID(showtime);
        
        ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));
        
        Orders order = new Orders();
        order.setOrderID(rs.getInt("orderID"));
        ticket.setOrderID(order);
        
        SeatDAO seatDAO = new SeatDAO(con);
        Seats seat = seatDAO.getSeatByID(rs.getInt("seatID"));
        ticket.setSeatID(seat);
        
        ticket.setPrice(rs.getDouble("price"));
        return ticket;
    }
}