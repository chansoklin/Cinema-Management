package panels;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import model.*;
import service.*;
import exception.DatabaseException;

public class ShowtimeSelectionPanel extends JPanel {
    private Movie movie;
    private JComboBox<String> dateCombo;
    private JComboBox<String> timeCombo;
    private JComboBox<String> screenCombo;
    private Schedule selectedSchedule;
    private List<Schedule> availableSchedules;
    private JLabel statusLabel;

    public ShowtimeSelectionPanel(Movie movie) {
        this.movie = movie;
        this.availableSchedules = new ArrayList<>();
        initComponents();
        loadShowtimes();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Select Showtime");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel movieLabel = new JLabel("Movie: " + movie.getTitle());
        movieLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 1;
        add(movieLabel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Select Date:"), gbc);
        gbc.gridx = 1;
        dateCombo = new JComboBox<>();
        dateCombo.setPreferredSize(new Dimension(200, 30));
        dateCombo.addActionListener(e -> loadTimesForDate());
        add(dateCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Select Time:"), gbc);
        gbc.gridx = 1;
        timeCombo = new JComboBox<>();
        timeCombo.setPreferredSize(new Dimension(200, 30));
        timeCombo.addActionListener(e -> updateSelectedSchedule());
        add(timeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Screen:"), gbc);
        gbc.gridx = 1;
        screenCombo = new JComboBox<>();
        screenCombo.setPreferredSize(new Dimension(200, 30));
        screenCombo.setEnabled(false);
        add(screenCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("Loading available showtimes...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(Color.GRAY);
        add(statusLabel, gbc);
    }

    private void loadShowtimes() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    ScheduleService scheduleService = new ScheduleService();
                    List<Schedule> allSchedules = scheduleService.getAllSchedules();

                    availableSchedules.clear();
                    for (Schedule schedule : allSchedules) {
                        if (schedule.getMovie() != null && schedule.getMovie().getTitle().equals(movie.getTitle())) {
                            availableSchedules.add(schedule);
                        }
                    }

                    // If no schedules found, create demo schedules for this movie
                    if (availableSchedules.isEmpty()) {
                        createDemoSchedules();
                    }

                } catch (DatabaseException e) {
                    createDemoSchedules();
                }
                return null;
            }

            @Override
            protected void done() {
                if (availableSchedules.isEmpty()) {
                    statusLabel.setText("No showtimes available for this movie");
                    dateCombo.addItem("No dates available");
                    timeCombo.addItem("No times available");
                } else {
                    loadDates();
                    statusLabel.setText("Found " + availableSchedules.size() + " showtimes");
                }
            }
        };
        worker.execute();
    }

    private void createDemoSchedules() {
        // Create demo schedules for this movie for the next 7 days
        String[] times = {"10:00", "13:00", "16:00", "19:00", "22:00"};
        double[] prices = {12.00, 12.00, 14.00, 15.00, 12.00};

        for (int day = 0; day < 7; day++) {
            for (int t = 0; t < times.length; t++) {
                Schedule demo = new Schedule();
                Movie mockMovie = new Movie();
                mockMovie.setTitle(movie.getTitle());
                demo.setMovie(mockMovie);
                demo.setMovieId(movie.getId());
                demo.setScreenName("Screen " + ((t % 3) + 1));
                demo.setScreenId((t % 3) + 1);
                demo.setBasePrice(prices[t]);

                // Create timestamp
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_YEAR, day);
                String[] hourMin = times[t].split(":");
                cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(hourMin[0]));
                cal.set(java.util.Calendar.MINUTE, Integer.parseInt(hourMin[1]));
                demo.setStartTime(new Timestamp(cal.getTimeInMillis()));

                // Calculate end time
                cal.add(java.util.Calendar.MINUTE, movie.getDuration());
                demo.setEndTime(new Timestamp(cal.getTimeInMillis()));

                availableSchedules.add(demo);
            }
        }
    }

    private void loadDates() {
        Set<String> dates = new LinkedHashSet<>();
        for (Schedule schedule : availableSchedules) {
            String dateStr = schedule.getStartTime().toLocalDateTime().toLocalDate().toString();
            dates.add(dateStr);
        }

        dateCombo.removeAllItems();
        for (String date : dates) {
            dateCombo.addItem(date);
        }

        if (dateCombo.getItemCount() > 0) {
            loadTimesForDate();
        }
    }

    private void loadTimesForDate() {
        timeCombo.removeAllItems();
        screenCombo.removeAllItems();

        String selectedDate = (String) dateCombo.getSelectedItem();
        if (selectedDate == null) return;

        for (Schedule schedule : availableSchedules) {
            String scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate().toString();
            if (scheduleDate.equals(selectedDate)) {
                String timeStr = schedule.getStartTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                timeCombo.addItem(timeStr + " - $" + schedule.getBasePrice());
            }
        }

        if (timeCombo.getItemCount() == 0) {
            timeCombo.addItem("No shows available for this date");
        }

        updateSelectedSchedule();
    }

    private void updateSelectedSchedule() {
        int selectedIndex = timeCombo.getSelectedIndex();
        String selectedDate = (String) dateCombo.getSelectedItem();

        if (selectedIndex >= 0 && selectedDate != null) {
            int count = 0;
            for (Schedule schedule : availableSchedules) {
                String scheduleDate = schedule.getStartTime().toLocalDateTime().toLocalDate().toString();
                if (scheduleDate.equals(selectedDate)) {
                    if (count == selectedIndex) {
                        selectedSchedule = schedule;
                        screenCombo.removeAllItems();
                        screenCombo.addItem(schedule.getScreenName());
                        break;
                    }
                    count++;
                }
            }
        }
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
}