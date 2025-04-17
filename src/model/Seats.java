package model;

public class Seats {
	private int seatID;
	private int roomID;
	private String seatNumber;
	
	public Seats() {
		
	}

	public Seats(int seatID, int roomID, String seatNumber) {
		this.seatID = seatID;
		this.roomID = roomID;
		this.seatNumber = seatNumber;
	}

	public int getSeatID() {
		return seatID;
	}

	public void setSeatID(int seatID) {
		this.seatID = seatID;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}
	
	
	
}
