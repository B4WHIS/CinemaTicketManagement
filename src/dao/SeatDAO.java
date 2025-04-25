package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Rooms;
import model.Seats;

public class SeatDAO {
    private final Connection con;

    public SeatDAO() {
        this.con = connectDB.getConnection();
    }

    public SeatDAO(Connection conn) {
        this.con = conn;
    }

    // Thêm một ghế mới 
    public void addSeat(Seats seat) throws SQLException {
        String query = "INSERT INTO Seats (roomID, seatNumber) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seat.getRoomID().getRoomID());
            ps.setString(2, seat.getSeatNumber());
            ps.executeUpdate();
        }
    }

    // Lấy tất cả ghế
    public List<Seats> getAllSeats() throws SQLException {
        List<Seats> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Seats seat = createSeatFromResultSet(rs);
                seats.add(seat);
            }
        }
        return seats;
    }

    // Lấy ghế theo ID
    public Seats getSeatById(int seatID) throws SQLException {
        String query = "SELECT * FROM Seats WHERE seatID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seatID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createSeatFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Lấy danh sách ghế theo phòng
    public List<Seats> getSeatsByRoom(Rooms room) throws SQLException {
        List<Seats> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats WHERE roomID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, room.getRoomID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(createSeatFromResultSet(rs));
                }
            }
        }
        return seats;
    }

    // Cập nhật ghế
    public void updateSeat(Seats seat) throws SQLException {
        String query = "UPDATE Seats SET roomID = ?, seatNumber = ? WHERE seatID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seat.getRoomID().getRoomID());
            ps.setString(2, seat.getSeatNumber());
            ps.setInt(3, seat.getSeatID());
            ps.executeUpdate();
        }
    }

    // Xóa ghế 
    public void deleteSeat(int seatID) throws SQLException {
        String query = "DELETE FROM Seats WHERE seatID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seatID);
            ps.executeUpdate();
        }
    }

    // Helper method to create Seat object from ResultSet
    private Seats createSeatFromResultSet(ResultSet rs) throws SQLException {
        Seats seat = new Seats();
        seat.setSeatID(rs.getInt("seatID"));
        Rooms room = new Rooms();
        room.setRoomID(rs.getInt("roomID"));
        seat.setRoomID(room);
        seat.setSeatNumber(rs.getString("seatNumber"));
        return seat;
    }
}