package panels;

import javax.swing.*;
import java.awt.*;
import model.User;
import panels.MovieBrowsePanel;
import panels.BookingHistoryPanel;

public class CustomerPortalPanel extends JPanel {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public CustomerPortalPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Browse Movies Tab
        MovieBrowsePanel browsePanel = new MovieBrowsePanel(currentUser);
        tabbedPane.addTab("🎬 Browse Movies", browsePanel);

        // My Bookings Tab
        BookingHistoryPanel bookingsPanel = new BookingHistoryPanel();
        tabbedPane.addTab("📋 My Bookings", bookingsPanel);

        // Profile Tab
        JPanel profilePanel = createProfilePanel();
        tabbedPane.addTab("👤 My Profile", profilePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("🎬 Customer Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Info
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentUser.getUsername()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentUser.getName()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        emailField.setColumns(20);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        phoneField.setColumns(20);
        panel.add(phoneField, gbc);

        // Save Button
        gbc.gridx = 1; gbc.gridy = 4;
        JButton saveBtn = new JButton("Update Profile");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Profile updated successfully!");
        });
        panel.add(saveBtn, gbc);

        return panel;
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
}