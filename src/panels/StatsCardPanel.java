package panels;

import javax.swing.*;
import java.awt.*;
import service.BookingService;
import service.ScheduleService;
import exception.DatabaseException;

public class StatsCardPanel extends JPanel {
    private JLabel todayRevenueLabel;
    private JLabel todayTicketsLabel;
    private JLabel occupancyLabel;
    private JLabel activeShowsLabel;
    private JProgressBar occupancyProgress;

    private BookingService bookingService;
    private ScheduleService scheduleService;

    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public StatsCardPanel() {
        this.bookingService = new BookingService();
        this.scheduleService = new ScheduleService();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 4, 15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        todayRevenueLabel = createStatCard("💰 Today's Revenue", "$0", SUCCESS_COLOR, "Cash + Card");
        todayTicketsLabel = createStatCard("🎫 Tickets Sold", "0", PRIMARY_COLOR, "Today");
        occupancyLabel = createStatCardWithProgress("📊 Occupancy", "0%", WARNING_COLOR);
        activeShowsLabel = createStatCard("🎬 Active Shows", "0", INFO_COLOR, "Now Playing");
    }

    private JLabel createStatCard(String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        add(card);
        return valueLabel;
    }

    private JLabel createStatCardWithProgress(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        occupancyProgress = new JProgressBar(0, 100);
        occupancyProgress.setValue(0);
        occupancyProgress.setStringPainted(true);
        occupancyProgress.setForeground(color);
        occupancyProgress.setPreferredSize(new Dimension(100, 15));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(occupancyProgress, BorderLayout.SOUTH);

        add(card);
        return valueLabel;
    }

    public void loadStatistics() {
        try {
            double todayRevenue = bookingService.getTodayRevenue();
            int todayTickets = bookingService.getTodayTicketsCount();
            double occupancy = bookingService.getAverageOccupancyRate();
            int activeShows = scheduleService.getActiveShowsCount();

            todayRevenueLabel.setText(String.format("$%,.2f", todayRevenue));
            todayTicketsLabel.setText(String.format("%,d", todayTickets));
            occupancyLabel.setText(String.format("%.0f%%", occupancy));
            activeShowsLabel.setText(String.valueOf(activeShows));

            if (occupancyProgress != null) {
                occupancyProgress.setValue((int) occupancy);
            }

        } catch (DatabaseException e) {
            // Demo data
            todayRevenueLabel.setText("$2,456");
            todayTicketsLabel.setText("187");
            occupancyLabel.setText("68%");
            activeShowsLabel.setText("5");
            if (occupancyProgress != null) occupancyProgress.setValue(68);
        }
    }

    public void refresh() {
        loadStatistics();
    }

    public static class ShowSelectionPanel {
    }
}