package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Showtimes {
    private int showtimeID;
    private Movies movie;
    private Rooms room;
    private Time startTime; // Sửa từ Date sang Time
    private Date dateTime;  // Giữ Date nếu DateTime là DATE
    private BigDecimal price;

    public Showtimes() {
    }

    public Showtimes(int showtimeID, Movies movie, Rooms room, Time startTime, Date dateTime, BigDecimal price) {
        this.showtimeID = showtimeID;
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.dateTime = dateTime;
        this.price = price;
    }

  

	

	public Showtimes(int int1, int int2, Movies movie2, Date date, Time time, double double1) {
		// TODO Auto-generated constructor stub
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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
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