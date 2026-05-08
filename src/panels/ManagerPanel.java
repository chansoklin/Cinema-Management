package panels;

import javax.swing.*;
import java.awt.*;
import model.User;

public class ManagerPanel extends JPanel {
    private User currentUser;
    private JLabel statusLabel;
    private Timer refreshTimer;

    // Component panels
    private StatsCardPanel statsCardPanel;
    private TodaySchedulePanel todaySchedulePanel;
    private TopMoviesPanel topMoviesPanel;
    private RecentSalesPanel recentSalesPanel;
    private ManagerFooterPanel footerPanel;
    private DashboardPanel dashboardPanel;  // Add Dashboard
    private ScheduleManagementPanel scheduleManagementPanel;  // Add Schedule Management

    public ManagerPanel(User user) {
        this.currentUser = user;
        initComponents();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Create Tabbed Pane for Manager
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Dashboard (Operations Overview)
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);

        // Tab 2: Today's Schedule (Quick View)
        JPanel scheduleOverviewPanel = createScheduleOverviewPanel();
        tabbedPane.addTab("📅 Today's Schedule", scheduleOverviewPanel);

        // Tab 3: Full Schedule Management
        scheduleManagementPanel = new ScheduleManagementPanel();
        tabbedPane.addTab("📆 Schedule Management", scheduleManagementPanel);

        // Tab 4: Sales Reports
        JPanel salesPanel = createSalesReportPanel();
        tabbedPane.addTab("💰 Sales Reports", salesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Footer
        footerPanel = new ManagerFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        statusLabel = footerPanel.getStatusLabel();
    }

    private JPanel createScheduleOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Today's Schedule Panel
        todaySchedulePanel = new TodaySchedulePanel();
        panel.add(todaySchedulePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Stats Cards
        statsCardPanel = new StatsCardPanel();
        panel.add(statsCardPanel, BorderLayout.NORTH);

        // Top Movies and Recent Sales
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        topMoviesPanel = new TopMoviesPanel();
        recentSalesPanel = new RecentSalesPanel();

        splitPane.setLeftComponent(topMoviesPanel);
        splitPane.setRightComponent(recentSalesPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("🎬 Manager Operations Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel quickStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        quickStats.setOpaque(false);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.WHITE);

        Timer dateTimer = new Timer(1000, e ->
                dateLabel.setText(java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, HH:mm")))
        );
        dateTimer.start();

        JLabel userLabel = new JLabel("👤 " + currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        JButton logoutBtn = createSmallButton("Logout", new Color(231, 76, 60), Color.WHITE);
        logoutBtn.addActionListener(e -> logout());

        quickStats.add(dateLabel);
        quickStats.add(userLabel);
        quickStats.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(quickStats, BorderLayout.EAST);

        return header;
    }

    private JButton createSmallButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
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

    private void loadData() {
        if (dashboardPanel != null) dashboardPanel.refresh();
        if (todaySchedulePanel != null) todaySchedulePanel.loadSchedule();
        if (topMoviesPanel != null) topMoviesPanel.loadTopMovies();
        if (recentSalesPanel != null) recentSalesPanel.loadRecentSales();
        if (scheduleManagementPanel != null) scheduleManagementPanel.refresh();
        if (statsCardPanel != null) statsCardPanel.loadStatistics();
        if (footerPanel != null) footerPanel.updateTimestamp();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(60000, e -> {
            loadData();
            if (statusLabel != null) {
                statusLabel.setText("✅ Auto-refreshed at " +
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        });
        refreshTimer.start();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void showManagerPanel(User user) {
        this.currentUser = user;
        loadData();
        revalidate();
        repaint();
    }

    public void refresh() {
        loadData();
    }
}