package dao;

import dbs.connectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Movies;

public class Movies_DAO {
    private Connection con;

    // Constructor nhận connection từ bên ngoài
    public Movies_DAO(Connection conn) {
        this.con = conn;
    }

    // Constructor mặc định: tự lấy connection từ connectDB
    public Movies_DAO() {
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

    // Thêm phim mới
    public boolean addMovie(Movies movie) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Movies WHERE Title=? AND Director=? AND ReleaseDate=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setString(1, movie.getTitle());
            checkStmt.setString(2, movie.getDirector());
            checkStmt.setDate(3, movie.getReleaseDate());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Phim đã tồn tại
                }
            }
        }

        String sql = "INSERT INTO Movies (MovieID, Title, Genre, Duration, Director, ReleaseDate, Image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, movie.getMovieID());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getDirector());
            stmt.setDate(6, movie.getReleaseDate());
            stmt.setString(7, movie.getImage());

            stmt.executeUpdate();
            return true;
        }
    }

    // Xóa phim
    public boolean deleteMovie(int movieID) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Movies WHERE MovieID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, movieID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false; // Phim không tồn tại
                }
            }
        }

        String sql = "DELETE FROM Movies WHERE MovieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            stmt.executeUpdate();
            return true;
        }
    }

    // Cập nhật thông tin phim
    public boolean updateMovie(Movies movie) throws SQLException {
        ensureConnection();

        String checkSql = "SELECT COUNT(*) FROM Movies WHERE MovieID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, movie.getMovieID());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return false; // Phim không tồn tại
                }
            }
        }

        String sql = "UPDATE Movies SET Title=?, Genre=?, Duration=?, Director=?, ReleaseDate=?, Image=? WHERE MovieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDirector());
            stmt.setDate(5, movie.getReleaseDate());
            stmt.setString(6, movie.getImage());
            stmt.setInt(7, movie.getMovieID());

            stmt.executeUpdate();
            return true;
        }
    }

    // Lấy phim theo ID
    public Movies getMovieByID(int id) throws SQLException {
        ensureConnection();

        String sql = "SELECT * FROM Movies WHERE MovieID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Movies(
                            rs.getInt("MovieID"),
                            rs.getString("Title"),
                            rs.getString("Genre"),
                            rs.getInt("Duration"),
                            rs.getString("Director"),
                            rs.getDate("ReleaseDate"),
                            rs.getString("Image")
                    );
                }
            }
        }
        return null;
    }

    // Lấy tất cả phim
    public List<Movies> getAllMovies() throws SQLException {
        ensureConnection();

        List<Movies> movieList = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Movies movie = new Movies(
                        rs.getInt("MovieID"),
                        rs.getString("Title"),
                        rs.getString("Genre"),
                        rs.getInt("Duration"),
                        rs.getString("Director"),
                        rs.getDate("ReleaseDate"),
                        rs.getString("Image")
                );
                movieList.add(movie);
            }
        }
        return movieList;
    }

    // Tìm kiếm phim theo tiêu đề
    public List<Movies> searchMoviesByTitle(String keyword) throws SQLException {
        ensureConnection();

        List<Movies> list = new ArrayList<>();
        String sql = "SELECT * FROM Movies WHERE Title LIKE ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movies movie = new Movies(
                            rs.getInt("MovieID"),
                            rs.getString("Title"),
                            rs.getString("Genre"),
                            rs.getInt("Duration"),
                            rs.getString("Director"),
                            rs.getDate("ReleaseDate"),
                            rs.getString("Image")
                    );
                    list.add(movie);
                }
            }
        }
        return list;
    }

    // Đóng kết nối (nếu cần)
    public void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}