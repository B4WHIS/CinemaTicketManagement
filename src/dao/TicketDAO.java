package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.READER;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import model.Tickets;

public class TicketDAO {
	
	//Thêm một vé mới
	public void saveTicket(Tickets ticket) throws SQLException {
		String query = "INSERT INTO Tickets (showTimeID, saleDate, orderID, price)"
				+ "VALUES (?, ?, ?, ?)";
		
		try(Connection con = SQLServerConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(query)){
			ps.setInt(1, ticket.getShowTimeID());
			ps.setObject(2, ticket.getSaleDate());
			ps.setInt(3, ticket.getOrderID());
			ps.setDouble(4, ticket.getPrice());
			//trả về số dòng bị ảnh hưởng
			ps.executeUpdate();
		}
	}
		//Lấy tất cả vé (Read - danh sách)
		public List<Tickets> getAllTickets() throws SQLException{
			List<Tickets> tickets = new ArrayList<>();
			String query = "SELECT * FROM Tickets";
			try (Connection con = SQLServerConnection.getConnection();
					PreparedStatement ps = con.prepareStatement(query);
					ResultSet rs = ps.executeQuery()){
				while (rs.next()) {
					Tickets ticket = new Tickets();
					ticket.setTicketID(rs.getInt("ticketID"));
					ticket.setShowTimeID(rs.getInt(1));
					ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));
					ticket.setOrderID(rs.getInt(3));
					ticket.setPrice(rs.getDouble(4));
					tickets.add(ticket);
				}
				
			}
		}
		//Lấy vé theo ID (Read - chi tiết)
		public Tickets getTicketById(int ticketID) throws SQLException{
			String query = "SELECT * FORM Tickets WHERE ticketID = ?";
			try(Connection con = SQLServerConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(query)){
					ps.setInt(1, ticketID);
					try(ResultSet rs = ps.executeQuery()){
						if(rs.next()) {
							Tickets ticket = new Tickets();
							ticket.setTicketID(rs.getInt("ticketID"));
							ticket.setShowTimeID(rs.getInt("showtimeID"));
							ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));
							ticket.setOrderID(rs.getInt("orderID"));
							ticket.setPrice(rs.getDouble("price"));
							return ticket;
						}
					}
			}
			return null;
		}
		//Lấy  danh sách vé theo đơn hàng (orderID)
		public List<Tickets> getTicketByOrder(int orderID) throws SQLException{
			List<Tickets> tickets = new ArrayList<>();
			String query = "SELECT * FROM Tickets WHERE orderID = ?";
			try(Connection con = SQLServerConnection.getConnection();
					PreparedStatement ps = con.prepareStatement(query)){
				ps.setInt(1, orderID);
				try(ResultSet rs = ps.executeQuery()){
					while(rs.next()) {
						Tickets ticket = new Tickets();
						ticket.setTicketID(rs.getInt("ticketID"));
						ticket.setShowTimeID(rs.getInt("showtimeID"));
						ticket.setSaleDate(rs.getObject("saleDate", LocalDateTime.class));
						ticket.setOrderID(rs.getInt("orderID"));
						ticket.setPrice(rs.getDouble("price"));
						tickets.add(ticket);
					}
				}
			}
			
			
		}
		//Kiểm tra trạng thái ghế
		public boolean isSeat(int showtimeID, int seatID) throws SQLException {
			String query = "SELECT COUNT (*) FROM Tickets WHERE showtimeID = ? AND seatID = ? ";
			try (Connection con = SQLServerConnection.getConnection();
					PreparedStatement ps = con.prepareStatement(query)){
						ps.setInt(1, showtimeID);
						ps.setInt(2, seatID);
						ResultSet rs = ps.executeQuery();
						if(rs.next()) {
							return rs.getInt(1) > 0;
						}
						return false;
				
			}
		}
		//Cập nhật vé 
		public void updateTicket(Tickets ticket) throws SQLException{
			String query = "UPDATE Tickets SET showtimeID = ?, saleDate = ?, orderID = ?, price = ? WHERE ticketID = ?";
			try (Connection con = SQLServerConnection.getConnection();
					PreparedStatement ps = con.prepareStatement(query)){
				ps.setInt(1, ticket.getShowTimeID());
				ps.setObject(2, ticket.getSaleDate());
				ps.setInt(3, ticket.getOrderID());
				ps.setDouble(4, ticket.getPrice());
				ps.setInt(5, ticket.getTicketID());
				ps.executeUpdate();
			}
			
		}
		//Xóa vé
		public void deleteTicket(int ticketID) throws SQLException{
			String query = "DELETE FROM Tickets WHERE ticketID = ?";
			try(Connection con = SQLServerConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(query)){
				ps.setInt(1, ticketID);
				ps.executeUpdate();
			}
		}
}
