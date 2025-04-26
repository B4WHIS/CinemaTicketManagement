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
        if (seat == null || seat.getRoom() == null) {
            System.err.println("Seat hoặc Room rifle hợp lệ.");
            return false;
        }

        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seat.getSeatID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("SeatID " + seat.getSeatID() + " đã tồn tại.");
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
            System.out.println("Thêm ghế thành công - SeatID: " + seat.getSeatID() +
                    ", SeatNumber: " + seat.getSeatNumber() +
                    ", RoomID: " + seat.getRoom().getRoomID());
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm ghế - SeatID: " + seat.getSeatID());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateSeat(Seats seat) throws SQLException {
        if (seat == null || seat.getRoom() == null) {
            System.err.println("Seat hoặc Room không hợp lệ.");
            return false;
        }

        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seat.getSeatID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("SeatID " + seat.getSeatID() + " không tồn tại.");
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
            System.out.println("Cập nhật ghế thành công - SeatID: " + seat.getSeatID() +
                    ", SeatNumber: " + seat.getSeatNumber() +
                    ", RoomID: " + seat.getRoom().getRoomID());
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật ghế - SeatID: " + seat.getSeatID());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteSeat(int seatID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Seats WHERE SeatID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, seatID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("SeatID " + seatID + " không tồn tại.");
                    return false;
                }
            }
        }

        String sql = "DELETE FROM Seats WHERE SeatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seatID);
            stmt.executeUpdate();
            System.out.println("Xóa ghế thành công - SeatID: " + seatID);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa ghế - SeatID: " + seatID);
            e.printStackTrace();
            throw e;
        }
    }

    public List<Seats> getSeatsByRoom(Rooms room) throws SQLException {
        if (room == null) {
            System.err.println("Room không hợp lệ.");
            return new ArrayList<>();
        }

        ensureConnection();

        List<Seats> seats = new ArrayList<>();
        String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName, r.Capacity, r.Type AS roomType " +
                     "FROM Seats s " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.RoomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, room.getRoomID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Dữ liệu thô - SeatID: " + rs.getInt("SeatID") +
                            ", SeatNumber: " + rs.getString("SeatNumber") +
                            ", RoomID: " + rs.getInt("RoomID") +
                            ", RoomName: " + rs.getString("RoomName") +
                            ", RoomCapacity: " + rs.getInt("Capacity") +
                            ", RoomType: " + rs.getString("roomType"));
                    Seats seat = createSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }
        }
        System.out.println("Lấy " + seats.size() + " ghế cho RoomID: " + room.getRoomID());
        return seats;
    }

    public Seats getSeatByID(int seatID) throws SQLException {
        ensureConnection();

        String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName, r.Capacity, r.Type AS roomType " +
                     "FROM Seats s " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.SeatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seatID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Lấy ghế thành công - SeatID: " + rs.getInt("SeatID") +
                            ", SeatNumber: " + rs.getString("SeatNumber") +
                            ", RoomID: " + rs.getInt("RoomID") +
                            ", RoomName: " + rs.getString("RoomName"));
                    return createSeatFromResultSet(rs);
                }
            }
        }
        System.out.println("Không tìm thấy ghế với SeatID: " + seatID);
        return null;
    }

    public List<Seats> getAllSeats() throws SQLException {
        ensureConnection();

        List<Seats> seats = new ArrayList<>();
        String sql = "SELECT s.SeatID, s.SeatNumber, s.RoomID, r.RoomName, r.Capacity, r.Type AS roomType " +
                     "FROM Seats s " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Dữ liệu thô - SeatID: " + rs.getInt("SeatID") +
                        ", SeatNumber: " + rs.getString("SeatNumber") +
                        ", RoomID: " + rs.getInt("RoomID") +
                        ", RoomName: " + rs.getString("RoomName") +
                        ", RoomCapacity: " + rs.getInt("Capacity") +
                        ", RoomType: " + rs.getString("roomType"));
                Seats seat = createSeatFromResultSet(rs);
                seats.add(seat);
            }
        }
        System.out.println("Lấy tất cả " + seats.size() + " ghế.");
        return seats;
    }

    private Seats createSeatFromResultSet(ResultSet rs) throws SQLException {
        int seatID = rs.getInt("SeatID");
        String seatNumber = rs.getString("SeatNumber");
        int roomID = rs.getInt("RoomID");
        String roomName = rs.getString("RoomName");
        int capacity = rs.getInt("Capacity");
        String roomType = rs.getString("roomType");

        Rooms room = new Rooms(
                roomID,
                roomName != null ? roomName : "Phòng không xác định",
                capacity,
                roomType != null ? roomType : "Không xác định"
        );
        return new Seats(seatID, seatNumber, room);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Đóng kết nối cơ sở dữ liệu thành công.");
        }
    }
}