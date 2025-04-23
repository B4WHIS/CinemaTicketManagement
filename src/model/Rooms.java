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
   public Rooms() {}
   public Rooms(int roomID2, String name, int capacity2){}

   public int getRoomID() {
    return roomID;
   }

   public void setRoomID(int roomID) {
    this.roomID = roomID;
   }

   public String getRoomName() {
    return roomName;
   }

   public void setRoomName(String roomName) {
    this.roomName = roomName;
   }

   public int getCapacity() {
    return capacity;
   }

   public void setCapacity(int capacity) {
    this.capacity = capacity;
   }

   public String getType() {
    return type;
   }

   public void setType(String type) {
    this.type = type;
   }


}
