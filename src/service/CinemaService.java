package service;

import exception.DatabaseException;
import model.Ticket;
import model.Seat;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CinemaService {

    private final TicketService ticketService = new TicketService();
    private final BookingService bookingService = new BookingService();

    public List<String> getSoldSeats(String scheduleId) {
        try {
            int scheduleIdInt = Integer.parseInt(scheduleId);
            // Get booked seat IDs for this schedule
            List<Integer> bookedSeatIds = bookingService.getBookedSeats(scheduleIdInt);

            // Convert to seat numbers (e.g., "A1", "B5")
            List<String> soldSeats = new ArrayList<>();
            // You would need to map seat IDs to seat numbers
            // This is a simplified version
            for (Integer seatId : bookedSeatIds) {
                soldSeats.add("Seat " + seatId);
            }
            return soldSeats;
        } catch (NumberFormatException | DatabaseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getAvailableSeats(String scheduleId) {
        try {
            int scheduleIdInt = Integer.parseInt(scheduleId);
            // This would return available seats
            // Implementation depends on your seat management logic
            return new ArrayList<>();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}