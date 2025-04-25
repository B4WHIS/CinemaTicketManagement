package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Movies;
import model.Rooms;
import model.Showtimes;

public class Showtimes_DAO {
    private Connection con;

    // Constructor nhận connection từ bên ngoài
    public Showtimes_DAO(Connection conn) {
        this.con = conn;
    }

    // Constructor mặc định: tự lấySea connection từ connectDB
    public Showtimes_DAO() {
        this.con = connectDB.getConnection();
    }

    // Kiểm tra trạng thái Connection trước khi sử dụng
    private void ensureConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
            }
        }
    }

    // Thêm lịch chiếu mới
    public boolean addShowtime(Showtimes showtime) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE MovieID=? AND RoomID=? AND StartTime=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getMovie().getMovieID());
            checkStmt.setInt(2, showtime.getRoom().getRoomID());
            checkStmt.setDate(3, showtime.getStartTime());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Trùng lịch chiếu
                }
            }
        }

        String sql = "INSERT INTO Showtimes (ShowtimeID, MovieID, RoomID, StartTime, DateTime, Price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getShowTimeID());
            stmt.setInt(2, showtime.getMovie().getMovieID());
            stmt.setInt(3, showtime.getRoom().getRoomID());
            stmt.setDate(4, showtime.getStartTime());
            stmt.setDate(5, showtime.getdateTime());
            stmt.setBigDecimal(6, showtime.getPrice());
            stmt.executeUpdate();
            return true;
        }
    }

    // Cập nhật lịch chiếu
    public boolean updateShowtime(Showtimes showtime) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false; // Không tồn tại
                }
            }
        }

        String sql = "UPDATE Showtimes SET MovieID=?, RoomID=?, StartTime=?, DateTime=?, Price=? WHERE ShowtimeID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getMovie().getMovieID());
            stmt.setInt(2, showtime.getRoom().getRoomID());
            stmt.setDate(3, showtime.getStartTime());
            stmt.setDate(4, showtime.getdateTime());
            stmt.setBigDecimal(5, showtime.getPrice());
            stmt.setInt(6, showtime.getShowTimeID());
            stmt.executeUpdate();
            return true;
        }
    }

    // Xóa lịch chiếu
    public boolean deleteShowtime(int showtimeID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false; // Không tồn tại
                }
            }
        }

        String sql = "DELETE FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
            return true;
        }
    }

    // Lấy thông tin lịch chiếu theo ID
    public Showtimes getShowtimeByID(int id) throws SQLException {
        ensureConnection();

        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                     "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                     "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                     "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                     "FROM Showtimes s " +
                     "JOIN Movies m ON s.MovieID = m.MovieID " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.ShowtimeID = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
                            rs.getInt("RoomID"),
                            rs.getString("roomName"),
                            rs.getInt("roomCapacity"),
                            rs.getString("roomType")
                    );

                    return new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            rs.getDate("StartTime"),
                            rs.getDate("DateTime"),
                            rs.getBigDecimal("Price")
                    );
                }
            }
        }
        return null;
    }

    // Lấy danh sách lịch chiếu theo mã phim
    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        ensureConnection();

        List<Showtimes> showtimeList = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                     "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                     "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                     "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                     "FROM Showtimes s " +
                     "JOIN Movies m ON s.MovieID = m.MovieID " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.MovieID = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
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
                            rs.getInt("RoomID"),
                            rs.getString("roomName"),
                            rs.getInt("roomCapacity"),
                            rs.getString("roomType")
                    );

                    Showtimes showtime = new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            rs.getDate("StartTime"),
                            rs.getDate("DateTime"),
                            rs.getBigDecimal("Price")
                    );
                    showtimeList.add(showtime);
                }
            }
        }
        return showtimeList;
    }

    // Lấy toàn bộ danh sách lịch chiếu
    public List<Showtimes> getAllShowtimes() throws SQLException {
        ensureConnection();

        List<Showtimes> showtimeList = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                     "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                     "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                     "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                     "FROM Showtimes s " +
                     "JOIN Movies m ON s.MovieID = m.MovieID " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "ORDER BY s.StartTime ASC";

        try (PreparedStatement stmt = con.prepareStatement(sql);
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
                        rs.getInt("RoomID"),
                        rs.getString("roomName"),
                        rs.getInt("roomCapacity"),
                        rs.getString("roomType")
                );

                Showtimes showtime = new Showtimes(
                        rs.getInt("ShowtimeID"),
                        movie,
                        room,
                        rs.getDate("StartTime"),
                        rs.getDate("DateTime"),
                        rs.getBigDecimal("Price")
                );
                showtimeList.add(showtime);
            }
        }
        return showtimeList;
    }

    // Đóng kết nối
    public void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}