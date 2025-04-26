package dao;

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

    // Kiểm tra xem ghế có được đặt hay không
    public boolean isSeatBooked(int seatID, int showtimeID) throws SQLException {
        ensureConnection();

        // B: Kiểm tra xem có bản ghi trong SeatStatus không
        String checkSql = "SELECT Status FROM SeatStatus WHERE ShowtimeID = ? AND SeatID = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            checkStmt.setInt(2, seatID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("Status");
                    return "Booked".equalsIgnoreCase(status);
                } else {
                    // B: Nếu không có bản ghi, khởi tạo trạng thái Available
                    String insertSql = "INSERT INTO SeatStatus (ShowtimeID, SeatID, Status) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, showtimeID);
                        insertStmt.setInt(2, seatID);
                        insertStmt.setString(3, "Available");
                        insertStmt.executeUpdate();
                    }
                    return false; // Ghế mới khởi tạo là Available
                }
            }
        }
    }

    // Cập nhật trạng thái ghế
    public boolean updateSeatStatus(int showtimeID, int seatID, String status) throws SQLException {
        ensureConnection();
        // B: Kiểm tra xem bản ghi có tồn tại không
        String checkSql = "SELECT Status FROM SeatStatus WHERE ShowtimeID = ? AND SeatID = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            checkStmt.setInt(2, seatID);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String currentStatus = rs.getString("Status");
                if ("Booked".equalsIgnoreCase(currentStatus) && "Booked".equalsIgnoreCase(status)) {
                    return false; // Ghế đã được đặt
                }
                // B: Cập nhật trạng thái nếu bản ghi tồn tại
                String updateSql = "UPDATE SeatStatus SET Status = ? WHERE ShowtimeID = ? AND SeatID = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                    updateStmt.setString(1, status);
                    updateStmt.setInt(2, showtimeID);
                    updateStmt.setInt(3, seatID);
                    updateStmt.executeUpdate();
                    return true;
                }
            } else {
                // B: Thêm bản ghi mới nếu không tồn tại
                String insertSql = "INSERT INTO SeatStatus (ShowtimeID, SeatID, Status) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, showtimeID);
                    insertStmt.setInt(2, seatID);
                    insertStmt.setString(3, status);
                    insertStmt.executeUpdate();
                    return true;
                }
            }
        }
    }

    // Đặt ghế
    public boolean bookSeat(int showtimeID, int seatID) throws SQLException {
        return updateSeatStatus(showtimeID, seatID, "Booked");
    }

    // Hủy đặt ghế
    public boolean cancelSeatBooking(int showtimeID, int seatID) throws SQLException {
        return updateSeatStatus(showtimeID, seatID, "Available");
    }

    // Xóa trạng thái ghế của một suất chiếu
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