package panels;

import javax.swing.*;
import java.awt.*;
import model.User;
import panels.UserManagementPanel;
import panels.MovieManagementPanel;
import panels.ScheduleManagementPanel;
import panels.AdminDashboardPanel;

public class AdminPanel extends JPanel {
    private User currentUser;
    private JTabbedPane tabbedPane;

    // Panel components
    private UserManagementPanel userPanel;
    private MovieManagementPanel moviePanel;
    private ScheduleManagementPanel schedulePanel;
    private AdminDashboardPanel dashboardPanel;

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = new Color(236, 240, 241);

    public AdminPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Initialize panels
        userPanel = new UserManagementPanel();
        moviePanel = new MovieManagementPanel(currentUser);
        schedulePanel = new ScheduleManagementPanel();
        dashboardPanel = new AdminDashboardPanel();

        // Add tabs
        tabbedPane.addTab("👥 User Management", userPanel);
        tabbedPane.addTab("🎬 Movie Management", moviePanel);
        tabbedPane.addTab("📅 Schedule Management", schedulePanel);
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("👑 Admin Control Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("System Management Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(189, 195, 199));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        leftPanel.add(textPanel);

        // Right side - User info and logout
        JPanel rightPanel = createRightPanel();

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Avatar circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillOval(0, 0, 40, 40);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String letter = currentUser.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int x = (40 - fm.stringWidth(letter)) / 2;
                int y = ((40 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(letter, x, y);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(40, 40));
        avatarPanel.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(189, 195, 199));

        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);
        userInfoPanel.add(userLabel);
        userInfoPanel.add(roleLabel);

        JButton logoutBtn = createModernButton("Logout", new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(avatarPanel);
        rightPanel.add(userInfoPanel);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(logoutBtn);

        return rightPanel;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void refreshData() {
        if (userPanel != null) userPanel.refresh();
        if (moviePanel != null) moviePanel.refresh();
        if (schedulePanel != null) schedulePanel.refresh();
        if (dashboardPanel != null) dashboardPanel.refresh();
    }
}