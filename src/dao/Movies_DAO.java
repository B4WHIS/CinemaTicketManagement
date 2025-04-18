package dao;
import model.Movies;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Movies_DAO {
    private Connection con;
    public Movies_DAO(Connection conn){
        this.con = conn;
    }  
    public Movies_DAO(){
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTickerManagement";
            
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
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

      } 
      String sql = "INSERT INTO Movies (movieID, title, genre, duration, director, releaseDate, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, movies.getMovieID());
        stmt.setString(2, movies.getTitle());
        stmt.setString(3, movies.getGenre());
        stmt.setInt(4, movies.getDuration());
        stmt.setString(5, movies.getDirector());
        stmt.setTimestamp(6, Timestamp.valueOf(movies.getReleaseDate()));
        stmt.setBytes(7, movies.getImage());

        

        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
   
    }
    public boolean deleteMovie(int movieID) throws SQLException{
        String checkSql = "SELECT COUNT(*) FROM Movies WHERE movieID=?";
      try(PreparedStatement checkStmt = con.prepareStatement(checkSql) ){
        checkStmt.setInt(1,movieID);
        ResultSet rs = checkStmt.executeQuery();
        if(rs.next() && rs.getInt(1) == 0){
            return false;
        }
      }catch( SQLException e){
        e.printStackTrace();
        return false;
      }

      String sql = "DELETE FROM Movies WHERE movieID=?";
      try(PreparedStatement stmt = con.prepareStatement(sql) ){
        stmt.setInt(1,movieID);
        stmt.executeUpdate();
        return true;

      }catch(SQLException e){
        e.printStackTrace();
        return false;
        
    }
}
    public boolean updateMovie(Movies movie) {
        String checkSql = "SELECT COUNT(*) FROM Movies WHERE movieID=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, movie.getMovieID());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    
        String sql = "UPDATE Movies SET title=?, genre=?, duration=?, director=?, releaseDate=?, image=? WHERE movieID=?";
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
    public Movies getMovieByID(int id) {
    String sql = "SELECT * FROM Movies WHERE movieID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int movieID = rs.getInt("movieID");
            String title = rs.getString("title");
            String genre = rs.getString("genre");
            int duration = rs.getInt("duration");
            String director = rs.getString("director");
            LocalDateTime releaseDate = rs.getTimestamp("releaseDate").toLocalDateTime();
            byte[] image = rs.getBytes("image");
            return new Movies(movieID, title, genre, duration, director, releaseDate, image);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null; // Không tìm thấy
}
public List<Movies> getAllMovies() {
    List<Movies> movieList = new ArrayList<>();
    String sql = "SELECT * FROM Movies";

    try (PreparedStatement stmt = con.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int movieID = rs.getInt("movieID");
            String title = rs.getString("title");
            String genre = rs.getString("genre");
            int duration = rs.getInt("duration");
            String director = rs.getString("director");
            LocalDateTime releaseDate = rs.getTimestamp("releaseDate").toLocalDateTime();
            byte[] image = rs.getBytes("image");
          

            Movies movie = new Movies(movieID, title, genre, duration, director, releaseDate, image);
            movieList.add(movie);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return movieList;
}


    
}
