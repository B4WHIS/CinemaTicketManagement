package dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
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
        if (showtime == null || showtime.getMovie() == null || showtime.getRoom() == null) {
            // System.err.println("Showtime, Movie hoặc Room không hợp lệ.");
            return false;
        }

        ensureConnection();

        String check = "{call sp_CheckShowtimeExists(?)}";
        try (CallableStatement checkStmt = connection.prepareCall(check)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("IsExists") > 0) {
                    // System.out.println("ShowtimeID " + showtime.getShowTimeID() + " đã tồn tại.");
                    return false;
                }
            }
        }

        String query = "{call sp_InsertShowtime(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setInt(1, showtime.getShowTimeID());
            stmt.setInt(2, showtime.getMovie().getMovieID());
            stmt.setInt(3, showtime.getRoom().getRoomID());
            stmt.setDate(4, showtime.getdateTime());
            stmt.setTime(5, showtime.getStartTime());
            stmt.setBigDecimal(6, showtime.getPrice());

            stmt.execute();
            System.out.println("Thêm suất chiếu thành công - ShowtimeID: " + showtime.getShowTimeID());
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm suất chiếu - ShowtimeID: " + showtime.getShowTimeID());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateShowtime(Showtimes showtime) throws SQLException {
        if (showtime == null || showtime.getMovie() == null || showtime.getRoom() == null) {
            // System.err.println("Showtime, Movie hoặc Room không hợp lệ.");
            return false;
        }

        ensureConnection();

        String check = "{call sp_CheckShowtimeExists(?)}";
        try (CallableStatement checkStmt = connection.prepareCall(check)) {
            checkStmt.setInt(1, showtime.getShowTimeID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("IsExists") > 0) {
                    // System.out.println("ShowtimeID " + showtime.getShowTimeID() + " đã tồn tại.");
                    return false;
                }
            }
        }

        String query = "{call sp_UpdateShowtime(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setInt(1, showtime.getShowTimeID());
            stmt.setInt(2, showtime.getMovie().getMovieID());
            stmt.setInt(3, showtime.getRoom().getRoomID());
            stmt.setDate(4, showtime.getdateTime());
            stmt.setTime(5, showtime.getStartTime());
            stmt.setBigDecimal(6, showtime.getPrice());

            stmt.execute();
            // System.out.println("Cập nhật suất chiếu thành công - ShowtimeID: " + showtime.getShowTimeID());
            return true;
        } catch (SQLException e) {
            // System.err.println("Lỗi khi cập nhật suất chiếu - ShowtimeID: " + showtime.getShowTimeID());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteShowtime(int showtimeID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, showtimeID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // System.out.println("ShowtimeID " + showtimeID + " không tồn tại.");
                    return false;
                }
            }
        }

        String sql = "DELETE FROM Showtimes WHERE ShowtimeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
            // System.out.println("Xóa suất chiếu thành công - ShowtimeID: " + showtimeID);
            return true;
        } catch (SQLException e) {
            // System.err.println("Lỗi khi xóa suất chiếu - ShowtimeID: " + showtimeID);
            e.printStackTrace();
            throw e;
        }
    }

    public Showtimes getShowtimeByID(int showtimeID) throws SQLException {
        ensureConnection();

        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
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
                    int roomID = rs.getInt("roomID");
                    String roomName = rs.getString("roomName");
                    int roomCapacity = rs.getInt("roomCapacity");
                    String roomType = rs.getString("roomType");
                    Rooms room = new Rooms(
                            roomID,
                            roomName != null ? roomName : "Phòng không xác định",
                            roomCapacity,
                            roomType != null ? roomType : "Không xác định"
                    );
                    Showtimes showtime = new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            rs.getTime("StartTime"),
                            rs.getDate("DateTime"),
                            rs.getBigDecimal("Price")
                    );
                    // System.out.println("Lấy suất chiếu thành công - ShowtimeID: " + showtime.getShowTimeID() +
                    //         ", RoomID: " + room.getRoomID() +
                    //         ", RoomName: " + room.getRoomName());
                    return showtime;
                }
            }
        }
        // System.out.println("Không tìm thấy suất chiếu với ShowtimeID: " + showtimeID);
        return null;
    }

    public List<Showtimes> getShowtimesByMovie(int movieID) throws SQLException {
        ensureConnection();

        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
                    "FROM Showtimes s " +
                    "JOIN Movies m ON s.MovieID = m.MovieID " +
                    "JOIN Rooms r ON s.RoomID = r.RoomID " +
                    "WHERE m.MovieID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // System.out.println("Dữ liệu thô - ShowtimeID: " + rs.getInt("ShowtimeID") +
                    //         ", RoomID: " + rs.getInt("roomID") +
                    //         ", RoomName: " + rs.getString("roomName") +
                    //         ", RoomCapacity: " + rs.getInt("roomCapacity") +
                    //         ", RoomType: " + rs.getString("roomType"));

                    Movies movie = new Movies(
                            rs.getInt("MovieID"),
                            rs.getString("movieTitle"),
                            rs.getString("movieGenre"),
                            rs.getInt("movieDuration"),
                            rs.getString("movieDirector"),
                            rs.getDate("movieReleaseDate"),
                            rs.getString("movieImage")
                    );

                    int roomID = rs.getInt("roomID");
                    String roomName = rs.getString("roomName");
                    int roomCapacity = rs.getInt("roomCapacity");
                    String roomType = rs.getString("roomType");

                    Rooms room = new Rooms(
                            roomID,
                            roomName != null ? roomName : "Phòng không xác định",
                            roomCapacity,
                            roomType != null ? roomType : "Không xác định"
                    );

                    BigDecimal price = rs.getBigDecimal("Price");
                    java.sql.Time startTime = rs.getTime("StartTime");
                    java.sql.Date dateTime = rs.getDate("DateTime");

                    // System.out.println("ShowtimeID: " + rs.getInt("ShowtimeID") +
                    //         ", StartTime: " + startTime +
                    //         ", DateTime: " + dateTime +
                    //         ", Price: " + price);

                    Showtimes showtime = new Showtimes(
                            rs.getInt("ShowtimeID"),
                            movie,
                            room,
                            startTime,
                            dateTime,
                            price
                    );

                    // System.out.println("Tạo suất chiếu - ShowtimeID: " + showtime.getShowTimeID() +
                    //         ", RoomID: " + room.getRoomID() +
                    //         ", RoomName: " + room.getRoomName());

                    showtimes.add(showtime);
                }
            }
        }
        // System.out.println("Lấy " + showtimes.size() + " suất chiếu cho MovieID: " + movieID);
        return showtimes;
    }

    public List<Showtimes> getAllShowtimes() throws SQLException {
        ensureConnection();

        List<Showtimes> showtimes = new ArrayList<>();
        String sql = "SELECT s.ShowtimeID, s.RoomID, s.DateTime, s.StartTime, s.Price, " +
                    "m.MovieID, m.Title AS movieTitle, m.Genre AS movieGenre, m.Duration AS movieDuration, " +
                    "m.Director AS movieDirector, m.ReleaseDate AS movieReleaseDate, m.Image AS movieImage, " +
                    "r.RoomID AS roomID, r.RoomName AS roomName, r.Capacity AS roomCapacity, r.Type AS roomType " +
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
                int roomID = rs.getInt("roomID");
                String roomName = rs.getString("roomName");
                int roomCapacity = rs.getInt("roomCapacity");
                String roomType = rs.getString("roomType");
                Rooms room = new Rooms(
                        roomID,
                        roomName != null ? roomName : "Phòng không xác định",
                        roomCapacity,
                        roomType != null ? roomType : "Không xác định"
                );

                Showtimes showtime = new Showtimes(
                        rs.getInt("ShowtimeID"),
                        movie,
                        room,
                        rs.getTime("StartTime"),
                        rs.getDate("DateTime"),
                        rs.getBigDecimal("Price")
                );

                // System.out.println("Tạo suất chiếu - ShowtimeID: " + showtime.getShowTimeID() +
                //         ", RoomID: " + room.getRoomID() +
                //         ", RoomName: " + room.getRoomName());

                showtimes.add(showtime);
            }
        }
        // System.out.println("Lấy tất cả " + showtimes.size() + " suất chiếu.");
        return showtimes;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            // System.out.println("Đóng kết nối cơ sở dữ liệu thành công.");
        }
    }
}