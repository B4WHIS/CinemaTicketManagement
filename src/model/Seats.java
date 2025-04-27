package model;

public class Seats {
    private int seatID;
    private String seatNumber;
    private Rooms room;
    
    
    public Seats() {
	
	}

	public Seats(int seatID, String seatNumber, Rooms room) {
        this.seatID = seatID;
        this.seatNumber = seatNumber;
        this.room = room;
    }

    public int getSeatID() {
        return seatID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public Rooms getRoom() {
        return room;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setRoom(Rooms room) {
        this.room = room;
    }
}