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
    private Connection connection;

    public SeatDAO(Connection connection) {
        this.connection = connection;
    }

    public SeatDAO() {
        this.connection = connectDB.getConnection();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = connectDB.getConnection();
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
            }
        }
    }

    public boolean addSeat(Seats seat) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seat.getSeatID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }

        String sql = "INSERT INTO Seats (SeatID, SeatNumber, RoomID) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seat.getSeatID());
            stmt.setString(2, seat.getSeatNumber());
            stmt.setInt(3, seat.getRoom().getRoomID());
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean updateSeat(Seats seat) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seat.getSeatID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false;
                }
            }
        }

        String sql = "UPDATE Seats SET SeatNumber = ?, RoomID = ? WHERE SeatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, seat.getSeatNumber());
            stmt.setInt(2, seat.getRoom().getRoomID());
            stmt.setInt(3, seat.getSeatID());
            stmt.executeUpdate();
            return true;
        }
    }

    public boolean deleteSeat(int seatID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seatID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false;
                }
            }
        }

        String sql = "DELETE FROM Seats WHERE SeatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seatID);
            stmt.executeUpdate();
            return true;
        }
    }

    public List<Seats> getSeatsByRoom(Rooms room) throws SQLException {
        ensureConnection();

        List<Seats> seats = new ArrayList<>();
        String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName, r.Capacity " +
                     "FROM Seats s " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.RoomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, room.getRoomID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seats seat = createSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    public Seats getSeatByID(int seatID) throws SQLException {
        ensureConnection();

        String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName, r.Capacity " +
                     "FROM Seats s " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.SeatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seatID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createSeatFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private Seats createSeatFromResultSet(ResultSet rs) throws SQLException {
        int seatID = rs.getInt("SeatID");
        String seatNumber = rs.getString("SeatNumber");
        int roomID = rs.getInt("RoomID");
        String roomName = rs.getString("RoomName");
        int capacity = rs.getInt("Capacity");

        // Sử dụng constructor đầy đủ của Rooms
        Rooms room = new Rooms(roomID, roomName != null ? roomName : "Phòng không xác định", capacity);
        return new Seats(seatID, seatNumber, room);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}