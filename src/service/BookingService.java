package service;

import model.*;
import dao.BookingDAO;
import dao.SeatDAO;
import exception.DatabaseException;
import util.Constants;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    private SeatDAO seatDAO;
    private PricingService pricingService;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.seatDAO = new SeatDAO();
        this.pricingService = new PricingService();
    }

    public Booking createBooking(User user, Schedule schedule, List<Seat> seats,
                                 List<String> ticketTypes, String customerName,
                                 String customerPhone, String paymentMethod)
            throws DatabaseException {

        if (seats.size() > Constants.MAX_TICKETS_PER_BOOKING) {
            throw new DatabaseException("Cannot book more than " + Constants.MAX_TICKETS_PER_BOOKING + " tickets");
        }

        Booking booking = new Booking();
        booking.setUserId(user != null ? user.getId() : 0);
        booking.setScheduleId(schedule.getId());
        booking.setCustomerName(customerName);
        booking.setCustomerPhone(customerPhone);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus(Constants.STATUS_CONFIRMED);

        double totalAmount = 0;

        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            String ticketType = ticketTypes.get(i);

            double price = pricingService.calculateTicketPrice(schedule, seat, ticketType);
            totalAmount += price;

            Ticket ticket = new Ticket(seat.getId(), price, ticketType);
            booking.addTicket(ticket);
        }

        booking.setTotalAmount(totalAmount);

        return bookingDAO.createBooking(booking);
    }

    public boolean cancelBooking(int bookingId) throws DatabaseException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            throw new DatabaseException("Booking not found");
        }
        return bookingDAO.cancelBooking(bookingId);
    }

    public Booking getBookingDetails(int bookingId) throws DatabaseException {
        return bookingDAO.getBookingById(bookingId);
    }

    public List<Integer> getBookedSeats(int scheduleId) throws DatabaseException {
        return bookingDAO.getBookedSeats(scheduleId);
    }

    // ========== METHODS FOR MANAGER PANEL DASHBOARD ==========

    /**
     * Get today's total revenue
     */
    public double getTodayRevenue() throws DatabaseException {
        String today = LocalDate.now().toString();
        return bookingDAO.getRevenueByDate(today);
    }

    /**
     * Get today's tickets sold count
     */
    public int getTodayTicketsCount() throws DatabaseException {
        String today = LocalDate.now().toString();
        return bookingDAO.getTicketsCountByDate(today);
    }

    /**
     * Get today's bookings count
     */
    public int getTodayBookingsCount() throws DatabaseException {
        String today = LocalDate.now().toString();
        return bookingDAO.getBookingsCountByDate(today);
    }

    /**
     * Get total revenue (all time)
     */
    public double getTotalRevenue() throws DatabaseException {
        return bookingDAO.getTotalRevenue();
    }

    /**
     * Get weekly revenue (last 7 days)
     */
    public double getWeeklyRevenue() throws DatabaseException {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        Timestamp weekAgo = Timestamp.valueOf(oneWeekAgo);
        return bookingDAO.getRevenueSince(weekAgo);
    }

    /**
     * Get monthly revenue (last 30 days)
     */
    public double getMonthlyRevenue() throws DatabaseException {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        Timestamp monthAgo = Timestamp.valueOf(oneMonthAgo);
        return bookingDAO.getRevenueSince(monthAgo);
    }

    /**
     * Get total tickets sold (all time)
     */
    public int getTotalTicketsSold() throws DatabaseException {
        return bookingDAO.getTotalTicketsCount();
    }

    /**
     * Get average occupancy rate across all schedules
     */
    public double getAverageOccupancyRate() throws DatabaseException {
        ScheduleService scheduleService = new ScheduleService();
        List<Schedule> schedules = scheduleService.getAllSchedules();
        if (schedules.isEmpty()) return 0;

        double totalOccupancy = 0;
        int count = 0;
        for (Schedule schedule : schedules) {
            int bookedSeats = bookingDAO.getBookedSeatsCount(schedule.getId());
            int capacity = 150; // Default capacity, can be fetched from screen
            double occupancy = (double) bookedSeats / capacity * 100;
            totalOccupancy += occupancy;
            count++;
        }
        return count > 0 ? totalOccupancy / count : 0;
    }

    /**
     * Get the most popular movie
     */
    public String getMostPopularMovie() throws DatabaseException {
        return bookingDAO.getMostPopularMovie();
    }

    /**
     * Get top N movies by tickets sold
     */
    public List<Object[]> getTopMovies(int limit) throws DatabaseException {
        return bookingDAO.getTopMovies(limit);
    }

    /**
     * Get recent bookings (last N)
     */
    public List<Object[]> getRecentBookings(int limit) throws DatabaseException {
        return bookingDAO.getRecentBookings(limit);
    }

    /**
     * Get recent sales feed for manager panel
     */
    public List<Object[]> getRecentSales(int limit) throws DatabaseException {
        return bookingDAO.getRecentSales(limit);
    }
}