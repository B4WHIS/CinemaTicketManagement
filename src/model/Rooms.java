package model;

public class Rooms {
    private int roomID;
    private String roomName;
    private int capacity;
    private String type;

    public Rooms(int roomID, String roomName, int capacity, String type) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.capacity = capacity;
        this.type = type;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getType() {
        return type;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setType(String type) {
        this.type = type;
    }
}
