package ui;

import javax.swing.*;
import java.awt.*;
import model.Schedule;
import model.Movie;
import java.text.SimpleDateFormat;

public class ScheduleDialog extends JDialog {
    private Schedule schedule;

    public ScheduleDialog(Frame parent, Schedule schedule) {
        super(parent, "Schedule Details", true);
        this.schedule = schedule;
        initComponents();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Movie Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Movie:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(schedule.getMovieTitle()), gbc);

        // Hall/Screen
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Hall:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(schedule.getHall()), gbc);

        // Show Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Show Time:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(schedule.getFormattedShowTime()), gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("$" + schedule.getPrice()), gbc);

        add(detailsPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}