package model;


import java.time.LocalDateTime;


public class Movies {
    private int movieID;
    private String title;
    private String genre;
    private int duration;
    private String director;
    private LocalDateTime releaseDate;
    private byte[] image;

    public Movies(){
    }
    public Movies(int movieID, String title, String genre, int duration, String director, LocalDateTime releaseDate, byte[] image){
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.releaseDate = releaseDate;
        this.image = image;
    }
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
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
  

   

}