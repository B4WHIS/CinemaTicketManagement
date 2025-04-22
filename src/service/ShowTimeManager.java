package service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Showtimes_DAO;
import dbs.connectDB;
import model.Showtimes;
public class ShowTimeManager {
    private static Showtimes_DAO showtimeDAO;

    public ShowTimeManager() throws SQLException {
        Connection conn = connectDB.getConnection();
		this.showtimeDAO = new Showtimes_DAO(conn);
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

    public static Showtimes getShowtimeByID(int id) {
        return showtimeDAO.getShowtimeByID(id);
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) {
        return showtimeDAO.getShowtimesByMovie(movieID);
    }

	public List<Showtimes> getAllShowtimes() {
		
		return showtimeDAO.getAllShowtimes();
	}

  
}
