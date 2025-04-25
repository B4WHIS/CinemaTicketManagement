package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Movies;
import model.Showtimes;

public class Showtimes_DAO {
    private Connection connection;

    public Showtimes_DAO(Connection connection) {
        this.connection = connection;
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage " +
                    "FROM Showtimes s JOIN Movies m ON s.MovieID = m.MovieID " +
                    "WHERE m.MovieID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movies movie = new Movies(
                            rs.getInt("MovieID"),
                            rs.getString("movieTitle"),
                            rs.getString("movieGenre"),
                            rs.getInt("movieDuration"),
                            rs.getString("movieDirector"),
                            rs.getDate("movieReleaseDate"),
                            rs.getString("movieImage")
                    );
                    Showtimes showtime = new Showtimes(
                            rs.getInt("ShowtimeID"),
                            rs.getInt("RoomID"),
                            movie,
                            rs.getDate("DateTime"),
                            rs.getTime("StartTime"),
                            rs.getDouble("Price")
                    );
                    showtimes.add(showtime);
                }
            }
        }
        return showtimes;
    }

    public Showtimes getShowtimeByID(int showtimeID) throws SQLException {
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage " +
                    "FROM Showtimes s JOIN Movies m ON s.MovieID = m.MovieID " +
                    "WHERE s.ShowtimeID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Movies movie = new Movies(
                            rs.getInt("MovieID"),
                            rs.getString("movieTitle"),
                            rs.getString("movieGenre"),
                            rs.getInt("movieDuration"),
                            rs.getString("movieDirector"),
                            rs.getDate("movieReleaseDate"),
                            rs.getString("movieImage")
                    );
                    return new Showtimes(
                            rs.getInt("ShowtimeID"),
                            rs.getInt("RoomID"),
                            movie,
                            rs.getDate("DateTime"),
                            rs.getTime("StartTime"),
                            rs.getDouble("Price")
                    );
                }
            }
        }
        return null;
    }

    public List<Showtimes> getAllShowtimes() throws SQLException {
        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage " +
                    "FROM Showtimes s JOIN Movies m ON s.MovieID = m.MovieID";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Movies movie = new Movies(
                        rs.getInt("MovieID"),
                        rs.getString("movieTitle"),
                        rs.getString("movieGenre"),
                        rs.getInt("movieDuration"),
                        rs.getString("movieDirector"),
                        rs.getDate("movieReleaseDate"),
                        rs.getString("movieImage")
                );
                Showtimes showtime = new Showtimes(
                        rs.getInt("ShowtimeID"),
                        rs.getInt("RoomID"),
                        movie,
                        rs.getDate("DateTime"),
                        rs.getTime("StartTime"),
                        rs.getDouble("Price")
                );
                showtimes.add(showtime);
            }
        }
        return showtimes;
    }
}