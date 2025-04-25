package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Showtimes {
    private int showtimeID;
    private Movies movie;
    private Rooms room;
    private Date startTime;
    private Date dateTime;
    private BigDecimal price;

    
    
    public Showtimes() {
		
	}

	public Showtimes(int showtimeID, Movies movie, Rooms room, Date startTime, Date dateTime, BigDecimal price) {
        this.showtimeID = showtimeID;
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.dateTime = dateTime;
        this.price = price;
    }

    public int getShowTimeID() {
        return showtimeID;
    }

    public void setShowTimeID(int showtimeID) {
        this.showtimeID = showtimeID;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getdateTime() {
        return dateTime;
    }

    public void setdateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}