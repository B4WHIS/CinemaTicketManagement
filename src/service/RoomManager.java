package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Rooms_DAO;
import dbs.connectDB;
import model.Rooms;

public class RoomManager {
	 private Rooms_DAO roomDAO;
	public RoomManager() {
        Connection con = connectDB.getConnection();
		this.roomDAO = new Rooms_DAO(con);
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
