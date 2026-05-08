package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int id;
    private int userId;
    private int scheduleId;
    private Timestamp bookingTime;
    private double totalAmount;
    private String status;
    private String paymentMethod;
    private String customerName;
    private String customerPhone;
    private Timestamp holdExpiry;

    private List<Ticket> tickets;
    private Schedule schedule;

    public Booking() {
        this.tickets = new ArrayList<>();
        this.bookingTime = new Timestamp(System.currentTimeMillis());
        this.status = "CONFIRMED";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(Timestamp bookingTime) { this.bookingTime = bookingTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Timestamp getHoldExpiry() { return holdExpiry; }
    public void setHoldExpiry(Timestamp holdExpiry) { this.holdExpiry = holdExpiry; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}