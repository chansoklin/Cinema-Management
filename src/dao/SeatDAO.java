package dao;

import model.Seat;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO extends BaseDAO {

    public List<Seat> getSeatsByScreen(int screenId) throws DatabaseException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE screen_id = ? ORDER BY seat_row, seat_number";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, screenId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                seats.add(extractSeatFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting seats by screen", e);
        }
        return seats;
    }

    public Seat getSeatById(int id) throws DatabaseException {
        String sql = "SELECT * FROM seats WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractSeatFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting seat by id", e);
            return null;
        }
    }

    public List<Seat> getSeatsByIds(List<Integer> seatIds) throws DatabaseException {
        List<Seat> seats = new ArrayList<>();
        if (seatIds.isEmpty()) return seats;

        String sql = "SELECT * FROM seats WHERE id IN (" + String.join(",", seatIds.stream().map(String::valueOf).toArray(String[]::new)) + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                seats.add(extractSeatFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting seats by ids", e);
        }
        return seats;
    }

    public List<Integer> getBookedSeatIdsForSchedule(int scheduleId) throws DatabaseException {
        List<Integer> bookedSeats = new ArrayList<>();
        String sql = "SELECT DISTINCT t.seat_id " +
                "FROM bookings b " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE b.schedule_id = ? AND b.status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookedSeats.add(rs.getInt("seat_id"));
            }
        } catch (SQLException e) {
            handleException("Error getting booked seat ids", e);
        }
        return bookedSeats;
    }

    private Seat extractSeatFromResultSet(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setId(rs.getInt("id"));
        seat.setScreenId(rs.getInt("screen_id"));
        seat.setRow(rs.getInt("seat_row"));
        seat.setNumber(rs.getInt("seat_number"));
        seat.setSeatType(rs.getString("seat_type"));
        return seat;
    }
}