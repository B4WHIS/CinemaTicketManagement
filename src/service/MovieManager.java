package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Movies_DAO;
import model.Movies;

public class MovieManager {
    private Movies_DAO movieDao;
    private Connection connection;

    public MovieManager(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection cannot be null");
        }
        this.connection = connection;
        this.movieDao = new Movies_DAO(connection);
    }

    public boolean addMovie(Movies movie) throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.addMovie(movie);
    }

    public boolean updateMovie(Movies movie) throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.updateMovie(movie);
    }

    public boolean deleteMovie(int movieID) throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.deleteMovie(movieID);
    }

    public Movies getMovieByID(int id) throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.getMovieByID(id);
    }

    public List<Movies> getAllMovies() throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.getAllMovies();
    }

    public List<Movies> searchMoviesByTitle(String keyword) throws SQLException {
        if (movieDao == null) {
            throw new SQLException("MovieManager not properly initialized: movieDao is null");
        }
        return movieDao.searchMoviesByTitle(keyword);
    }

    public int getNextMovieId() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is null");
        }
        String sql = "SELECT MAX(MovieID) + 1 FROM Movies";
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1; // Nếu bảng rỗng, bắt đầu từ 1
        }
    }
}