package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Showtimes {
    private int showTimeID;
    private Movies movie;
    private Rooms room;
    private Time startTime;
    private Date dateTime;
    private BigDecimal price;

    public Showtimes(int showTimeID, Movies movie, Rooms room, Time startTime, Date dateTime, BigDecimal price) {
        this.showTimeID = showTimeID;
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.dateTime = dateTime;
        this.price = price;
    }

    public Showtimes() {
        // Constructor mặc định không khởi tạo room
    }

    public int getShowTimeID() {
        return showTimeID;
    }

    public Movies getMovie() {
        return movie;
    }

    public Rooms getRoom() {
        if (room == null) {
            System.err.println("Room is null for ShowtimeID: " + showTimeID);
            return new Rooms(-1, "Phòng không hợp lệ", 0,"...");
        }
        
        return room;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Date getdateTime() {
        return dateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setShowTimeID(int showTimeID) {
        this.showTimeID = showTimeID;
    }

    public void setMovie(Movies movie) {
        this.movie = movie;
    }

    public void setRoom(Rooms room) {
        this.room = room;
        // B: Log khi setRoom được gọi
        System.out.println("setRoom called - ShowtimeID: " + showTimeID +
                ", RoomID: " + (room != null ? room.getRoomID() : "null") +
                ", RoomName: " + (room != null ? room.getRoomName() : "null"));
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setdateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}