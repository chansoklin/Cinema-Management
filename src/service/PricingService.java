package service;

import model.Schedule;
import model.Seat;
import util.Constants;
import java.time.LocalDateTime;

public class PricingService {

    public double calculateTicketPrice(Schedule schedule, Seat seat, String ticketType) {
        double price = schedule.getBasePrice();

        // Apply seat type multiplier
        if (seat != null && Constants.SEAT_VIP.equals(seat.getSeatType())) {
            price *= Constants.VIP_MULTIPLIER;
        }

        // Apply ticket type multiplier
        switch (ticketType) {
            case Constants.TICKET_CHILD:
                price *= Constants.CHILD_MULTIPLIER;
                break;
            case Constants.TICKET_SENIOR:
                price *= Constants.SENIOR_MULTIPLIER;
                break;
            default:
                price *= Constants.ADULT_MULTIPLIER;
        }

        // Apply peak hour surcharge
        if (isPeakHour(schedule.getStartTime())) {
            price *= 1.2;
        }

        return Math.round(price * 100.0) / 100.0;
    }

    private boolean isPeakHour(java.sql.Timestamp timestamp) {
        if (timestamp == null) return false;
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        int hour = dateTime.getHour();
        return hour >= Constants.PEAK_HOUR_START && hour < Constants.PEAK_HOUR_END;
    }
}