package model;

public class SeatStatus {
    private int reservationID;
    private int showtimeID;
    private int seatID;
    private String status;

    public SeatStatus() {
    }

    public SeatStatus(int reservationID, int showtimeID, int seatID, String status) {
        this.reservationID = reservationID;
        this.showtimeID = showtimeID;
        this.seatID = seatID;
        this.status = status;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public int getShowtimeID() {
        return showtimeID;
    }

    public void setShowtimeID(int showtimeID) {
        this.showtimeID = showtimeID;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}