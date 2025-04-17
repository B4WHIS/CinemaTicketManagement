package service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Showtimes_DAO;
public class ShowTimeManager {
    private Showtimes_DAO showtimeDAO;

    public ShowTimeManager() {
        try {
            Connection conn = SQLServerConnection.getConnection();
            this.showtimeDAO = new Showtimes_DAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addShowtime(Showtimes showtime) {
        return showtimeDAO.addShowtime(showtime);
    }

    public boolean updateShowtime(Showtimes showtime) {
        return showtimeDAO.updateShowtime(showtime);
    }

    public boolean deleteShowtime(int id) {
        return showtimeDAO.deleteShowtime(id);
    }

    public Showtimes getShowtimeByID(int id) {
        return showtimeDAO.getShowtimeByID(id);
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) {
        return showtimeDAO.getShowtimesByMovie(movieID);
    }

  
}

