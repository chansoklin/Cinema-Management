package util;

public class Constants {
    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_FRONT_DESK = "FRONT_DESK";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    // Ticket Status
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_REFUNDED = "REFUNDED";

    // Seat Types
    public static final String SEAT_VIP = "VIP";
    public static final String SEAT_REGULAR = "REGULAR";

    // Ticket Types
    public static final String TICKET_ADULT = "ADULT";
    public static final String TICKET_CHILD = "CHILD";
    public static final String TICKET_SENIOR = "SENIOR";

    // Ticket Type Multipliers
    public static final double ADULT_MULTIPLIER = 1.0;
    public static final double CHILD_MULTIPLIER = 0.7;
    public static final double SENIOR_MULTIPLIER = 0.8;
    public static final double VIP_MULTIPLIER = 1.5;

    // Business Rules
    public static final int BOOKING_TIMEOUT_MINUTES = 10;
    public static final int MAX_TICKETS_PER_BOOKING = 10;
    public static final double REFUND_PENALTY_PERCENTAGE = 0.2;

    // Peak Hours
    public static final int PEAK_HOUR_START = 18;
    public static final int PEAK_HOUR_END = 22;
}