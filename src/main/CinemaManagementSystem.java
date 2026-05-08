package main;

import controller.*;
import service.*;

public class CinemaManagementSystem {
    private static CinemaManagementSystem instance;

    private UserService userService;
    private MovieService movieService;
    private BookingService bookingService;
    private TicketService ticketService;
    private ScheduleService scheduleService;
    private PricingService pricingService;

    private CinemaManagementSystem() {
        this.userService = new UserService();
        this.movieService = new MovieService();
        this.bookingService = new BookingService();
        this.ticketService = new TicketService();
        this.scheduleService = new ScheduleService();
        this.pricingService = new PricingService();
    }

    public static CinemaManagementSystem getInstance() {
        if (instance == null) {
            instance = new CinemaManagementSystem();
        }
        return instance;
    }

    // Getters for services
    public UserService getUserService() {
        return userService;
    }

    public MovieService getMovieService() {
        return movieService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public TicketService getTicketService() {
        return ticketService;
    }

    public ScheduleService getScheduleService() {
        return scheduleService;
    }

    public PricingService getPricingService() {
        return pricingService;
    }

    public void start() {
        Main.main(new String[]{});
    }
}