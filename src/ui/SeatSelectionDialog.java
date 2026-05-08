package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class SeatSelectionDialog extends JDialog {
    private List<Seat> selectedSeats;
    private boolean confirmed = false;
    private JLabel selectedCountLabel;
    private JLabel totalPriceLabel;
    private JPanel seatPanel;

    private final double VIP_PRICE = 18.00;
    private final double REGULAR_PRICE = 12.00;
    private final double STANDARD_PRICE = 10.00;

    public SeatSelectionDialog(Frame parent, Schedule schedule) {
        super(parent, "Select Seats - " + schedule.getMovie().getTitle(), true);
        this.selectedSeats = new ArrayList<>();
        initComponents(schedule);
        setSize(800, 650);
        setLocationRelativeTo(parent);
    }

    private void initComponents(Schedule schedule) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("🎬 " + schedule.getMovie().getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel time = new JLabel("🕐 " + formatTime(schedule.getStartTime()) + " | " + schedule.getScreenName());
        time.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        time.setForeground(Color.LIGHT_GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(time);

        selectedCountLabel = new JLabel("Selected: 0 seats");
        selectedCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedCountLabel.setForeground(Color.WHITE);

        totalPriceLabel = new JLabel("Total: $0.00");
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalPriceLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(selectedCountLabel);
        rightPanel.add(totalPriceLabel);

        header.add(textPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Screen
        JLabel screen = new JLabel("S C R E E N", SwingConstants.CENTER);
        screen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        screen.setOpaque(true);
        screen.setBackground(new Color(52, 73, 94));
        screen.setForeground(Color.WHITE);
        screen.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(screen, BorderLayout.CENTER);

        // Seat grid
        seatPanel = new JPanel(new GridLayout(10, 15, 4, 4));
        seatPanel.setBackground(new Color(248, 249, 250));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 15; col++) {
                String seatNum = String.format("%c%d", (char)('A' + row), col + 1);
                JToggleButton btn = new JToggleButton(seatNum);
                btn.setPreferredSize(new Dimension(45, 45));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));

                Color bgColor;
                String type;
                double price;

                if (row < 2) {
                    bgColor = new Color(255, 215, 0);
                    type = "VIP";
                    price = VIP_PRICE;
                } else if (row < 7) {
                    bgColor = new Color(173, 216, 230);
                    type = "REGULAR";
                    price = REGULAR_PRICE;
                } else {
                    bgColor = new Color(144, 238, 144);
                    type = "STANDARD";
                    price = STANDARD_PRICE;
                }

                btn.setBackground(bgColor);
                btn.setToolTipText(type + " Seat - $" + price);

                final int r = row;
                final int c = col;
                final String seatType = type;
                final double seatPrice = price;

                btn.addActionListener(e -> {
                    if (btn.isSelected()) {
                        Seat seat = new Seat();
                        seat.setRow(r + 1);
                        seat.setNumber(c + 1);
                        seat.setSeatType(seatType);
                        selectedSeats.add(seat);
                        btn.setBackground(Color.ORANGE);
                    } else {
                        selectedSeats.removeIf(s -> s.getRow() == r + 1 && s.getNumber() == c + 1);
                        btn.setBackground(bgColor);
                    }
                    updateSummary();
                });

                seatPanel.add(btn);
            }
        }

        JScrollPane scroll = new JScrollPane(seatPanel);
        add(scroll, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        legend.setBackground(Color.WHITE);
        legend.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        legend.add(createLegend(new Color(255, 215, 0), "VIP - $18"));
        legend.add(createLegend(new Color(173, 216, 230), "Regular - $12"));
        legend.add(createLegend(new Color(144, 238, 144), "Standard - $10"));
        legend.add(createLegend(Color.ORANGE, "Selected"));

        add(legend, BorderLayout.SOUTH);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setBackground(new Color(52, 73, 94));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton clear = new JButton("Clear All");
        clear.setBackground(new Color(149, 165, 166));
        clear.setForeground(Color.WHITE);
        clear.addActionListener(e -> clearAllSeats());

        JButton confirm = new JButton("Confirm Selection");
        confirm.setBackground(new Color(46, 204, 113));
        confirm.setForeground(Color.WHITE);
        confirm.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        buttons.add(clear);
        buttons.add(confirm);
        buttons.add(cancel);
        add(buttons, BorderLayout.NORTH);
    }

    private JPanel createLegend(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        JLabel colorBox = new JLabel("  ");
        colorBox.setBackground(color);
        colorBox.setOpaque(true);
        colorBox.setPreferredSize(new Dimension(25, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(colorBox);
        panel.add(new JLabel(text));
        return panel;
    }

    private void updateSummary() {
        int count = selectedSeats.size();
        double total = 0;
        for (Seat s : selectedSeats) {
            if ("VIP".equals(s.getSeatType())) total += VIP_PRICE;
            else if ("REGULAR".equals(s.getSeatType())) total += REGULAR_PRICE;
            else total += STANDARD_PRICE;
        }
        selectedCountLabel.setText("Selected: " + count + " seat" + (count != 1 ? "s" : ""));
        totalPriceLabel.setText(String.format("Total: $%.2f", total));
    }

    private void clearAllSeats() {
        selectedSeats.clear();
        Component[] comps = seatPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof JToggleButton) {
                JToggleButton btn = (JToggleButton) comps[i];
                btn.setSelected(false);
                int row = i / 15;
                if (row < 2) btn.setBackground(new Color(255, 215, 0));
                else if (row < 7) btn.setBackground(new Color(173, 216, 230));
                else btn.setBackground(new Color(144, 238, 144));
            }
        }
        updateSummary();
    }

    private String formatTime(Timestamp t) {
        if (t == null) return "N/A";
        return t.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }
    // In SeatSelectionDialog.java, update the getTotalPrice method
    public List<Seat> getSelectedSeats() {
        return confirmed ? selectedSeats : new ArrayList<>();
    }

    public double getTotalPrice() {
        double total = 0;
        for (Seat seat : selectedSeats) {
            if ("VIP".equals(seat.getSeatType())) {
                total += 18.00;
            } else if ("REGULAR".equals(seat.getSeatType())) {
                total += 12.00;
            } else {
                total += 10.00;
            }
        }
        return total;
    }
}