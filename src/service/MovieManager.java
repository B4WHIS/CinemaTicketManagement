package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import dao.Movies_DAO;
import model.Movies;

public class MovieManager {
    private Movies_DAO movieDAO;

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
}

