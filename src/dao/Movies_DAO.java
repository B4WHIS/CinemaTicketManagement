package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Movies;

public class Movies_DAO {
    private Connection con;

    // Constructor nhận connection từ bên ngoài
    public Movies_DAO(Connection conn) {
        this.con = conn;
    }
<<<<<<< HEAD
    public boolean addMovie(Movies movies){
        String checksql = "SELECT COUNT(*) FROM Movies WHERE title=? AND director=? AND releaseDate=?";
      try(PreparedStatement checkSmt = con.prepareStatement(checksql)  ){
        checkSmt.setString(1,movies.getTitle());
        checkSmt.setString(2,movies.getDirector());
        checkSmt.setString(3,Timestamp.valueOf(movies.getReleaseDate()));
        ResultSet rs = checkSmt.executeQuery();
        if(rs.next() && rs.getInt(1) > 0){
            return false;
        }
=======
>>>>>>> d2b3e1732ddec16421b08b270050edd0ebdff2c5

    // Constructor mặc định: tự lấy connection từ connectDB
    public Movies_DAO() {
        this.con = connectDB.getConnection();
    }

    // Thêm phim mới
    public boolean addMovie(Movies movie) {
        String checkSql = "SELECT COUNT(*) FROM Movies WHERE title=? AND director=? AND releaseDate=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setString(1, movie.getTitle());
            checkStmt.setString(2, movie.getDirector());
            checkStmt.setTimestamp(3, Timestamp.valueOf(movie.getReleaseDate()));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Phim đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO Movies (movieID, title, genre, duration, director, releaseDate, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, movie.getMovieID());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getDirector());
            stmt.setTimestamp(6, Timestamp.valueOf(movie.getReleaseDate()));
            stmt.setBytes(7, movie.getImage());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phim
    public boolean deleteMovie(int movieID) {
        String checkSql = "SELECT COUNT(*) FROM Movies WHERE movieID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, movieID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; // Phim không tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "DELETE FROM Movies WHERE movieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin phim
    public boolean updateMovie(Movies movie) {
        String checkSql = "SELECT COUNT(*) FROM Movies WHERE movieID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, movie.getMovieID());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; // Phim không tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE Movies SET title=?, genre=?, duration=?, director=?, releaseDate=?, image=? WHERE movieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDirector());
            stmt.setTimestamp(5, Timestamp.valueOf(movie.getReleaseDate()));
            stmt.setBytes(6, movie.getImage());
            stmt.setInt(7, movie.getMovieID());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy phim theo ID
    public Movies getMovieByID(int id) {
        String sql = "SELECT * FROM Movies WHERE movieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Movies(
                        rs.getInt("movieID"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("director"),
                        rs.getTimestamp("releaseDate").toLocalDateTime(),
                        rs.getBytes("image")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả phim
    public List<Movies> getAllMovies() {
        List<Movies> movieList = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movies movie = new Movies(
                        rs.getInt("movieID"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("director"),
                        rs.getTimestamp("releaseDate").toLocalDateTime(),
                        rs.getBytes("image")
                );
                movieList.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieList;
    }
}
