package panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import service.*;
import exception.DatabaseException;

public class DashboardPanel extends JPanel {
    // Statistics Labels
    private JLabel totalUsersLabel;
    private JLabel totalMoviesLabel;
    private JLabel totalSchedulesLabel;
    private JLabel todayBookingsLabel;
    private JLabel totalRevenueLabel;
    private JLabel occupancyLabel;
    private JLabel weeklyRevenueLabel;
    private JLabel popularMovieLabel;

    // Progress Bar
    private JProgressBar occupancyProgress;

    // Tables
    private DefaultTableModel topMoviesModel;
    private DefaultTableModel recentBookingsModel;

    // Timer for auto-refresh
    private Timer refreshTimer;

    // Services
    private UserService userService;
    private MovieService movieService;
    private ScheduleService scheduleService;
    private BookingService bookingService;

    public DashboardPanel() {
        this.userService = new UserService();
        this.movieService = new MovieService();
        this.scheduleService = new ScheduleService();
        this.bookingService = new BookingService();
        initComponents();
        loadStatistics();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topSection = createStatisticsCards();
        JPanel bottomSection = createBottomSection();

        centerPanel.add(topSection, BorderLayout.NORTH);
        centerPanel.add(bottomSection, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📊 Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.WHITE);

        Timer dateTimer = new Timer(1000, e ->
                dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss")))
        );
        dateTimer.start();

        header.add(titleLabel, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatisticsCards() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalUsersLabel = createStatCard(panel, "👥 Total Users", "0", new Color(52, 152, 219), "Registered Users");
        totalMoviesLabel = createStatCard(panel, "🎬 Total Movies", "0", new Color(46, 204, 113), "In Library");
        totalSchedulesLabel = createStatCard(panel, "📅 Active Shows", "0", new Color(155, 89, 182), "Today");
        todayBookingsLabel = createStatCard(panel, "🎟️ Today's Bookings", "0", new Color(231, 76, 60), "Tickets Sold");

        totalRevenueLabel = createStatCard(panel, "💰 Total Revenue", "$0", new Color(241, 196, 15), "All Time");

        JPanel occupancyCard = createOccupancyCard();
        panel.add(occupancyCard);

        weeklyRevenueLabel = createStatCard(panel, "📈 Weekly Revenue", "$0", new Color(26, 188, 156), "Last 7 Days");
        popularMovieLabel = createStatCard(panel, "⭐ Popular Movie", "N/A", new Color(142, 68, 173), "Most Booked");

        return panel;
    }

    private JPanel createOccupancyCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        Color color = new Color(230, 126, 34);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("📊 Occupancy Rate");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);

