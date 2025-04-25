package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Showtimes_DAO;
import dbs.connectDB;
import model.Showtimes;

public class ShowTimeManager {
    private Showtimes_DAO showtimeDAO;
    private Connection connection;

    public ShowTimeManager() throws SQLException {
        this.connection = connectDB.getConnection();
        if (this.connection == null) {
            throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
        }
        this.showtimeDAO = new Showtimes_DAO(this.connection);
    }

    public boolean addShowtime(Showtimes showtime) throws SQLException {
        return showtimeDAO.addShowtime(showtime);
    }

    public boolean updateShowtime(Showtimes showtime) throws SQLException {
        return showtimeDAO.updateShowtime(showtime);
    }

    public boolean deleteShowtime(int id) throws SQLException {
        return showtimeDAO.deleteShowtime(id);
    }

    public Showtimes getShowtimeByID(int id) throws SQLException {
        return showtimeDAO.getShowtimeByID(id);
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        return showtimeDAO.getShowtimesByMovie(movieID);
    }

    public List<Showtimes> getAllShowtimes() throws SQLException {
        return showtimeDAO.getAllShowtimes();
    }

    // Đóng kết nối
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        showtimeDAO.closeConnection();
    }
}