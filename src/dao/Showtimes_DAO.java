package dao;

import dbs.connectDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Movies;
import model.Rooms;
import model.Showtimes;

public class Showtimes_DAO {
    private Connection connection;

    public Showtimes_DAO(Connection connection) {
        this.connection = connection;
    }

    public Showtimes_DAO() {
        this.connection = connectDB.getConnection();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = connectDB.getConnection();
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
            }
        }
    }

    public boolean addShowtime(Showtimes showtime) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }

        String sql = "INSERT INTO Showtimes (ShowtimeID, MovieID, RoomID, DateTime, StartTime, Price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getShowTimeID());
            stmt.setInt(2, showtime.getMovie().getMovieID());
            stmt.setInt(3, showtime.getRoom().getRoomID());
            stmt.setDate(4, showtime.getdateTime());
            stmt.setTime(5, showtime.getStartTime());
            stmt.setBigDecimal(6, showtime.getPrice());

            stmt.executeUpdate();
            return true;
        }
    }

    public boolean updateShowtime(Showtimes showtime) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false;
                }
            }
        }

        String sql = "UPDATE Showtimes SET MovieID=?, RoomID=?, DateTime=?, StartTime=?, Price=? WHERE ShowtimeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getMovie().getMovieID());
            stmt.setInt(2, showtime.getRoom().getRoomID());
            stmt.setDate(3, showtime.getdateTime());
            stmt.setTime(4, showtime.getStartTime());
            stmt.setBigDecimal(5, showtime.getPrice());
            stmt.setInt(6, showtime.getShowTimeID());

            stmt.executeUpdate();
            return true;
        }
    }

    public boolean deleteShowtime(int showtimeID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false;
                }
            }
        }

        String sql = "DELETE FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
            return true;
        }
    }

    public Showtimes getShowtimeByID(int showtimeID) throws SQLException {
        ensureConnection();

        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity " +
                    "FROM Showtimes s " +
                    "JOIN Movies m ON s.MovieID = m.MovieID " +
                    "JOIN Rooms r ON s.RoomID = r.RoomID " +
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
                    Rooms room = new Rooms(
                            rs.getInt("roomID"),
                            rs.getString("roomName"),
                            rs.getInt("roomCapacity")
                    );
                    return new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            rs.getTime("StartTime"),
                            rs.getDate("DateTime"),
                            rs.getBigDecimal("Price")
                    );
                }
            }
        }
        return null;
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        ensureConnection();

        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity " +
                    "FROM Showtimes s " +
                    "JOIN Movies m ON s.MovieID = m.MovieID " +
                    "JOIN Rooms r ON s.RoomID = r.RoomID " +
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
                    Rooms room = new Rooms(
                            rs.getInt("roomID"),
                            rs.getString("roomName"),
                            rs.getInt("roomCapacity")
                    );
                    BigDecimal price = rs.getBigDecimal("Price");
                    java.sql.Time startTime = rs.getTime("StartTime");
                    java.sql.Date dateTime = rs.getDate("DateTime");

                    // Log dữ liệu gốc từ cơ sở dữ liệu
                    System.out.println("ShowtimeID: " + rs.getInt("ShowtimeID") +
                            ", StartTime: " + startTime +
                            ", DateTime: " + dateTime +
                            ", Price: " + price);

                    Showtimes showtime = new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            startTime,
                            dateTime,
                            price
                    );
                    showtimes.add(showtime);
                }
            }
        }
        return showtimes;
    }

    public List<Showtimes> getAllShowtimes() throws SQLException {
        ensureConnection();

        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity " +
                    "FROM Showtimes s " +
                    "JOIN Movies m ON s.MovieID = m.MovieID " +
                    "JOIN Rooms r ON s.RoomID = r.RoomID";
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
                Rooms room = new Rooms(
                        rs.getInt("roomID"),
                        rs.getString("roomName"),
                        rs.getInt("roomCapacity")
                );
                Showtimes showtime = new Showtimes(
                        rs.getInt("ShowtimeID"),
                        movie,
                        room,
                        rs.getTime("StartTime"),
                        rs.getDate("DateTime"),
                        rs.getBigDecimal("Price")
                );
                showtimes.add(showtime);
            }
        }
        return showtimes;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}