package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import dao.Movies_DAO;
import dbs.connectDB;
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

    

	    public List<Movies> getAllMovies() {
	        return movieDao.getAllMovies();
	    }
}
