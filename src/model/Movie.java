package model;

public class Movie {
    private int id;
    private String title;
    private int duration;
    private String genre;
    private String rating;
    private String description;
    private String posterUrl;
    private boolean isActive;

    public Movie() {}

    public Movie(String title, int duration, String genre, String rating) {
        this.title = title;
        this.duration = duration;
        this.genre = genre;
        this.rating = rating;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getMovieId() {
        return id;
    }

    @Override
    public String toString() {
        return title + " (" + duration + " min)";
    }
}