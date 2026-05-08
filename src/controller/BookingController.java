package controller;

import model.Booking;
import model.Schedule;
import model.Seat;
import model.User;
import service.BookingService;
import service.ScheduleService;
import service.PricingService;
import exception.DatabaseException;
import java.util.List;

public class BookingController {
    private BookingService bookingService;
    private ScheduleService scheduleService;
    private PricingService pricingService;

    public BookingController() {
        this.bookingService = new BookingService();
        this.scheduleService = new ScheduleService();
        this.pricingService = new PricingService();
    }

    public Booking createBooking(User user, Schedule schedule, List<Seat> seats,
                                 List<String> ticketTypes, String customerName,
                                 String customerPhone, String paymentMethod)
            throws DatabaseException {
        return bookingService.createBooking(user, schedule, seats, ticketTypes,
                customerName, customerPhone, paymentMethod);
    }

    public boolean cancelBooking(int bookingId) throws DatabaseException {
        return bookingService.cancelBooking(bookingId);
    }

    public Booking getBookingDetails(int bookingId) throws DatabaseException {
        return bookingService.getBookingDetails(bookingId);
    }

    public List<Schedule> getAllSchedules() throws DatabaseException {
        return scheduleService.getAllSchedules();
    }

    public Schedule getScheduleById(int id) throws DatabaseException {
        return scheduleService.getScheduleById(id);
    }
}