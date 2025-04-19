package service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import dao.Rooms_DAO;
import model.Rooms;
public class RoomManager {
    private Rooms_DAO roomDAO;

    public RoomManager() {
        try {
            Connection conn = SQLServerConnection.getConnection();
            this.roomDAO = new Rooms_DAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addRoom(Rooms room) {
        return roomDAO.addRoom(room);
    }

    public boolean updateRoom(Rooms room) {
        return roomDAO.updateRoom(room);
    }

    public boolean deleteRoom(int roomID) {
        return roomDAO.deleteRoom(roomID);
    }

    public Rooms getRoomByID(int id) {
        return roomDAO.getRoomByID(id);
    }

    public List<Rooms> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}
