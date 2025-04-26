package model;

public class Rooms {
    private int roomID;
    private String roomName;
    private int capacity;

    public Rooms(int roomID, String roomName, int capacity) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.capacity = capacity;
        // B: Log khi tạo đối tượng Rooms
        System.out.println("Inside Rooms constructor - RoomID: " + this.roomID +
                ", RoomName: " + this.roomName +
                ", Capacity: " + this.capacity);
    }

    public int getRoomID() {
        // B: Log khi getRoomID được gọi
        System.out.println("getRoomID called - RoomID: " + roomID);
        return roomID;
    }

    public String getRoomName() {
        // B: Log khi getRoomName được gọi
        System.out.println("getRoomName called - RoomName: " + roomName);
        return roomName;
    }

    public int getCapacity() {
        return capacity;
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
}