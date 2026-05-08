package service;

import model.Movie;
import dao.MovieDAO;
import exception.DatabaseException;
import exception.ValidationException;
import util.ValidationUtil;
import java.util.List;

public class MovieService {
    private MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieDAO.getAllMovies();
    }

    public List<Movie> getMovies() throws DatabaseException {
        return movieDAO.getAllMovies();
    }

    public Movie getMovieById(int id) throws DatabaseException {
        return movieDAO.getMovieById(id);
    }

    public boolean createMovie(Movie movie) throws DatabaseException, ValidationException {
        validateMovie(movie);
        return movieDAO.createMovie(movie);
    }

    public boolean addMovie(Movie movie) throws DatabaseException, ValidationException {
        return createMovie(movie);
    }

    public boolean updateMovie(Movie movie) throws DatabaseException, ValidationException {
        validateMovie(movie);
        return movieDAO.updateMovie(movie);
    }

    public boolean deleteMovie(int id) throws DatabaseException {
        return movieDAO.deleteMovie(id);
    }

    public List<Movie> searchMovies(String keyword) throws DatabaseException {
        return movieDAO.searchMovies(keyword);
    }
    // In MovieService.java
    public Movie getMovieByTitle(String title) throws DatabaseException {
        return movieDAO.getMovieByTitle(title);
    }

    private void validateMovie(Movie movie) throws ValidationException {
        ValidationUtil.validateNotEmpty(movie.getTitle(), "Movie title");
        ValidationUtil.validatePositiveNumber(movie.getDuration(), "Duration");
    }
}