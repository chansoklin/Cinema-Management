package panels;

import javax.swing.*;
import java.awt.*;
import model.User;

public class FrontDeskPanel extends JPanel {
    private User currentUser;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;

    private SellTicketPanel sellTicketPanel;
    private BookingHistoryPanel bookingHistoryPanel;
    private SalesRecordPanel salesRecordPanel;
    public FrontDeskPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Tab 1: Sell Tickets
        sellTicketPanel = new SellTicketPanel(currentUser);
        tabbedPane.addTab("🎟️ Sell Tickets", sellTicketPanel);

        // Tab 2: Booking History
        bookingHistoryPanel = new BookingHistoryPanel();
        tabbedPane.addTab("📋 Booking History", bookingHistoryPanel);
        tabbedPane.addTab("💰 Sales Records", new SalesRecordPanel());  // Add this tab
        add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = createFooter();
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("🎪 Front Desk - Ticket Sales");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("👤 " + currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.WHITE);

        Timer dateTimer = new Timer(1000, e ->
                dateLabel.setText(java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, HH:mm")))
        );
        dateTimer.start();

        JButton logoutBtn = createButton("Logout", new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(dateLabel);
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        statusLabel = new JLabel("✅ Ready to process ticket sales");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);

        footer.add(statusLabel, BorderLayout.WEST);

        return footer;
    }

    private JButton createButton(String text, Color color) {
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

    public void refresh() {
        if (sellTicketPanel != null) sellTicketPanel.refresh();
        if (bookingHistoryPanel != null) bookingHistoryPanel.refresh();
    }
}