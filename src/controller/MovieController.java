package controller;

import model.Movie;
import service.MovieService;
import exception.DatabaseException;
import exception.ValidationException;
import java.util.List;

public class MovieController {
    private MovieService movieService;

    public MovieController() {
        this.movieService = new MovieService();
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieService.getAllMovies();
    }

    public Movie getMovieById(int id) throws DatabaseException {
        return movieService.getMovieById(id);
    }

    public boolean addMovie(Movie movie) throws DatabaseException, ValidationException {
        return movieService.createMovie(movie);
    }

    public boolean updateMovie(Movie movie) throws DatabaseException, ValidationException {
        return movieService.updateMovie(movie);
    }

    public boolean deleteMovie(int id) throws DatabaseException {
        return movieService.deleteMovie(id);
    }

    public List<Movie> searchMovies(String keyword) throws DatabaseException {
        return movieService.searchMovies(keyword);
    }
}