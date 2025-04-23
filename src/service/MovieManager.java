package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import dao.Movies_DAO;
<<<<<<< HEAD
=======
import dbs.connectDB;
>>>>>>> phim_phong_lichChieu
import model.Movies;

public class MovieManager {
	private Movies_DAO movieDao;
	public MovieManager() {
		try {
			Connection con = connectDB.getConnection();
			this.movieDao = new Movies_DAO(con);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public boolean addMovie(Movies movie) {
		return movieDao.addMovie(movie);
	}
	public boolean updateMovie(Movies movie) {
		 return movieDao.updateMovie(movie);
	}
	public boolean deleteMovie(int movieID) throws SQLException {
		 return movieDao.deleteMovie(movieID);
	}
	 public Movies getMovieByID(int id) {
	        return movieDao.getMovieByID(id);
	    }

<<<<<<< HEAD
    public MovieManager() {
        try {
            Connection conn = SQLServerConnection.getConnection();
            this.movieDAO = new Movies_DAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addMovie(Movies movie) throws SQLException {
        return movieDAO.addMovie(movie);
    }

    public boolean updateMovie(Movies movie) throws SQLException {
        return movieDAO.updateMovie(movie);
    }

    public boolean deleteMovie(int movieID) {
        try {
            return movieDAO.deleteMovie(movieID);
        } catch (SQLException e) {
            return false;
        }
    }

    public Movies getMovieByID(int id) {
        return movieDAO.getMovieByID(id);
    }

    public List<Movies> getAllMovies() {
        return movieDAO.getAllMovies();
    }
=======
	    public List<Movies> getAllMovies() {
	        return movieDao.getAllMovies();
	    }
>>>>>>> phim_phong_lichChieu
}
