package panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManagerFooterPanel extends JPanel {
    private JLabel statusLabel;
    private JLabel timerLabel;

    public ManagerFooterPanel() {
        initComponents();
        startTimer();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        statusLabel = new JLabel("✅ System Online");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timerLabel.setForeground(Color.GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = createButton("🔄 Refresh All", new Color(46, 204, 113));
        refreshBtn.addActionListener(e -> {
            statusLabel.setText("✅ Refreshing...");
        });

        buttonPanel.add(refreshBtn);

        add(statusLabel, BorderLayout.WEST);
        add(timerLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void startTimer() {
        Timer timer = new Timer(1000, e -> {
            timerLabel.setText("📅 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        timer.start();
    }

    public void updateTimestamp() {
        statusLabel.setText("✅ Auto-refreshed at " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Reset after 3 seconds
        Timer resetTimer = new Timer(3000, e -> {
            statusLabel.setText("✅ System Online");
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }
}