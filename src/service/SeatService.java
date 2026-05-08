package service;

import model.Seat;
import dao.SeatDAO;
import exception.DatabaseException;
import java.util.List;
import java.util.stream.Collectors;

public class SeatService {
    private SeatDAO seatDAO;

    public SeatService() {
        this.seatDAO = new SeatDAO();
    }

    public List<Seat> getSeatsByScreen(int screenId) throws DatabaseException {
        return seatDAO.getSeatsByScreen(screenId);
    }

    public List<Seat> getAvailableSeats(int scheduleId, int screenId) throws DatabaseException {
        List<Seat> allSeats = seatDAO.getSeatsByScreen(screenId);
        List<Integer> bookedSeatIds = seatDAO.getBookedSeatIdsForSchedule(scheduleId);

        return allSeats.stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
    }

    public Seat getSeatById(int id) throws DatabaseException {
        return seatDAO.getSeatById(id);
    }

    public List<Seat> getSeatsByIds(List<Integer> seatIds) throws DatabaseException {
        return seatDAO.getSeatsByIds(seatIds);
    }
}