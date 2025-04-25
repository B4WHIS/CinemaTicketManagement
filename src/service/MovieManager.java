package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Movies_DAO;
import dbs.connectDB;
import model.Movies;

public class MovieManager {
	private Movies_DAO movieDao;
	public MovieManager(Connection connection) {
		try {
			Connection con = connectDB.getConnection();
			this.movieDao = new Movies_DAO(con);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		/////dsfhihds
	}
	public boolean addMovie(Movies movie) throws SQLException {
		return movieDao.addMovie(movie);
	}
	public boolean updateMovie(Movies movie) throws SQLException {
		 return movieDao.updateMovie(movie);
	}
	public boolean deleteMovie(int movieID) throws SQLException {
		 return movieDao.deleteMovie(movieID);
	}
	 public Movies getMovieByID(int id) throws SQLException {
	        return movieDao.getMovieByID(id);
	    }

	    public List<Movies> getAllMovies() throws SQLException {
	        return movieDao.getAllMovies();
	    }
	    public List<Movies> searchMoviesByTitle(String keyword) throws SQLException {
	        return movieDao.searchMoviesByTitle(keyword);
	    }

}