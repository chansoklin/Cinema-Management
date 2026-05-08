package dao;

import model.Ticket;
import model.Seat;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO extends BaseDAO {

    public boolean createTicket(Ticket ticket, Connection conn) throws DatabaseException {
        String sql = "INSERT INTO tickets (booking_id, seat_id, price, ticket_type) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ticket.getBookingId());
            ps.setInt(2, ticket.getSeatId());
            ps.setDouble(3, ticket.getPrice());
            ps.setString(4, ticket.getTicketType());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            handleException("Error creating ticket", e);
            return false;
        }
    }

    public List<Ticket> getTicketsByBooking(int bookingId) throws DatabaseException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, s.screen_id, s.seat_row, s.seat_number, s.seat_type " +
                "FROM tickets t " +
                "JOIN seats s ON t.seat_id = s.id " +
                "WHERE t.booking_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ticket ticket = extractTicketFromResultSet(rs);

                Seat seat = new Seat();
                seat.setId(rs.getInt("seat_id"));
                seat.setScreenId(rs.getInt("screen_id"));
                seat.setRow(rs.getInt("seat_row"));
                seat.setNumber(rs.getInt("seat_number"));
                seat.setSeatType(rs.getString("seat_type"));
                ticket.setSeat(seat);

                tickets.add(ticket);
            }
        } catch (SQLException e) {
            handleException("Error getting tickets by booking", e);
        }
        return tickets;
    }

    // Add missing methods
    public boolean sellTicket(Ticket ticket) throws DatabaseException {
        String sql = "INSERT INTO tickets (booking_id, seat_id, price, ticket_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticket.getBookingId());
            ps.setInt(2, ticket.getSeatId());
            ps.setDouble(3, ticket.getPrice());
            ps.setString(4, ticket.getTicketType());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error selling ticket", e);
            return false;
        }
    }

    public boolean refundTicket(int ticketId) throws DatabaseException {
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error refunding ticket", e);
            return false;
        }
    }

    public List<Ticket> getAllTickets() throws DatabaseException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, s.screen_id, s.seat_row, s.seat_number, s.seat_type " +
                "FROM tickets t " +
                "JOIN seats s ON t.seat_id = s.id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = extractTicketFromResultSet(rs);

                Seat seat = new Seat();
                seat.setId(rs.getInt("seat_id"));
                seat.setScreenId(rs.getInt("screen_id"));
                seat.setRow(rs.getInt("seat_row"));
                seat.setNumber(rs.getInt("seat_number"));
                seat.setSeatType(rs.getString("seat_type"));
                ticket.setSeat(seat);

                tickets.add(ticket);
            }
        } catch (SQLException e) {
            handleException("Error getting all tickets", e);
        }
        return tickets;
    }

    public boolean cancelTicketsForBooking(int bookingId) throws DatabaseException {
        String sql = "DELETE FROM tickets WHERE booking_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            handleException("Error cancelling tickets", e);
            return false;
        }
    }

    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setBookingId(rs.getInt("booking_id"));
        ticket.setSeatId(rs.getInt("seat_id"));
        ticket.setPrice(rs.getDouble("price"));
        ticket.setTicketType(rs.getString("ticket_type"));
        return ticket;
    }
}