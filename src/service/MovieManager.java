package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Movies;

public class MovieManager {
    private Connection connection;

    public MovieManager(Connection connection) {
        this.connection = connection;
    }

    public List<Movies> getAllMovies() {
        List<Movies> movies = new ArrayList<>();
        String query = "{CALL sp_GetAllMovies}";

        try (CallableStatement stmt = connection.prepareCall(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Movies movie = new Movies();
                movie.setMovieID(rs.getInt("movieID"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setDuration(rs.getInt("duration"));
                movie.setReleaseDate(rs.getDate("releaseDate"));
                movie.setDirector(rs.getString("director"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}