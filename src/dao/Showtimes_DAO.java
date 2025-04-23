package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dbs.connectDB;
import model.Movies;
import model.Rooms;
import model.Showtimes;

public class Showtimes_DAO {
    private Connection con;

    public Showtimes_DAO(Connection conn) {
        this.con = conn;
    }

    public Showtimes_DAO() {
        this.con = connectDB.getConnection();
    }

    // Thêm lịch chiếu mới
    public boolean addShowtime(Showtimes showtime) {
        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE MovieID=? AND RoomID=? AND StartTime=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getMovie().getMovieID());
            checkStmt.setInt(2, showtime.getRoom().getRoomID());
            checkStmt.setTimestamp(3, Timestamp.valueOf(showtime.getStartTime()));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Trùng lịch chiếu
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO Showtimes (ShowtimeID, MovieID, RoomID, StartTime, DateTime, Price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getShowTimeID());
            stmt.setInt(2, showtime.getMovie().getMovieID());
            stmt.setInt(3, showtime.getRoom().getRoomID());
            stmt.setTimestamp(4, Timestamp.valueOf(showtime.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(showtime.getdateTime()));
            stmt.setBigDecimal(6, showtime.getPrice());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật lịch chiếu
    public boolean updateShowtime(Showtimes showtime) {
        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; // Không tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE Showtimes SET MovieID=?, RoomID=?, StartTime=?, DateTime=?, Price=? WHERE showtimeID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtime.getMovie().getMovieID());
            stmt.setInt(2, showtime.getRoom().getRoomID());
            stmt.setTimestamp(3, Timestamp.valueOf(showtime.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(showtime.getdateTime()));
            stmt.setBigDecimal(5, showtime.getPrice());
            stmt.setInt(6, showtime.getShowTimeID());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa lịch chiếu
    public boolean deleteShowtime(int showtimeID) {
        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; // Không tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "DELETE FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thông tin lịch chiếu theo ID
    public Showtimes getShowtimeByID(int id) {
        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                     "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                     "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                     "FROM Showtimes s " +
                     "JOIN Movies m ON s.MovieID = m.MovieID " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "WHERE s.ShowtimeID = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Lấy thông tin từ ResultSet và tạo đối tượng Showtimes
                int showtimeID = rs.getInt("ShowtimeID");
                int movieID = rs.getInt("MovieID");
                int roomID = rs.getInt("RoomID");
                LocalDateTime startTime = rs.getTimestamp("StartTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("DateTime").toLocalDateTime();
                BigDecimal price = rs.getBigDecimal("Price");
                
                // Tạo đối tượng Movie và Room từ kết quả
                Movies movie = new Movies(
                        movieID,
                        rs.getString("movieTitle"),
                        rs.getString("movieGenre"),
                        rs.getInt("movieDuration"),
                        rs.getString("movieDirector"),
                        rs.getTimestamp("movieReleaseDate").toLocalDateTime(),
                        rs.getBytes("movieImage")
                );
                
                Rooms room = new Rooms(
                        roomID,
                        rs.getString("roomName"),
                        rs.getInt("roomCapacity"),
                        rs.getString("roomType")
                );

                return new Showtimes(showtimeID, movie, room, startTime, endTime, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy lịch chiếu
    }


    // Lấy danh sách lịch chiếu theo mã phim
    public List<Showtimes> getShowtimesByMovie(int movieID) {
        List<Showtimes> showtimeList = new ArrayList<>();
        
        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                "FROM Showtimes s " +
                "JOIN Movies m ON s.MovieID = m.MovieID " +
                "JOIN Rooms r ON s.RoomID = r.RoomID " +
                "WHERE s.MovieID = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int showtimeID = rs.getInt("ShowtimeID");
                int roomID = rs.getInt("RoomID");
                LocalDateTime startTime = rs.getTimestamp("StartTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("DateTime").toLocalDateTime();
                BigDecimal price = rs.getBigDecimal("Price");

                Movies movie = new Movies(
                        movieID,
                        rs.getString("movieTitle"),
                        rs.getString("movieGenre"),
                        rs.getInt("movieDuration"),
                        rs.getString("movieDirector"),
                        rs.getTimestamp("movieReleaseDate").toLocalDateTime(),
                        rs.getBytes("movieImage")
                );

                Rooms room = new Rooms(
                        roomID,
                        rs.getString("roomName"),
                        rs.getInt("roomCapacity"),
                        rs.getString("roomType")
                );

                Showtimes showtime = new Showtimes(showtimeID, movie, room, startTime, endTime, price);
                showtimeList.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtimeList;
    }
    
    
    
 // Lấy toàn bộ danh sách lịch chiếu
    public List<Showtimes> getAllShowtimes() {
        List<Showtimes> showtimeList = new ArrayList<>();
        
        String sql = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.StartTime, s.DateTime, s.Price, " +
                "m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                "r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                "FROM Showtimes s " +  // sửa chỗ này
                "JOIN Movies m ON s.MovieID = m.MovieID " +
                "JOIN Rooms r ON s.RoomID = r.RoomID " +
                "ORDER BY s.StartTime ASC";  // Sắp xếp theo thời gian bắt đầu (tùy chọn)

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Tạo đối tượng Movies
                Movies movie = new Movies(
                    rs.getInt("MovieID"),
                    rs.getString("movieTitle"),
                    rs.getString("movieGenre"),
                    rs.getInt("movieDuration"),
                    rs.getString("movieDirector"),
                    rs.getTimestamp("movieReleaseDate").toLocalDateTime(),
                    rs.getBytes("movieImage")
                );

                // Tạo đối tượng Rooms
                Rooms room = new Rooms(
                    rs.getInt("RoomID"),
                    rs.getString("roomName"),
                    rs.getInt("roomCapacity"),
                    rs.getString("roomType")
                );

                // Tạo đối tượng Showtimes và thêm vào danh sách
                Showtimes showtime = new Showtimes(
                    rs.getInt("ShowtimeID"),
                    movie,
                    room,
                    rs.getTimestamp("StartTime").toLocalDateTime(),
                    rs.getTimestamp("DateTime").toLocalDateTime(),
                    rs.getBigDecimal("Price")
                );

                showtimeList.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtimeList;
    }

}
