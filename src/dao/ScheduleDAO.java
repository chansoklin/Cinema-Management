package dao;

import model.Schedule;
import model.Movie;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO extends BaseDAO {

    public List<Schedule> getAllSchedules() throws DatabaseException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, sc.name as screen_name " +
                "FROM schedules s " +
                "JOIN screens sc ON s.screen_id = sc.id " +
                "ORDER BY s.start_time";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Schedule schedule = extractScheduleFromResultSet(rs);
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            handleException("Error getting all schedules", e);
        }
        return schedules;
    }

    public Schedule getScheduleById(int id) throws DatabaseException {
        String sql = "SELECT s.*, sc.name as screen_name " +
                "FROM schedules s " +
                "JOIN screens sc ON s.screen_id = sc.id " +
                "WHERE s.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting schedule by id", e);
            return null;
        }
    }

    public List<Schedule> getSchedulesByMovie(int movieId) throws DatabaseException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, sc.name as screen_name " +
                "FROM schedules s " +
                "JOIN screens sc ON s.screen_id = sc.id " +
                "WHERE s.movie_id = ? AND s.start_time > NOW() " +
                "ORDER BY s.start_time";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                schedules.add(extractScheduleFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting schedules by movie", e);
        }
        return schedules;
    }

    public boolean createSchedule(Schedule schedule) throws DatabaseException {
        String sql = "INSERT INTO schedules (movie_id, screen_id, start_time, end_time, base_price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, schedule.getMovieId());
            ps.setInt(2, schedule.getScreenId());
            ps.setTimestamp(3, schedule.getStartTime());
            ps.setTimestamp(4, schedule.getEndTime());
            ps.setDouble(5, schedule.getBasePrice());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    schedule.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            handleException("Error creating schedule", e);
            return false;
        }
    }

    public boolean updateSchedule(Schedule schedule) throws DatabaseException {
        String sql = "UPDATE schedules SET movie_id = ?, screen_id = ?, start_time = ?, end_time = ?, base_price = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, schedule.getMovieId());
            ps.setInt(2, schedule.getScreenId());
            ps.setTimestamp(3, schedule.getStartTime());
            ps.setTimestamp(4, schedule.getEndTime());
            ps.setDouble(5, schedule.getBasePrice());
            ps.setInt(6, schedule.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error updating schedule", e);
            return false;
        }
    }

    public boolean deleteSchedule(int id) throws DatabaseException {
        String sql = "DELETE FROM schedules WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting schedule", e);
            return false;
        }
    }

    public int getBookedSeatsCount(int scheduleId) throws DatabaseException {
        String sql = "SELECT COUNT(DISTINCT t.seat_id) as booked_count " +
                "FROM bookings b " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE b.schedule_id = ? AND b.status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("booked_count");
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting booked seats count", e);
            return 0;
        }
    }

    private Schedule extractScheduleFromResultSet(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setId(rs.getInt("id"));
        schedule.setMovieId(rs.getInt("movie_id"));
        schedule.setScreenId(rs.getInt("screen_id"));
        schedule.setStartTime(rs.getTimestamp("start_time"));
        schedule.setEndTime(rs.getTimestamp("end_time"));
        schedule.setBasePrice(rs.getDouble("base_price"));
        schedule.setScreenName(rs.getString("screen_name"));
        return schedule;
    }
}