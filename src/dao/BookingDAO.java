package dao;

import model.Booking;
import model.Ticket;
import exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO extends BaseDAO {

    public Booking createBooking(Booking booking) throws DatabaseException {
        String sql = "INSERT INTO bookings (user_id, schedule_id, total_amount, status, payment_method, customer_name, customer_phone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (booking.getUserId() > 0) {
                ps.setInt(1, booking.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, booking.getScheduleId());
            ps.setDouble(3, booking.getTotalAmount());
            ps.setString(4, booking.getStatus());
            ps.setString(5, booking.getPaymentMethod());
            ps.setString(6, booking.getCustomerName());
            ps.setString(7, booking.getCustomerPhone());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    booking.setId(rs.getInt(1));
                }

                // Create tickets
                TicketDAO ticketDAO = new TicketDAO();
                for (Ticket ticket : booking.getTickets()) {
                    ticket.setBookingId(booking.getId());
                    ticketDAO.createTicket(ticket, conn);
                }

                conn.commit();
                return booking;
            }

            conn.rollback();
            return null;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            handleException("Error creating booking", e);
            return null;
        } finally {
            closeResources(rs, ps);
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Booking getBookingById(int id) throws DatabaseException {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);

                // Load tickets
                TicketDAO ticketDAO = new TicketDAO();
                booking.setTickets(ticketDAO.getTicketsByBooking(id));

                return booking;
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting booking by id", e);
            return null;
        }
    }

    public List<Booking> getBookingsByUser(int userId) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting bookings by user", e);
        }
        return bookings;
    }

    public List<Booking> getBookingsBySchedule(int scheduleId) throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE schedule_id = ? AND status != 'CANCELLED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting bookings by schedule", e);
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId) throws DatabaseException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error cancelling booking", e);
            return false;
        }
    }

    public List<Integer> getBookedSeats(int scheduleId) throws DatabaseException {
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
            handleException("Error getting booked seats", e);
        }
        return bookedSeats;
    }

    // ========== METHODS FOR MANAGER PANEL ==========

    /**
     * Get revenue for a specific date
     */
    public double getRevenueByDate(String date) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings " +
                "WHERE DATE(booking_time) = ? AND status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting revenue by date", e);
            return 0;
        }
    }

    /**
     * Get tickets count for a specific date
     */
    public int getTicketsCountByDate(String date) throws DatabaseException {
        String sql = "SELECT COUNT(t.id) FROM bookings b " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE DATE(b.booking_time) = ? AND b.status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting tickets count by date", e);
            return 0;
        }
    }

    /**
     * Get bookings count for a specific date
     */
    public int getBookingsCountByDate(String date) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE DATE(booking_time) = ? AND status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting bookings count by date", e);
            return 0;
        }
    }

    /**
     * Get total revenue all time
     */
    public double getTotalRevenue() throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings WHERE status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting total revenue", e);
            return 0;
        }
    }

    /**
     * Get revenue since a specific timestamp
     */
    public double getRevenueSince(Timestamp since) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings " +
                "WHERE booking_time >= ? AND status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, since);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting revenue since date", e);
            return 0;
        }
    }

    /**
     * Get total tickets count all time
     */
    public int getTotalTicketsCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM tickets t " +
                "JOIN bookings b ON t.booking_id = b.id " +
                "WHERE b.status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting total tickets count", e);
            return 0;
        }
    }

    /**
     * Get booked seats count for a specific schedule
     */
    public int getBookedSeatsCount(int scheduleId) throws DatabaseException {
        String sql = "SELECT COUNT(DISTINCT t.seat_id) FROM bookings b " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE b.schedule_id = ? AND b.status = 'CONFIRMED'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            handleException("Error getting booked seats count", e);
            return 0;
        }
    }

    /**
     * Get the most popular movie
     */
    public String getMostPopularMovie() throws DatabaseException {
        String sql = "SELECT m.title, COUNT(*) as booking_count " +
                "FROM bookings b " +
                "JOIN schedules s ON b.schedule_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "WHERE b.status = 'CONFIRMED' " +
                "GROUP BY m.id, m.title " +
                "ORDER BY booking_count DESC LIMIT 1";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("title");
            }
            return null;
        } catch (SQLException e) {
            handleException("Error getting most popular movie", e);
            return null;
        }
    }

    /**
     * Get top N movies by tickets sold
     */
    public List<Object[]> getTopMovies(int limit) throws DatabaseException {
        List<Object[]> topMovies = new ArrayList<>();
        String sql = "SELECT m.title, COUNT(t.id) as tickets_sold, COALESCE(SUM(t.price), 0) as revenue " +
                "FROM bookings b " +
                "JOIN schedules s ON b.schedule_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE b.status = 'CONFIRMED' " +
                "GROUP BY m.id, m.title " +
                "ORDER BY tickets_sold DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] movie = new Object[]{
                        rs.getString("title"),
                        rs.getInt("tickets_sold"),
                        String.format("%.2f", rs.getDouble("revenue"))
                };
                topMovies.add(movie);
            }
        } catch (SQLException e) {
            handleException("Error getting top movies", e);
        }
        return topMovies;
    }

    /**
     * Get recent bookings for display
     */
    public List<Object[]> getRecentBookings(int limit) throws DatabaseException {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.id, b.customer_name, m.title, DATE_FORMAT(s.start_time, '%H:%i') as show_time, " +
                "COUNT(t.id) as ticket_count, b.total_amount, b.status " +
                "FROM bookings b " +
                "JOIN schedules s ON b.schedule_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "WHERE b.status != 'CANCELLED' " +
                "GROUP BY b.id, b.customer_name, m.title, s.start_time, b.total_amount, b.status " +
                "ORDER BY b.booking_time DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] booking = new Object[]{
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("title"),
                        rs.getString("show_time"),
                        rs.getInt("ticket_count"),
                        "$" + String.format("%.2f", rs.getDouble("total_amount")),
                        rs.getString("status")
                };
                bookings.add(booking);
            }
        } catch (SQLException e) {
            handleException("Error getting recent bookings", e);
        }
        return bookings;
    }

    /**
     * Get recent sales feed for manager panel
     */
    public List<Object[]> getRecentSales(int limit) throws DatabaseException {
        List<Object[]> sales = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(b.booking_time, '%H:%i') as sale_time, " +
                "m.title, COUNT(t.id) as ticket_count, b.total_amount, " +
                "COALESCE(u.name, 'Walk-in') as staff_name " +
                "FROM bookings b " +
                "JOIN schedules s ON b.schedule_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN tickets t ON b.id = t.booking_id " +
                "LEFT JOIN users u ON b.user_id = u.id " +
                "WHERE b.status = 'CONFIRMED' " +
                "GROUP BY b.id, sale_time, m.title, b.total_amount, staff_name " +
                "ORDER BY b.booking_time DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] sale = new Object[]{
                        rs.getString("sale_time"),
                        rs.getString("title"),
                        rs.getInt("ticket_count"),
                        "$" + String.format("%.2f", rs.getDouble("total_amount")),
                        rs.getString("staff_name")
                };
                sales.add(sale);
            }
        } catch (SQLException e) {
            handleException("Error getting recent sales", e);
        }
        return sales;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setScheduleId(rs.getInt("schedule_id"));
        booking.setBookingTime(rs.getTimestamp("booking_time"));
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setStatus(rs.getString("status"));
        booking.setPaymentMethod(rs.getString("payment_method"));
        booking.setCustomerName(rs.getString("customer_name"));
        booking.setCustomerPhone(rs.getString("customer_phone"));
        booking.setHoldExpiry(rs.getTimestamp("hold_expiry"));
        return booking;
    }
}