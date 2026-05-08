package panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import service.*;
import exception.DatabaseException;

public class AdminDashboardPanel extends JPanel {
    private JLabel totalUsersLabel;
    private JLabel totalMoviesLabel;
    private JLabel totalSchedulesLabel;
    private JLabel todayBookingsLabel;
    private JLabel totalRevenueLabel;
    private JLabel occupancyLabel;
    private JLabel weeklyRevenueLabel;
    private JLabel popularMovieLabel;
    private JProgressBar occupancyProgress;
    private Timer refreshTimer;
    private JLabel lastUpdateLabel;

    // Modern color palette
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_WARNING = new Color(241, 196, 15);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_INFO = new Color(52, 152, 219);
    private final Color COLOR_PURPLE = new Color(155, 89, 182);
    private final Color COLOR_DARK = new Color(44, 62, 80);
    private final Color COLOR_WHITE = Color.WHITE;
    private final Color COLOR_BG = new Color(248, 249, 250);
    private final Color COLOR_BORDER = new Color(235, 240, 245);

    public AdminDashboardPanel() {
        initComponents();
        loadStatistics();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);

        // Main content with gradient header
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header Section
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Stats Grid
        JPanel statsPanel = createStatsGrid();
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_DARK);

        JLabel subtitleLabel = new JLabel("Welcome back! Here's what's happening with your cinema today.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        leftPanel.add(iconLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(textPanel);

        // Right side - Date/Time
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        Timer dateTimer = new Timer(1000, e ->
                dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy • HH:mm")))
        );
        dateTimer.start();

        rightPanel.add(dateLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsGrid() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 20, 20));
        panel.setOpaque(false);

        // Row 1 - Main Statistics
        totalUsersLabel = createStatCard(panel, "Total Users", "0", "👥", COLOR_PRIMARY, "Registered customers");
        totalMoviesLabel = createStatCard(panel, "Total Movies", "0", "🎬", COLOR_SUCCESS, "In library");
        totalSchedulesLabel = createStatCard(panel, "Active Shows", "0", "📅", COLOR_PURPLE, "Today's screenings");
        todayBookingsLabel = createStatCard(panel, "Today's Bookings", "0", "🎟️", COLOR_WARNING, "Tickets sold today");

        // Row 2 - Financial & Performance
        totalRevenueLabel = createStatCard(panel, "Total Revenue", "$0", "💰", COLOR_SUCCESS, "All time");

        // Occupancy Card with Progress Bar
        JPanel occupancyCard = createOccupancyCard();
        panel.add(occupancyCard);

        weeklyRevenueLabel = createStatCard(panel, "Weekly Revenue", "$0", "📈", COLOR_INFO, "Last 7 days");
        popularMovieLabel = createStatCard(panel, "Popular Movie", "N/A", "⭐", COLOR_DANGER, "Most booked");

        return panel;
    }

    private JLabel createStatCard(JPanel parent, String title, String value, String icon, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        // Top section with icon and value
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(valueLabel, BorderLayout.EAST);

        // Bottom section with title and subtitle
        JPanel bottomSection = new JPanel(new GridLayout(2, 1, 0, 3));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(COLOR_DARK);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(Color.GRAY);

        bottomSection.add(titleLabel);
        bottomSection.add(subLabel);

        card.add(topSection, BorderLayout.CENTER);
        card.add(bottomSection, BorderLayout.SOUTH);

        parent.add(card);
        return valueLabel;
    }

    private JPanel createOccupancyCard() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        // Top section with icon and value
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));

        occupancyLabel = new JLabel("0%");
        occupancyLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        occupancyLabel.setForeground(COLOR_INFO);
        occupancyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(occupancyLabel, BorderLayout.EAST);

        // Middle section with title
        JLabel titleLabel = new JLabel("Occupancy Rate");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(COLOR_DARK);

        // Progress bar
        occupancyProgress = new JProgressBar(0, 100);
        occupancyProgress.setValue(0);
        occupancyProgress.setStringPainted(true);
        occupancyProgress.setForeground(COLOR_INFO);
        occupancyProgress.setBackground(new Color(220, 220, 220));
        occupancyProgress.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        progressPanel.add(occupancyProgress, BorderLayout.CENTER);

        // Subtitle
        JLabel subLabel = new JLabel("Current seat utilization");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(Color.GRAY);

        card.add(topSection, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(progressPanel, BorderLayout.SOUTH);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left side - Action buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JButton refreshBtn = createModernButton("🔄 Refresh Statistics", COLOR_PRIMARY, COLOR_WHITE);
        refreshBtn.addActionListener(e -> {
            loadStatistics();
            JOptionPane.showMessageDialog(this, "✅ Statistics refreshed!");
        });

        JButton exportBtn = createModernButton("📊 Export Report", COLOR_SUCCESS, COLOR_WHITE);
        exportBtn.addActionListener(e -> exportReport());

        JButton printBtn = createModernButton("🖨️ Print Dashboard", COLOR_PURPLE, COLOR_WHITE);
        printBtn.addActionListener(e -> printDashboard());

        leftPanel.add(refreshBtn);
        leftPanel.add(exportBtn);
        leftPanel.add(printBtn);

        // Right side - Status
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        lastUpdateLabel = new JLabel();
        lastUpdateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lastUpdateLabel.setForeground(Color.GRAY);

        rightPanel.add(lastUpdateLabel);

        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadStatistics() {
        try {
            UserService userService = new UserService();
            MovieService movieService = new MovieService();
            ScheduleService scheduleService = new ScheduleService();
            BookingService bookingService = new BookingService();

            int users = userService.getAllUsers().size();
            int movies = movieService.getAllMovies().size();
            int schedules = scheduleService.getAllSchedules().size();

            totalUsersLabel.setText(String.valueOf(users));
            totalMoviesLabel.setText(String.valueOf(movies));
            totalSchedulesLabel.setText(String.valueOf(schedules));

            // Get real data from database
            int todayBookings = bookingService.getTodayBookingsCount();
            double totalRevenue = bookingService.getTotalRevenue();
            double weeklyRevenue = bookingService.getWeeklyRevenue();
            int occupancy = (int) bookingService.getAverageOccupancyRate();
            String popularMovie = bookingService.getMostPopularMovie();

            todayBookingsLabel.setText(String.valueOf(todayBookings));
            totalRevenueLabel.setText(String.format("$%,.2f", totalRevenue));
            weeklyRevenueLabel.setText(String.format("$%,.2f", weeklyRevenue));
            occupancyLabel.setText(occupancy + "%");
            popularMovieLabel.setText(popularMovie != null ? popularMovie : "N/A");

            if (occupancyProgress != null) {
                occupancyProgress.setValue(Math.min(occupancy, 100));
            }

            lastUpdateLabel.setText("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        } catch (DatabaseException e) {
            // Fallback to demo data if database connection fails
            totalUsersLabel.setText("128");
            totalMoviesLabel.setText("45");
            totalSchedulesLabel.setText("12");
            todayBookingsLabel.setText("87");
            totalRevenueLabel.setText("$12,450");
            weeklyRevenueLabel.setText("$3,240");
            occupancyLabel.setText("68%");
            popularMovieLabel.setText("Inception");

            if (occupancyProgress != null) {
                occupancyProgress.setValue(68);
            }

            lastUpdateLabel.setText("Demo mode - Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
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
                writer.println("METRIC,VALUE");
                writer.println("Total Users," + totalUsersLabel.getText());
                writer.println("Total Movies," + totalMoviesLabel.getText());
                writer.println("Active Schedules," + totalSchedulesLabel.getText());
                writer.println("Today's Bookings," + todayBookingsLabel.getText());
                writer.println("Total Revenue," + totalRevenueLabel.getText());
                writer.println("Weekly Revenue," + weeklyRevenueLabel.getText());
                writer.println("Occupancy Rate," + occupancyLabel.getText());
                writer.println("Popular Movie," + popularMovieLabel.getText());

                JOptionPane.showMessageDialog(this, "✅ Report exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }

    private void printDashboard() {
        try {
            boolean complete = this.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Dashboard sent to printer!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage());
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