package service;

import model.Ticket;
import model.Booking;
import model.Seat;
import dao.TicketDAO;
import dao.BookingDAO;
import exception.DatabaseException;
import util.Constants;
import java.util.List;
import java.util.ArrayList;

public class TicketService {
    private TicketDAO ticketDAO;
    private BookingDAO bookingDAO;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
        this.bookingDAO = new BookingDAO();
    }

    public boolean sellTicket(Ticket ticket) throws DatabaseException {
        return ticketDAO.sellTicket(ticket);
    }

    public boolean refundTicket(int ticketId) throws DatabaseException {
        return ticketDAO.refundTicket(ticketId);
    }

    public List<Ticket> getAllTickets() throws DatabaseException {
        return ticketDAO.getAllTickets();
    }

    public List<Ticket> getTicketsByBooking(int bookingId) throws DatabaseException {
        return ticketDAO.getTicketsByBooking(bookingId);
    }

    // Add this method to fix the CinemaService error
    public List<String> getSoldSeats(String scheduleId) {
        try {
            int scheduleIdInt = Integer.parseInt(scheduleId);
            List<Integer> bookedSeatIds = bookingDAO.getBookedSeats(scheduleIdInt);
            List<String> soldSeats = new ArrayList<>();

            for (Integer seatId : bookedSeatIds) {
                soldSeats.add(String.valueOf(seatId));
            }
            return soldSeats;
        } catch (NumberFormatException | DatabaseException e) {
            System.err.println("Error getting sold seats: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Alternative method that returns seat objects
    public List<Seat> getSoldSeatsAsSeats(String scheduleId) throws DatabaseException {
        int scheduleIdInt = Integer.parseInt(scheduleId);
        List<Integer> bookedSeatIds = bookingDAO.getBookedSeats(scheduleIdInt);
        List<Seat> soldSeats = new ArrayList<>();

        // You would need a SeatService or SeatDAO to convert IDs to Seat objects
        // For now, return empty list or implement properly
        return soldSeats;
    }

    public double calculateRefundAmount(Ticket ticket, Booking booking) {
        double refundAmount = ticket.getPrice();
        // Apply penalty if cancellation is late
        return refundAmount * (1 - Constants.REFUND_PENALTY_PERCENTAGE);
    }
}