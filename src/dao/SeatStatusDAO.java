package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.SeatStatus;
import model.Seats;

public class SeatStatusDAO {
    private final Connection con;

    public SeatStatusDAO() {
        this.con = connectDB.getConnection();
    }

    public SeatStatusDAO(Connection conn) {
        this.con = conn;
    }

    private void ensureConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng.");
        }
    }

    // Khởi tạo trạng thái ghế cho một suất chiếu
    public void initializeSeatsForShowtime(int showtimeID, List<Seats> seats) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO SeatStatus (ShowtimeID, SeatID, Status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (Seats seat : seats) {
                stmt.setInt(1, showtimeID);
                stmt.setInt(2, seat.getSeatID());
                stmt.setString(3, "Available");
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    // Lấy danh sách trạng thái ghế theo suất chiếu
    public List<SeatStatus> getSeatStatusesByShowtime(int showtimeID) throws SQLException {
        ensureConnection();
        List<SeatStatus> seatStatuses = new ArrayList<>();
        String sql = "SELECT * FROM SeatStatus WHERE ShowtimeID = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SeatStatus seatStatus = new SeatStatus();
                    seatStatus.setReservationID(rs.getInt("ReservationID"));
                    seatStatus.setShowtimeID(rs.getInt("ShowtimeID"));
                    seatStatus.setSeatID(rs.getInt("SeatID"));
                    seatStatus.setStatus(rs.getString("Status"));
                    seatStatuses.add(seatStatus);
                }
            }
        }
        return seatStatuses;
    }

    // Kiểm tra xem ghế có được đặt hay không (dùng sp_IsSeatBooked)
    public boolean isSeatBooked(int seatID, int showtimeID) throws SQLException {
        ensureConnection();

        try (CallableStatement stmt = con.prepareCall("{call sp_IsSeatBooked(?, ?)}")) {
            stmt.setInt(1, seatID);
            stmt.setInt(2, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IsBooked") == 1;
                }
            }
        }
        return false; // Mặc định là chưa đặt nếu không có kết quả
    }

    // Cập nhật trạng thái ghế (dùng sp_UpdateSeatStatus)
    public boolean updateSeatStatus(int showtimeID, int seatID, String status) throws SQLException {
        ensureConnection();

        try (CallableStatement stmt = con.prepareCall("{call sp_UpdateSeatStatus(?, ?, ?)}")) {
            stmt.setInt(1, seatID);
            stmt.setInt(2, showtimeID);
            stmt.setString(3, status);
            stmt.execute();

            // Kiểm tra xem có bản ghi được cập nhật hay không
            try (ResultSet rs = stmt.getResultSet()) {
                return rs != null && rs.next(); // Trả về true nếu có kết quả
            }
        }
    }

    // Đặt ghế (gộp từ SeatStatusManager)
    public boolean bookSeat(int showtimeID, int seatID) throws SQLException {
        ensureConnection();
        if (!isSeatBooked(seatID, showtimeID)) {
            return updateSeatStatus(showtimeID, seatID, "Booked");
        }
        return false;
    }

    // Hủy đặt ghế (gộp từ SeatStatusManager)
    public boolean cancelSeatBooking(int showtimeID, int seatID) throws SQLException {
        ensureConnection();
        return updateSeatStatus(showtimeID, seatID, "Available");
    }

    // Xóa trạng thái ghế của một suất chiếu (tùy chọn)
    public void deleteSeatStatusesByShowtime(int showtimeID) throws SQLException {
        ensureConnection();
        String sql = "DELETE FROM SeatStatus WHERE ShowtimeID = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
        }
    }

    public void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}