package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import model.Rooms;
import model.Seats;

public class SeatDAO {

    // Thêm một ghế mới 
    public void addSeat(Seats seat) throws SQLException {
        String query = "INSERT INTO Seats (roomID, seatNumber) VALUES (?, ?)";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seat.getRoomID().getRoomID()); // Sửa ở đây
            ps.setString(2, seat.getSeatNumber());
            ps.executeUpdate();
        }
    }

    // Lấy tất cả ghế
    public List<Seats> getAllSeats() throws SQLException {
        List<Seats> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Seats seat = new Seats();
                seat.setSeatID(rs.getInt("seatID"));
                Rooms room = new Rooms();
                room.setRoomID(rs.getInt("roomID"));
                seat.setRoomID(room);
                seat.setSeatNumber(rs.getString("seatNumber"));
                seats.add(seat);
            }
        }
        return seats;
    }

    // Lấy ghế theo ID
    public Seats getSeatById(int seatID) throws SQLException {
        String query = "SELECT * FROM Seats WHERE seatID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seatID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Seats seat = new Seats();
                    seat.setSeatID(rs.getInt("seatID"));
                    Rooms room = new Rooms();
                    room.setRoomID(rs.getInt("roomID"));
                    seat.setRoomID(room);
                    seat.setSeatNumber(rs.getString("seatNumber"));
                    return seat;
                }
            }
        }
        return null;
    }

    // Lấy danh sách ghế theo phòng
    public List<Seats> getSeatsByRoom(Rooms room) throws SQLException {
        List<Seats> seats = new ArrayList<>();
        String query = "SELECT * FROM Seats WHERE roomID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, room.getRoomID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Seats seat = new Seats();
                    seat.setSeatID(rs.getInt("seatID"));
                    Rooms roomObj = new Rooms();
                    roomObj.setRoomID(rs.getInt("roomID"));
                    seat.setRoomID(roomObj);
                    seat.setSeatNumber(rs.getString("seatNumber"));
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    // Cập nhật ghế
    public void updateSeat(Seats seat) throws SQLException {
        String query = "UPDATE Seats SET roomID = ?, seatNumber = ? WHERE seatID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seat.getRoomID().getRoomID());
            ps.setString(2, seat.getSeatNumber());
            ps.setInt(3, seat.getSeatID());
            ps.executeUpdate();
        }
    }

    // Xóa ghế 
    public void deleteSeat(int seatID) throws SQLException {
        String query = "DELETE FROM Seats WHERE seatID = ?";
        try (Connection con = SQLServerConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seatID);
            ps.executeUpdate();
        }
    }
}