package dao;

import model.Movie;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO extends BaseDAO {

    public List<Movie> getAllMovies() throws DatabaseException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE is_active = true ORDER BY title";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting all movies", e);
        }
        return movies;
    }

    public Movie getMovieById(int id) throws DatabaseException {
        String sql = "SELECT * FROM movies WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractMovieFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting movie by id", e);
            return null;
        }
    }

    public boolean createMovie(Movie movie) throws DatabaseException {
        String sql = "INSERT INTO movies (title, duration, genre, rating, description, poster_url) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, movie.getTitle());
            ps.setInt(2, movie.getDuration());
            ps.setString(3, movie.getGenre());
            ps.setString(4, movie.getRating());
            ps.setString(5, movie.getDescription());
            ps.setString(6, movie.getPosterUrl());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    movie.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            handleException("Error creating movie", e);
            return false;
        }
    }

    public boolean updateMovie(Movie movie) throws DatabaseException {
        String sql = "UPDATE movies SET title = ?, duration = ?, genre = ?, rating = ?, description = ?, poster_url = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movie.getTitle());
            ps.setInt(2, movie.getDuration());
            ps.setString(3, movie.getGenre());
            ps.setString(4, movie.getRating());
            ps.setString(5, movie.getDescription());
            ps.setString(6, movie.getPosterUrl());
            ps.setInt(7, movie.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error updating movie", e);
            return false;
        }
    }

    public boolean deleteMovie(int id) throws DatabaseException {
        String sql = "UPDATE movies SET is_active = false WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting movie", e);
            return false;
        }
    }

    public List<Movie> searchMovies(String keyword) throws DatabaseException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE title LIKE ? AND is_active = true";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                movies.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error searching movies", e);
        }
        return movies;
    }

    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setDuration(rs.getInt("duration"));
        movie.setGenre(rs.getString("genre"));
        movie.setRating(rs.getString("rating"));
        movie.setDescription(rs.getString("description"));
        movie.setPosterUrl(rs.getString("poster_url"));
        movie.setActive(rs.getBoolean("is_active"));
        return movie;
    }
    // Add to MovieDAO.java
    public Movie getMovieByTitle(String title) throws DatabaseException {
        String sql = "SELECT * FROM movies WHERE title = ? AND is_active = true";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractMovieFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting movie by title", e);
            return null;
        }
    }
}