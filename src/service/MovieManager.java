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
    private Connection connection;
	private Movies_DAO movieDao;
	

    public MovieManager() {
        this.connection = connection;
    }

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

	public void addMovie(Movies movie) {
		// TODO Auto-generated method stub
		
	}
}