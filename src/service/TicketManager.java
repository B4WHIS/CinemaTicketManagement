package service;

import java.sql.SQLException;
import java.util.List;

import dao.TicketDAO;
import model.Tickets;

public class TicketManager {
    private TicketDAO ticketDAO;

    // Constructor
    public TicketManager() {
        this.ticketDAO = new TicketDAO();
    }

    // Thêm vé mới (Create)
    public void addTicket(Tickets ticket) throws SQLException {
        // Kiểm tra logic nghiệp vụ
        if (ticket.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá vé phải lớn hơn 0");
        }
        ticketDAO.saveTicket(ticket);
    }

    // Lấy tất cả vé (Read - danh sách)
    public List<Tickets> getAllTickets() throws SQLException {
        return ticketDAO.getAllTickets();
    }

    // Lấy vé theo ID (Read - chi tiết)
    public Tickets getTicket(int ticketID) throws SQLException {
        Tickets ticket = ticketDAO.getTicketById(ticketID);
        if (ticket == null) {
            throw new SQLException("Không tìm thấy vé với ID: " + ticketID);
        }
        return ticket;
    }

    // Lấy danh sách vé theo đơn hàng (Read - theo đơn hàng)
    public List<Tickets> getTicketsByOrder(int orderID) throws SQLException {
        return ticketDAO.getTicketByOrder(orderID);
    }
}