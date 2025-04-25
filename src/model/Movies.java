package model;

import java.sql.Date;

public class Movies {
    private int movieID;
    private String title;
    private String genre;
    private int duration;
    private String director;
    private Date releaseDate;
    private String image;

    public Movies() {
        // Constructor mặc định cho AddMovieFrame
    }

    public Movies(int movieID, String title, String genre, int duration, String director, Date releaseDate, String image) {
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.releaseDate = releaseDate;
        this.image = image;
    }

    // Xóa constructor với byte[] vì không cần nữa
    // public Movies(int i, String string, String string2, int j, String string3, Date date, byte[] bs) {}

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date date) {
        this.releaseDate = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}