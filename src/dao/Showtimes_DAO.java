package dao;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Showtimes;
public class Showtimes_DAO {
      private Connection con;
    public Showtimes_DAO(Connection conn){
        this.con = conn;
    }  
    public Showtimes_DAO(){
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTickerManagement";
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean addShowtime(Showtimes showtime) {
    String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE movieID=? AND roomID=? AND startTime=?";
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

    String sql = "INSERT INTO Showtimes (showtimeID,movieID, roomID, startTime, endTime, price) VALUES (?,?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, showtime.getShowTimeID());
        stmt.setInt(2, showtime.getMovie().getMovieID());
        stmt.setInt(3, showtime.getRoom().getRoomID());
        stmt.setTimestamp(4, Timestamp.valueOf(showtime.getStartTime()));
        stmt.setTimestamp(5, Timestamp.valueOf(showtime.getEndTime()));
        stmt.setBigDecimal(6, showtime.getPrice());
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean updateShowtime(Showtimes showtime) {
    String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE showtimeID=?";
    try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
        checkStmt.setInt(1, showtime.getShowtimeID());
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            return false; // Không tồn tại
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

    String sql = "UPDATE Showtimes SET movieID=?, roomID=?, startTime=?, endTime=?, price=? WHERE showtimeID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, showtime.getShowtimeID());
        stmt.setInt(2, showtime.getMovie().getMovieID());
        stmt.setInt(3, showtime.getRoom().getRoomID());
        stmt.setTimestamp(4, Timestamp.valueOf(showtime.getStartTime()));
        stmt.setTimestamp(5, Timestamp.valueOf(showtime.getEndTime()));
        stmt.setBigDecimal(6, showtime.getPrice());
        
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean deleteShowtime(int showtimeID) {
    String checkSql = "SELECT COUNT(*) FROM Showtimes WHERE showtimeID=?";
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

    String sql = "DELETE FROM Showtimes WHERE showtimeID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, showtimeID);
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public Showtimes getShowtimeByID(int id) {
    String sql = "SELECT * FROM Showtimes WHERE showtimeID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int showtimeID = rs.getInt("showtimeID");
            int movieID = rs.getInt("movieID");
            int roomID = rs.getInt("roomID");
            LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
            LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
            BigDecimal price = rs.getBigDecimal("price");

            Movies movie = Movies_DAO.getMovieByID(movieID);
            Rooms room = Rooms_DAO.getRoomByID(roomID);

            return new Showtimes(showtimeID, movie, room, startTime, endTime, price);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
public List<Showtimes> getShowtimesByMovie(int movieID) {
    List<Showtimes> showtimeList = new ArrayList<>();
    String sql = "SELECT * FROM Showtimes WHERE movieID = ?";

    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, movieID);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int showtimeID = rs.getInt("showtimeID");
            int roomID = rs.getInt("roomID");
            LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
            LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
            BigDecimal price = rs.getBigDecimal("price");

            // Giả sử bạn đã có movieDAO và roomDAO
            Movies movie = Movies_DAO.getMovieByID(movieID);
            Rooms room = Rooms_DAO.getRoomByID(roomID);

            Showtimes showtime = new Showtimes(showtimeID, movie, room, startTime, endTime, price);
            showtimeList.add(showtime);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return showtimeList;
}




}
