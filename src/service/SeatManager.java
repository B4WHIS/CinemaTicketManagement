package service;



import java.sql.SQLException;
import java.util.List;

import dao.SeatDAO;
import model.Rooms;
import model.Seats;

public class SeatManager {
    private SeatDAO seatDAO;

    // Constructor
    public SeatManager() {
        this.seatDAO = new SeatDAO();
    }

    // Lấy tất cả ghế (Read - danh sách)
//    public List<Seats> getAllSeats() throws SQLException {
//        return seatDAO.getAllSeats();
//    }

    // Lấy ghế theo ID (Read - chi tiết)
    public Seats getSeat(int seatID) throws SQLException {
        Seats seat = seatDAO.getSeatByID(seatID);
        if (seat == null) {
            throw new SQLException("Không tìm thấy ghế với ID: " + seatID);
        }
        return seat;
    }

    // Lấy danh sách ghế theo phòng (Read - theo phòng)
    public List<Seats> getSeatsByRoom(Rooms roomID) throws SQLException {
        return seatDAO.getSeatsByRoom(roomID);
    }

    // Cập nhật ghế (Update)
    public void updateSeat(Seats seat) throws SQLException {
        seatDAO.updateSeat(seat);
    }

    // Xóa ghế (Delete)
    public void deleteSeat(int seatID) throws SQLException {
        seatDAO.deleteSeat(seatID);
    }
}