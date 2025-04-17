package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Rooms;
public class Rooms_DAO {
     private Connection con;
     public Rooms_DAO(Connection conn){
        this.con = conn;
    }  
    public Rooms_DAO(){
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=CinemaTickerManagement";
            
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean addRoom(Rooms room) {
    String checkSql = "SELECT COUNT(*) FROM Rooms WHERE roomName=?";
    try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
        checkStmt.setString(1, room.getRoomName());
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            return false; // Trùng tên phòng
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

    String sql = "INSERT INTO Rooms (roomID,roomName, capacity, type) VALUES (?,?, ?, ?)";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setString(11, room.getRoomIDID());
        stmt.setString(2, room.getRoomName());
        stmt.setInt(3, room.getCapacity());
        stmt.setString(4, room.getType());
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean updateRoom(Rooms room) {
    String checkSql = "SELECT COUNT(*) FROM Rooms WHERE roomID=?";
    try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
        checkStmt.setInt(1, room.getRoomID());
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            return false; // Không tồn tại
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

    String sql = "UPDATE Rooms SET roomName=?, capacity=?, type=? WHERE roomID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, room.getRoomID());
        stmt.setString(2, room.getRoomName());
        stmt.setInt(3, room.getCapacity());
        stmt.setString(4, room.getType());
      
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean deleteRoom(int roomID) {
    String checkSql = "SELECT COUNT(*) FROM Rooms WHERE roomID=?";
    try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
        checkStmt.setInt(1, roomID);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            return false; // Không tồn tại
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

    String sql = "DELETE FROM Rooms WHERE roomID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, roomID);
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public Rooms getRoomByID(int id) {
    String sql = "SELECT * FROM Rooms WHERE roomID=?";
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int roomID = rs.getInt("roomID");
            String roomName = rs.getString("roomName");
            int capacity = rs.getInt("capacity");
            String type = rs.getString("type");

            return new Roomss(roomID, roomName, capacity, type);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

public List<Rooms> getAllRooms() {
    List<Rooms> rooms = new ArrayList<>();
    String sql = "SELECT * FROM Rooms";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int roomID = rs.getInt("roomID");
            String name = rs.getString("name");
            int capacity = rs.getInt("capacity");

            Room room = new Room(roomID, name, capacity);
            rooms.add(room);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return rooms;
}



}
