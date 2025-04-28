package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import dbs.connectDB;
import model.Tickets;

public class TicketDAO {
    private final Connection con;

    // Constructor mặc định: lấy connection từ connectDB
    public TicketDAO() {
        this.con = connectDB.getConnection();
    }

    // Constructor với connection truyền vào
    public TicketDAO(Connection conn) {
        this.con = conn;
    }

    // Lưu ticket
    public void saveTicket(Tickets ticket) throws SQLException {
        // Lấy TicketID mới: +1 từ TicketID lớn nhất
        String getMaxIdSql = "SELECT MAX(TicketID) FROM Tickets";
        int newTicketId = 1;
        try (PreparedStatement maxStmt = con.prepareStatement(getMaxIdSql)) {
            try (ResultSet rs = maxStmt.executeQuery()) {
                if (rs.next()) {
                    newTicketId = rs.getInt(1) + 1;
                }
            }
        }

        ticket.setTicketID(newTicketId);

        // Thêm ticket vào database
        String query = "{call sp_InsertTicket(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = con.prepareCall(query)) {
            stmt.setInt(1, ticket.getTicketID());
            stmt.setInt(2, ticket.getShowTimeID().getShowTimeID());
            stmt.setTimestamp(3, Timestamp.valueOf(ticket.getSaleDate()));
            stmt.setInt(4, ticket.getOrderID().getOrderID());
            
            if (ticket.getSeatID() != null) {
                stmt.setInt(5, ticket.getSeatID().getSeatID());
            } else {
                throw new SQLException("SeatID không được null.");
            }
            
            stmt.setDouble(6, ticket.getPrice());
            stmt.execute();
            System.out.println("Ticket saved successfully with TicketID: " + ticket.getTicketID());
        }
    }
}
