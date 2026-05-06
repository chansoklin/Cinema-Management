package manager;

public class
Movie {
    private String id;
    private String title;
    private String director;
    private String actors;
    private String description;  // Consistent field name
    private int duration;
    private double rating;
    private boolean isScreening;
    private String movieTitles;
    private String plotSummary;

    public Movie(String id, String title, String director, String actors,
                 int duration, double rating, String text, boolean isScreening) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.actors = actors;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.isScreening = isScreening;
    }


    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isScreening() {
        return isScreening;
    }

    public void setScreening(boolean screening) {
        isScreening = screening;
    }

    public String getId() {
        return id;
    }

    public String getPlotSummary() { return plotSummary; }
    public void setPlotSummary(String plotSummary) { this.plotSummary = plotSummary; }
}
