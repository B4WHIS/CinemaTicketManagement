package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Movies_DAO;

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

    public boolean addMovie(Movies movie) {
        try {
            return movieDAO.addMovie(movie);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateMovie(Movies movie) {
        try {
            return movieDAO.updateMovie(movie);
        } catch (SQLException e) {
            return false;
        }
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

