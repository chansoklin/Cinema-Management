package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Schedule {
    private int id;
    private int movieId;
    private int screenId;
    private Timestamp startTime;
    private Timestamp endTime;
    private double basePrice;

    private Movie movie;
    private String screenName;

    public Schedule() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getScreenId() { return screenId; }
    public void setScreenId(int screenId) { this.screenId = screenId; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) {
        this.movie = movie;
        if (movie != null) {
            this.movieId = movie.getId();
        }
    }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    // Additional methods for ScheduleDialog and other UI components
    public String getMovieTitle() {
        return movie != null ? movie.getTitle() : "Unknown Movie";
    }

    public String getHall() {
        return screenName != null ? screenName : "Screen " + screenId;
    }

    public String getFormattedShowTime() {
        if (startTime == null) return "TBD";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(startTime);
    }

    public double getPrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return movie != null ? movie.getTitle() + " - " + startTime : String.valueOf(id);
    }
}