package panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Schedule;
import service.BookingService;
import service.ScheduleService;
import exception.DatabaseException;

public class TodaySchedulePanel extends JPanel {
    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;
    private BookingService bookingService;
    private ScheduleService scheduleService;
    private JLabel summaryLabel;
    private JComboBox<String> dateSelector;
    private JLabel refreshTimeLabel;
    private Timer refreshTimer;

    public TodaySchedulePanel() {
        this.bookingService = new BookingService();
        this.scheduleService = new ScheduleService();
        initComponents();
        loadSchedule();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Header Panel with Title and Controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("📅 Today's Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Date selector for viewing different days
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(Color.WHITE);

        controlPanel.add(new JLabel("View:"));
        dateSelector = new JComboBox<>(new String[]{"Today", "Tomorrow", "This Week"});
        dateSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateSelector.addActionListener(e -> loadSchedule());
        controlPanel.add(dateSelector);

        JButton refreshBtn = createButton("🔄 Refresh", new Color(41, 128, 185));
        refreshBtn.addActionListener(e -> loadSchedule());
        controlPanel.add(refreshBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Time", "Movie", "Screen", "Sold", "Available", "Occupancy", "Status"};
        scheduleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(scheduleModel);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scheduleTable.setRowHeight(35);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        scheduleTable.getTableHeader().setBackground(new Color(52, 73, 94));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);
        scheduleTable.setSelectionBackground(new Color(41, 128, 185, 50));
        scheduleTable.setShowGrid(false);
        scheduleTable.setIntercellSpacing(new Dimension(0, 0));

        // Set column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Custom cell renderers
        scheduleTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        scheduleTable.getColumnModel().getColumn(5).setCellRenderer(new OccupancyCellRenderer());

        // Zebra striping
        scheduleTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        // Footer Panel with Summary
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        summaryLabel.setForeground(Color.GRAY);

        refreshTimeLabel = new JLabel();
        refreshTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        refreshTimeLabel.setForeground(Color.GRAY);

        footerPanel.add(summaryLabel, BorderLayout.WEST);
        footerPanel.add(refreshTimeLabel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    public void loadSchedule() {
        scheduleModel.setRowCount(0);

        String selectedView = (String) dateSelector.getSelectedItem();

        try {
            List<Schedule> schedules;

            if ("Today".equals(selectedView)) {
                schedules = scheduleService.getTodaySchedules();
            } else if ("Tomorrow".equals(selectedView)) {
                schedules = scheduleService.getSchedulesByDate(LocalDate.now().plusDays(1));
            } else {
                schedules = scheduleService.getSchedulesBetweenDates(LocalDate.now(), LocalDate.now().plusDays(7));
            }

            int totalShows = 0;
            int totalSold = 0;
            int totalAvailable = 0;

            for (Schedule schedule : schedules) {
                int bookedSeats = bookingService.getBookedSeats(schedule.getId()).size();
                int capacity = 150;
                int available = capacity - bookedSeats;
                double occupancyPercent = (double) bookedSeats / capacity * 100;

                String status;
                if (bookedSeats > 100) {
                    status = "🔴 Almost Full";
                } else if (bookedSeats > 50) {
                    status = "🟡 Selling Fast";
                } else {
                    status = "🟢 Available";
                }

                String occupancyDisplay = String.format("%.0f%%", occupancyPercent);

                scheduleModel.addRow(new Object[]{
                        formatTime(schedule.getStartTime()),
                        schedule.getMovie().getTitle(),
                        schedule.getScreenName(),
                        bookedSeats,
                        available,
                        occupancyDisplay,
                        status
                });

                totalShows++;
                totalSold += bookedSeats;
                totalAvailable += available;
            }

            // Update summary
            double avgOccupancy = totalShows > 0 ? (double) totalSold / (totalSold + totalAvailable) * 100 : 0;
            summaryLabel.setText(String.format("📊 %d shows • %d tickets sold • %d seats available • %.1f%% average occupancy",
                    totalShows, totalSold, totalAvailable, avgOccupancy));

            refreshTimeLabel.setText("Last updated: " +
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        } catch (DatabaseException e) {
            // Demo data with summary
            loadDemoData();
        }
    }

    private void loadDemoData() {
        scheduleModel.setRowCount(0);

        Object[][] demoData = {
                {"10:00", "Inception", "Screen 1", 45, 105, "30%", "🟢 Available"},
                {"13:00", "The Dark Knight", "Screen 2", 78, 72, "52%", "🟡 Selling Fast"},
                {"16:00", "Interstellar", "Screen 1", 112, 38, "75%", "🔴 Almost Full"},
                {"19:00", "John Wick", "Screen 2", 95, 55, "63%", "🟡 Selling Fast"},
                {"21:00", "The Matrix", "Screen 1", 134, 16, "89%", "🔴 Almost Full"}
        };

        for (Object[] row : demoData) {
            scheduleModel.addRow(row);
        }

        summaryLabel.setText("📊 5 shows • 464 tickets sold • 286 seats available • 62% average occupancy");
        refreshTimeLabel.setText("Last updated: " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(30000, e -> loadSchedule());
        refreshTimer.start();
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";

            if (!isSelected) {
                if (status.contains("🟢")) {
                    c.setBackground(new Color(46, 204, 113, 20));
                    setForeground(new Color(30, 126, 52));
                } else if (status.contains("🟡")) {
                    c.setBackground(new Color(241, 196, 15, 20));
                    setForeground(new Color(184, 134, 11));
                } else if (status.contains("🔴")) {
                    c.setBackground(new Color(231, 76, 60, 20));
                    setForeground(new Color(176, 42, 55));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                setFont(new Font("Segoe UI", Font.BOLD, 11));
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    class OccupancyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value != null) {
                String occupancyStr = value.toString().replace("%", "");
                try {
                    int occupancy = Integer.parseInt(occupancyStr);
                    if (occupancy >= 70) {
                        setForeground(new Color(231, 76, 60));
                    } else if (occupancy >= 40) {
                        setForeground(new Color(241, 196, 15));
                    } else {
                        setForeground(new Color(46, 204, 113));
                    }
                } catch (NumberFormatException e) {
                    setForeground(Color.GRAY);
                }
                setFont(new Font("Segoe UI", Font.BOLD, 11));
            }

            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    public void refresh() {
        loadSchedule();
    }
}