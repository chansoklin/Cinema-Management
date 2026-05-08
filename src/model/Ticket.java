package model;

public class Ticket {
    private int id;
    private int bookingId;
    private int seatId;
    private double price;
    private String ticketType;

    private Seat seat;
    private Booking booking;

    public Ticket() {}

    public Ticket(int seatId, double price, String ticketType) {
        this.seatId = seatId;
        this.price = price;
        this.ticketType = ticketType;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getSeatId() {
        return seatId;
    }

    public double getPrice() {
        return price;
    }

    public String getTicketType() {
        return ticketType;
    }

    public Seat getSeat() {
        return seat;
    }

    public Booking getBooking() {
        return booking;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    // Add this method to Ticket.java
    public int getMovieId() {
        if (booking != null && booking.getSchedule() != null && booking.getSchedule().getMovie() != null) {
            return booking.getSchedule().getMovie().getId();
        }
        return 0;
    }
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", seat=" + (seat != null ? seat.getSeatNumber() : seatId) +
                ", price=" + price +
                ", type=" + ticketType +
                '}';
    }
}