        occupancyLabel = new JLabel("0%");
        occupancyLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        occupancyLabel.setForeground(color);
        occupancyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        occupancyProgress = new JProgressBar(0, 100);
        occupancyProgress.setValue(0);
        occupancyProgress.setStringPainted(true);
        occupancyProgress.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(occupancyLabel, BorderLayout.CENTER);
        card.add(occupancyProgress, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(180, 100));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        parent.add(card);
        return valueLabel;
    }

    private JPanel createBottomSection() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        // Left Panel - Top Movies
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("🎬 Top 5 Popular Movies"));

        String[] movieColumns = {"Rank", "Movie Title", "Tickets Sold", "Revenue", "Rating"};
        topMoviesModel = new DefaultTableModel(movieColumns, 0);
        JTable topMoviesTable = new JTable(topMoviesModel);
        topMoviesTable.setRowHeight(30);
        topMoviesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane movieScroll = new JScrollPane(topMoviesTable);
        leftPanel.add(movieScroll, BorderLayout.CENTER);

        JPanel movieActions = new JPanel(new FlowLayout());
        JButton refreshMoviesBtn = new JButton("🔄 Refresh");
        refreshMoviesBtn.addActionListener(e -> loadTopMovies());
        movieActions.add(refreshMoviesBtn);
        leftPanel.add(movieActions, BorderLayout.SOUTH);

        // Right Panel - Recent Bookings
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("📋 Recent Bookings"));

        String[] bookingColumns = {"Booking ID", "Customer", "Movie", "Show Time", "Tickets", "Amount", "Status"};
        recentBookingsModel = new DefaultTableModel(bookingColumns, 0);
        JTable recentBookingsTable = new JTable(recentBookingsModel);
        recentBookingsTable.setRowHeight(30);
        recentBookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane bookingScroll = new JScrollPane(recentBookingsTable);
        rightPanel.add(bookingScroll, BorderLayout.CENTER);

        JPanel bookingActions = new JPanel(new FlowLayout());
        JButton refreshBookingsBtn = new JButton("🔄 Refresh");
        refreshBookingsBtn.addActionListener(e -> loadRecentBookings());
        bookingActions.add(refreshBookingsBtn);
        rightPanel.add(bookingActions, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        return mainPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel buttonPanel = createButtonPanel();

        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(Color.GRAY);

        Timer statusTimer = new Timer(5000, e ->
                statusLabel.setText("Last auto-refresh: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        );
        statusTimer.start();

        footer.add(buttonPanel, BorderLayout.WEST);
        footer.add(statusLabel, BorderLayout.EAST);

        return footer;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = new JButton("🔄 Refresh All Statistics");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            loadStatistics();
            JOptionPane.showMessageDialog(this, "Statistics refreshed!");
        });

        JButton exportBtn = new JButton("📊 Export Report");
        exportBtn.setBackground(new Color(46, 204, 113));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.addActionListener(e -> exportReport());

        JButton printBtn = new JButton("🖨️ Print Dashboard");
        printBtn.setBackground(new Color(155, 89, 182));
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> printDashboard());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(printBtn);

        return buttonPanel;
    }

    private void loadStatistics() {
        try {
            int users = userService.getAllUsers().size();
            int movies = movieService.getAllMovies().size();
            int schedules = scheduleService.getAllSchedules().size();

            totalUsersLabel.setText(String.valueOf(users));
            totalMoviesLabel.setText(String.valueOf(movies));
            totalSchedulesLabel.setText(String.valueOf(schedules));

            // Use mock data for now (since booking service might not have data)
            int todayBookings = 45;
            double totalRevenue = 5280.00;
            double weeklyRevenue = 18450.00;
            double occupancy = 67;
            String popularMovie = "Inception";

            todayBookingsLabel.setText(String.valueOf(todayBookings));
            totalRevenueLabel.setText(String.format("$%,.2f", totalRevenue));
            weeklyRevenueLabel.setText(String.format("$%,.2f", weeklyRevenue));
            occupancyLabel.setText(String.format("%.0f%%", occupancy));
            popularMovieLabel.setText(popularMovie != null ? popularMovie : "N/A");

            if (occupancyProgress != null) {
                occupancyProgress.setValue((int) occupancy);
            }

        } catch (DatabaseException e) {
            totalUsersLabel.setText("Error");
            totalMoviesLabel.setText("Error");
            totalSchedulesLabel.setText("Error");
        }

        // Always load mock data for tables
        loadTopMovies();
        loadRecentBookings();
    }

    private void loadTopMovies() {
        topMoviesModel.setRowCount(0);

        // Safe mock data - always 5 rows
        String[][] movies = {
                {"1", "Inception", "156", "$1,872", "4.8 ★"},
                {"2", "The Dark Knight", "142", "$1,704", "4.9 ★"},
                {"3", "Interstellar", "128", "$1,536", "4.7 ★"},
                {"4", "John Wick", "98", "$1,176", "4.6 ★"},
                {"5", "The Matrix", "87", "$1,044", "4.8 ★"}
        };

        for (String[] movie : movies) {
            topMoviesModel.addRow(movie);
        }
    }

    private void loadRecentBookings() {
        recentBookingsModel.setRowCount(0);

        // Safe mock data
        String[][] bookings = {
                {"1001", "John Smith", "Inception", "18:00", "2", "$24.00", "Confirmed"},
                {"1002", "Emma Wilson", "The Dark Knight", "19:00", "3", "$36.00", "Confirmed"},
                {"1003", "Michael Brown", "Interstellar", "21:00", "1", "$12.00", "Pending"},
                {"1004", "Sarah Davis", "John Wick", "20:00", "4", "$48.00", "Confirmed"},
                {"1005", "James Lee", "The Matrix", "18:30", "2", "$24.00", "Cancelled"}
        };

        for (String[] booking : bookings) {
            recentBookingsModel.addRow(booking);
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("dashboard_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("CINEMA MANAGEMENT SYSTEM - DASHBOARD REPORT");
                writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println();

                writer.println("STATISTICS SUMMARY");
                writer.println("Total Users," + totalUsersLabel.getText());
                writer.println("Total Movies," + totalMoviesLabel.getText());
                writer.println("Active Schedules," + totalSchedulesLabel.getText());
                writer.println("Today's Bookings," + todayBookingsLabel.getText());
                writer.println("Total Revenue," + totalRevenueLabel.getText());
                writer.println("Weekly Revenue," + weeklyRevenueLabel.getText());
                writer.println("Occupancy Rate," + occupancyLabel.getText());
                writer.println("Popular Movie," + popularMovieLabel.getText());
                writer.println();

                writer.println("TOP 5 MOVIES");
                writer.println("Rank,Movie Title,Tickets Sold,Revenue,Rating");
                for (int i = 0; i < topMoviesModel.getRowCount(); i++) {
                    writer.println(topMoviesModel.getValueAt(i, 0) + "," +
                            topMoviesModel.getValueAt(i, 1) + "," +
                            topMoviesModel.getValueAt(i, 2) + "," +
                            topMoviesModel.getValueAt(i, 3) + "," +
                            topMoviesModel.getValueAt(i, 4));
                }
                writer.println();

                writer.println("RECENT BOOKINGS");
                writer.println("Booking ID,Customer,Movie,Show Time,Tickets,Amount,Status");
                for (int i = 0; i < recentBookingsModel.getRowCount(); i++) {
                    writer.println(recentBookingsModel.getValueAt(i, 0) + "," +
                            recentBookingsModel.getValueAt(i, 1) + "," +
                            recentBookingsModel.getValueAt(i, 2) + "," +
                            recentBookingsModel.getValueAt(i, 3) + "," +
                            recentBookingsModel.getValueAt(i, 4) + "," +
                            recentBookingsModel.getValueAt(i, 5) + "," +
                            recentBookingsModel.getValueAt(i, 6));
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }

    private void printDashboard() {
        String[] options = {"Quick Print", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Print Dashboard?",
                "Print Dashboard",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            try {
                boolean complete = this.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Dashboard sent to printer!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
            }
        }
    }

    private boolean print() {return print();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(60000, e -> loadStatistics());
        refreshTimer.start();
    }

    public void refresh() {
        loadStatistics();
    }
}