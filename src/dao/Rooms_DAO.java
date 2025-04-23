package dao;

import dbs.connectDB;
import model.Rooms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Rooms_DAO {
    private static Connection con;

    public Rooms_DAO(Connection conn) {
        this.con = conn;
    }

    public Rooms_DAO() {
        this.con = connectDB.getConnection();
    }

    // Thêm phòng mới
    public boolean addRoom(Rooms room) {
        String checkSql = "SELECT COUNT(*) FROM Rooms WHERE RoomName=?";
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

        String sql = "INSERT INTO Rooms (RoomID, RoomName, Capacity, Type) VALUES (?, ?, ?, ?)";
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

    // Cập nhật phòng
    public boolean updateRoom(Rooms room) {
        String checkSql = "SELECT COUNT(*) FROM Rooms WHERE RoomID=?";
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

        String sql = "UPDATE Rooms SET RoomName=?, Capacity=?, Type=? WHERE RoomID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getType());
            stmt.setInt(4, room.getRoomID());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phòng
    public boolean deleteRoom(int roomID) {
        String checkSql = "SELECT COUNT(*) FROM Rooms WHERE RoomID=?";
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

        String sql = "DELETE FROM Rooms WHERE RoomID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, roomID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy phòng theo ID
    public static Rooms getRoomByID(int id) {
        String sql = "SELECT * FROM Rooms WHERE RoomID=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int roomID = rs.getInt("RoomID");
                String roomName = rs.getString("RoomName");
                int capacity = rs.getInt("Capacity");
                String type = rs.getString("Type");

                return new Rooms(roomID, roomName, capacity, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả phòng
    public List<Rooms> getAllRooms() {
        List<Rooms> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int roomID = rs.getInt("RoomID");
                String roomName = rs.getString("RoomName");
                int capacity = rs.getInt("Capacity");
                String type = rs.getString("Type");

                Rooms room = new Rooms(roomID,  roomName, capacity, type);
                rooms.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }
}
