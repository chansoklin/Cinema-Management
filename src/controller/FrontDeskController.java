package controller;

import model.Booking;
import model.Schedule;
import model.Seat;
import model.User;
import model.Movie;
import service.TicketService;
import service.BookingService;
import service.MovieService;
import service.ScheduleService;
import service.PricingService;
import exception.DatabaseException;
import java.util.List;

public class FrontDeskController {
    private TicketService ticketService;
    private BookingService bookingService;
    private MovieService movieService;
    private ScheduleService scheduleService;
    private PricingService pricingService;

    public FrontDeskController() {
        this.ticketService = new TicketService();
        this.bookingService = new BookingService();
        this.movieService = new MovieService();
        this.scheduleService = new ScheduleService();
        this.pricingService = new PricingService();
    }

    public Booking sellTicket(User cashier, Schedule schedule, List<Seat> seats,
                              List<String> ticketTypes, String customerName,
                              String customerPhone, String paymentMethod)
            throws DatabaseException {
        return bookingService.createBooking(cashier, schedule, seats, ticketTypes,
                customerName, customerPhone, paymentMethod);
    }

    public boolean processRefund(int bookingId) throws DatabaseException {
        return bookingService.cancelBooking(bookingId);
    }

    public List<Schedule> getTodayShows() throws DatabaseException {
        return scheduleService.getAllSchedules();
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieService.getAllMovies();
    }

    public double calculatePrice(Schedule schedule, Seat seat, String ticketType) {
        return pricingService.calculateTicketPrice(schedule, seat, ticketType);
    }
}