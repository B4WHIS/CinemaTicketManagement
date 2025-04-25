package dao;

import java.sql.CallableStatement;
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

    private void ensureConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng.");
        }
    }

    // Thêm một ghế mới 
    public void addSeat(Seats seat) throws SQLException {
        ensureConnection();
        String query = "INSERT INTO Seats (roomID, seatNumber) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seat.getRoomID().getRoomID());
            ps.setString(2, seat.getSeatNumber());
            ps.executeUpdate();
        }
    }

    // Lấy tất cả ghế
    public List<Seats> getAllSeats() throws SQLException {
        ensureConnection();
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
        ensureConnection();
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

    // Lấy danh sách ghế theo phòng (không kèm trạng thái)
    public List<Seats> getSeatsByRoom(Rooms room) throws SQLException {
        ensureConnection();
        List<Seats> seats = new ArrayList<>();

        // Gọi stored procedure sp_GetSeatsByRoom
        try (CallableStatement stmt = con.prepareCall("{call sp_GetSeatsByRoom(?, ?)}")) {
            stmt.setInt(1, room.getRoomID());
            stmt.setNull(2, java.sql.Types.INTEGER); // Không cung cấp ShowtimeID
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seats seat = createSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    // Lấy danh sách ghế kèm trạng thái theo suất chiếu
    public List<SeatWithStatus> getSeatsWithStatus(int showtimeID, Rooms room) throws SQLException {
        ensureConnection();
        List<SeatWithStatus> seatsWithStatus = new ArrayList<>();

        // Gọi stored procedure sp_GetSeatsByRoom với ShowtimeID
        try (CallableStatement stmt = con.prepareCall("{call sp_GetSeatsByRoom(?, ?)}")) {
            stmt.setInt(1, room.getRoomID());
            stmt.setInt(2, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seatsWithStatus.add(new SeatWithStatus(
                            rs.getInt("SeatID"),
                            rs.getInt("RoomID"),
                            rs.getString("SeatNumber"),
                            rs.getString("Status")
                    ));
                }
            }
        }
        return seatsWithStatus;
    }

    // Cập nhật ghế
    public void updateSeat(Seats seat) throws SQLException {
        ensureConnection();
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
        ensureConnection();
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

    public void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}

// Lớp SeatWithStatus để lưu thông tin ghế kèm trạng thái
class SeatWithStatus {
    private int seatID;
    private int roomID;
    private String seatNumber;
    private String status;

    public SeatWithStatus(int seatID, int roomID, String seatNumber, String status) {
        this.seatID = seatID;
        this.roomID = roomID;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public int getSeatID() {
        return seatID;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }
}