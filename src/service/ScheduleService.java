package service;

import model.Schedule;
import model.Movie;
import dao.ScheduleDAO;
import dao.MovieDAO;
import exception.DatabaseException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleService {
    private ScheduleDAO scheduleDAO;
    private MovieDAO movieDAO;

    public ScheduleService() {
        this.scheduleDAO = new ScheduleDAO();
        this.movieDAO = new MovieDAO();
    }

    public List<Schedule> getAllSchedules() throws DatabaseException {
        List<Schedule> schedules = scheduleDAO.getAllSchedules();
        // Load movie details for each schedule
        for (Schedule schedule : schedules) {
            Movie movie = movieDAO.getMovieById(schedule.getMovieId());
            schedule.setMovie(movie);
        }

        // If no schedules in DB, return demo schedules
        if (schedules.isEmpty()) {
            schedules = createDemoSchedules();
        }

        return schedules;
    }

    private List<Schedule> createDemoSchedules() {
        List<Schedule> demoSchedules = new ArrayList<>();
        String[] movies = {"Inception", "The Dark Knight", "Interstellar", "John Wick", "The Matrix"};
        String[] times = {"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM", "9:00 PM"};
        String[] screens = {"Screen 1", "Screen 2"};

        for (int i = 0; i < movies.length; i++) {
            for (int j = 0; j < 2; j++) {
                Schedule schedule = new Schedule();
                Movie movie = new Movie();
                movie.setTitle(movies[i]);
                schedule.setMovie(movie);
                schedule.setScreenName(screens[j]);
                schedule.setStartTime(new Timestamp(System.currentTimeMillis() + (i * 3600000)));
                schedule.setBasePrice(12.00 + (i * 1.0));
                demoSchedules.add(schedule);
            }
        }
        return demoSchedules;
    }

    public Schedule getScheduleById(int id) throws DatabaseException {
        Schedule schedule = scheduleDAO.getScheduleById(id);
        if (schedule != null) {
            Movie movie = movieDAO.getMovieById(schedule.getMovieId());
            schedule.setMovie(movie);
        }
        return schedule;
    }

    public List<Schedule> getSchedulesByMovie(int movieId) throws DatabaseException {
        return scheduleDAO.getSchedulesByMovie(movieId);
    }

    public boolean createSchedule(Schedule schedule) throws DatabaseException {
        return scheduleDAO.createSchedule(schedule);
    }

    public boolean updateSchedule(Schedule schedule) throws DatabaseException {
        return scheduleDAO.updateSchedule(schedule);
    }

    public boolean deleteSchedule(int id) throws DatabaseException {
        return scheduleDAO.deleteSchedule(id);
    }

    public List<Schedule> getUpcomingSchedules() throws DatabaseException {
        List<Schedule> allSchedules = getAllSchedules();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return allSchedules.stream()
                .filter(s -> s.getStartTime().after(now))
                .collect(Collectors.toList());
    }

    // ========== METHODS FOR MANAGER PANEL ==========

    /**
     * Get today's schedules
     */
    public List<Schedule> getTodaySchedules() throws DatabaseException {
        List<Schedule> allSchedules = getAllSchedules();
        LocalDate today = LocalDate.now();
        List<Schedule> todaySchedules = new ArrayList<>();

        for (Schedule schedule : allSchedules) {
            if (schedule.getStartTime() != null) {
                LocalDate scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate();
                if (scheduleDate.equals(today)) {
                    todaySchedules.add(schedule);
                }
            }
        }
        return todaySchedules;
    }

    /**
     * Get count of currently active shows (now playing)
     */
    public int getActiveShowsCount() throws DatabaseException {
        List<Schedule> schedules = getAllSchedules();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int count = 0;

        for (Schedule schedule : schedules) {
            if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
                if (schedule.getStartTime().before(now) && schedule.getEndTime().after(now)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Get schedules for a specific date
     */
    public List<Schedule> getSchedulesByDate(LocalDate date) throws DatabaseException {
        List<Schedule> allSchedules = getAllSchedules();
        List<Schedule> schedulesByDate = new ArrayList<>();

        for (Schedule schedule : allSchedules) {
            if (schedule.getStartTime() != null) {
                LocalDate scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate();
                if (scheduleDate.equals(date)) {
                    schedulesByDate.add(schedule);
                }
            }
        }
        return schedulesByDate;
    }

    /**
     * Get schedules for a date range
     */
    public List<Schedule> getSchedulesBetweenDates(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        List<Schedule> allSchedules = getAllSchedules();
        List<Schedule> schedulesInRange = new ArrayList<>();

        for (Schedule schedule : allSchedules) {
            if (schedule.getStartTime() != null) {
                LocalDate scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate();
                if (!scheduleDate.isBefore(startDate) && !scheduleDate.isAfter(endDate)) {
                    schedulesInRange.add(schedule);
                }
            }
        }
        return schedulesInRange;
    }

    /**
     * Get total number of schedules
     */
    public int getTotalSchedulesCount() throws DatabaseException {
        return getAllSchedules().size();
    }

    /**
     * Check if a schedule exists at given time and screen
     */
    public boolean isScheduleConflict(int screenId, Timestamp startTime, Timestamp endTime) throws DatabaseException {
        List<Schedule> schedules = getAllSchedules();
        for (Schedule schedule : schedules) {
            if (schedule.getScreenId() == screenId) {
                // Check for time overlap
                if (startTime.before(schedule.getEndTime()) && endTime.after(schedule.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

}