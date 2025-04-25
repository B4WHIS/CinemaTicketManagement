package service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import dao.Showtimes_DAO;
import dbs.connectDB;
import model.Showtimes;
public class ShowTimeManager {
    private static Showtimes_DAO showtimeDAO;

    public ShowTimeManager() throws SQLException {
        Connection conn = connectDB.getConnection();
		this.showtimeDAO = new Showtimes_DAO(conn);
    }
    public static Showtimes getShowtimeByID(int id) throws SQLException {
        return showtimeDAO.getShowtimeByID(id);
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        return showtimeDAO.getShowtimesByMovie(movieID);
    }

	public List<Showtimes> getAllShowtimes() throws SQLException {
		
		return showtimeDAO.getAllShowtimes();
	}

  
}
