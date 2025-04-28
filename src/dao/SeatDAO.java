package dao;

import dbs.connectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Rooms;   
import model.Seats;   

public class SeatDAO {
    private Connection connection;

    public SeatDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor mặc định connectDB
    public SeatDAO() {
        this.connection = connectDB.getConnection();
    }

    //Kiểm tra kết nốinối
    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = connectDB.getConnection();
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
            }
        }
    }

    // Lấy danh sách các ghế theo phòng
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
                    Seats seat = createSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }
        }
        System.out.println("Lấy " + seats.size() + " ghế cho RoomID: " + room.getRoomID());
        return seats;
    }

    //TTạo đối tượng Seats từ ResultSet
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

    // Đóng kết nối CSDL
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Đóng kết nối cơ sở dữ liệu thành công.");
        }
    }
}
