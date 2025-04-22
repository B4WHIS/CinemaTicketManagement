package model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Showtimes {
    private int showTimeID;
    private Movies movie;
    private Rooms room;
    private LocalDateTime startTime;
    private LocalDateTime dateTime;
    private BigDecimal price;
    public Showtimes() {
    }
    public Showtimes(int showTimeID, Movies movie, Rooms room, LocalDateTime startTime, LocalDateTime endTime,
            BigDecimal price) {
        this.showTimeID = showTimeID;
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.dateTime = endTime;
        this.price = price;
    }
    public int getShowTimeID() {
        return showTimeID;
    }
    public void setShowTimeID(int showTimeID) {
        this.showTimeID = showTimeID;
    }
    public Movies getMovie() {
        return movie;
    }
    public void setMovie(Movies movie) {
        this.movie = movie;
    }
    public Rooms getRoom() {
        return room;
    }
    public void setRoom(Rooms room) {
        this.room = room;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getdateTime() {
        return dateTime;
    }
    public void setdateTime(LocalDateTime endTime) {
        this.dateTime = endTime;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

  
}

