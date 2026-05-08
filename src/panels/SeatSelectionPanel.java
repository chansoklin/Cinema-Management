package panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import service.BookingService;
import exception.DatabaseException;

public class SeatSelectionPanel extends JPanel {
    private Schedule schedule;
    private List<Seat> selectedSeats;
    private JPanel seatGridPanel;
    private List<JToggleButton> seatButtons;
    private int rows;
    private int cols;
    private JLabel selectedCountLabel;
    private JLabel totalPriceLabel;

    // Seat prices
    private final double VIP_PRICE = 18.00;
    private final double REGULAR_PRICE = 12.00;
    private final double STANDARD_PRICE = 10.00;

    // High contrast colors for better visibility
    private final Color COLOR_VIP = new Color(255, 193, 7);      // Gold - very visible
    private final Color COLOR_REGULAR = new Color(33, 150, 243); // Bright Blue
    private final Color COLOR_STANDARD = new Color(76, 175, 80); // Bright Green
    private final Color COLOR_SELECTED = new Color(255, 87, 34); // Bright Orange
    private final Color COLOR_BOOKED = new Color(244, 67, 54);   // Bright Red
    private final Color COLOR_BG = new Color(250, 250, 252);
    private final Color COLOR_BORDER = new Color(200, 200, 200);
    private final Color TEXT_DARK = new Color(33, 33, 33);
    private final Color TEXT_LIGHT = new Color(255, 255, 255);

    public SeatSelectionPanel(Schedule schedule, int rows, int cols) {
        this.schedule = schedule;
        this.rows = rows;
        this.cols = cols;
        this.selectedSeats = new ArrayList<>();
        this.seatButtons = new ArrayList<>();
        initComponents();
        loadBookedSeats();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Info Panel with seat categories
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);

        // Screen representation
        JPanel screenPanel = createScreenPanel();
        add(screenPanel, BorderLayout.NORTH);

        // Seat grid
        seatGridPanel = new JPanel(new GridLayout(rows, cols, 8, 8));
        seatGridPanel.setBackground(COLOR_BG);
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        createSeatButtons();

        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(COLOR_BG);
        scrollPane.setPreferredSize(new Dimension(700, 450));
        add(scrollPane, BorderLayout.CENTER);

        // Legend Panel
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Select Your Seats");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        summaryPanel.setBackground(Color.WHITE);

        selectedCountLabel = new JLabel("Selected: 0 seats");
        selectedCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedCountLabel.setForeground(COLOR_REGULAR);

        totalPriceLabel = new JLabel("Total: $0.00");
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalPriceLabel.setForeground(COLOR_SELECTED);

        summaryPanel.add(selectedCountLabel);
        summaryPanel.add(totalPriceLabel);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(summaryPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Row A-B: VIP
        JPanel vipInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        vipInfo.setBackground(new Color(255, 193, 7, 30));
        vipInfo.setBorder(BorderFactory.createLineBorder(COLOR_VIP));
        JLabel vipLabel = new JLabel("🎖️ Rows A-B: VIP Seats - $18");
        vipLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vipLabel.setForeground(new Color(255, 193, 7));
        vipInfo.add(vipLabel);

        // Row C-G: Regular
        JPanel regularInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        regularInfo.setBackground(new Color(33, 150, 243, 30));
        regularInfo.setBorder(BorderFactory.createLineBorder(COLOR_REGULAR));
        JLabel regularLabel = new JLabel("💺 Rows C-G: Regular Seats - $12");
        regularLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        regularLabel.setForeground(COLOR_REGULAR);
        regularInfo.add(regularLabel);

        // Row H-J: Standard
        JPanel standardInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        standardInfo.setBackground(new Color(76, 175, 80, 30));
        standardInfo.setBorder(BorderFactory.createLineBorder(COLOR_STANDARD));
        JLabel standardLabel = new JLabel("💺 Rows H-J: Standard Seats - $10");
        standardLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        standardLabel.setForeground(COLOR_STANDARD);
        standardInfo.add(standardLabel);

        panel.add(vipInfo);
        panel.add(regularInfo);
        panel.add(standardInfo);

        return panel;
    }

    private JPanel createScreenPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 15, 50));

        JLabel screenLabel = new JLabel("S C R E E N", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(52, 73, 94));
        screenLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));

        panel.add(screenLabel, BorderLayout.CENTER);

        return panel;
    }

    private void createSeatButtons() {
        seatGridPanel.removeAll();
        seatButtons.clear();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                final int seatRow = row + 1;
                final int seatNumber = col + 1;
                final String seatLabel = String.format("%c%d", (char)('A' + row), col + 1);

                // Determine seat type and price
                String seatType;
                double price;
                Color bgColor;
                String tooltip;

                if (row < 2) {
                    seatType = "VIP";
                    price = VIP_PRICE;
                    bgColor = COLOR_VIP;
                    tooltip = String.format("🎖️ VIP Seat %s - $%.2f", seatLabel, price);
                } else if (row < 7) {
                    seatType = "REGULAR";
                    price = REGULAR_PRICE;
                    bgColor = COLOR_REGULAR;
                    tooltip = String.format("💺 Regular Seat %s - $%.2f", seatLabel, price);
                } else {
                    seatType = "STANDARD";
                    price = STANDARD_PRICE;
                    bgColor = COLOR_STANDARD;
                    tooltip = String.format("💺 Standard Seat %s - $%.2f", seatLabel, price);
                }

                JToggleButton seatButton = new JToggleButton(seatLabel);
                seatButton.setPreferredSize(new Dimension(55, 55));
                seatButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                seatButton.setBackground(bgColor);
                seatButton.setForeground(TEXT_LIGHT);
                seatButton.setToolTipText(tooltip);
                seatButton.setBorder(BorderFactory.createRaisedBevelBorder());
                seatButton.setFocusPainted(false);

                final String finalSeatType = seatType;
                final double finalPrice = price;
                final Color originalColor = bgColor;

                seatButton.addActionListener(e -> {
                    if (seatButton.isSelected()) {
                        Seat seat = new Seat();
                        seat.setRow(seatRow);
                        seat.setNumber(seatNumber);
                        seat.setSeatType(finalSeatType);
                        selectedSeats.add(seat);
                        seatButton.setBackground(COLOR_SELECTED);
                        seatButton.setToolTipText("✓ Selected");
                        seatButton.setBorder(BorderFactory.createLoweredBevelBorder());
                    } else {
                        selectedSeats.removeIf(s -> s.getRow() == seatRow && s.getNumber() == seatNumber);
                        seatButton.setBackground(originalColor);
                        seatButton.setToolTipText(tooltip);
                        seatButton.setBorder(BorderFactory.createRaisedBevelBorder());
                    }
                    updateSummary();
                });

                seatGridPanel.add(seatButton);
                seatButtons.add(seatButton);
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        panel.add(createLegendItem(COLOR_VIP, "🎖️ VIP - $18"));
        panel.add(createLegendItem(COLOR_REGULAR, "💺 Regular - $12"));
        panel.add(createLegendItem(COLOR_STANDARD, "💺 Standard - $10"));
        panel.add(createLegendItem(COLOR_SELECTED, "✓ Selected"));
        panel.add(createLegendItem(COLOR_BOOKED, "🔴 Booked"));

        return panel;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setBackground(Color.WHITE);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(25, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_DARK);

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private void updateSummary() {
        int count = selectedSeats.size();
        double total = calculateTotal();

        selectedCountLabel.setText(String.format("✓ Selected: %d seat%s", count, count != 1 ? "s" : ""));
        totalPriceLabel.setText(String.format("💰 Total: $%.2f", total));
    }

    private double calculateTotal() {
        double total = 0;
        for (Seat seat : selectedSeats) {
            if ("VIP".equals(seat.getSeatType())) {
                total += VIP_PRICE;
            } else if ("REGULAR".equals(seat.getSeatType())) {
                total += REGULAR_PRICE;
            } else {
                total += STANDARD_PRICE;
            }
        }
        return total;
    }

    public void updateSchedule(Schedule schedule) {
        this.schedule = schedule;
        clearSelection();
        loadBookedSeats();
        revalidate();
        repaint();
    }

    private void loadBookedSeats() {
        if (schedule != null) {
            try {
                BookingService bookingService = new BookingService();
                List<Integer> bookedSeatIds = bookingService.getBookedSeats(schedule.getId());

                for (int i = 0; i < seatButtons.size() && i < bookedSeatIds.size(); i++) {
                    if (bookedSeatIds.contains(i + 1)) {
                        JToggleButton btn = seatButtons.get(i);
                        btn.setEnabled(false);
                        btn.setBackground(COLOR_BOOKED);
                        btn.setToolTipText("🔴 Already Booked");
                        btn.setBorder(BorderFactory.createLoweredBevelBorder());
                    }
                }
            } catch (DatabaseException e) {
                // No booked seats to load
            }
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public void clearSelection() {
        selectedSeats.clear();
        for (int i = 0; i < seatButtons.size(); i++) {
            JToggleButton button = seatButtons.get(i);
            button.setSelected(false);

            int row = i / cols;
            Color originalColor;
            String tooltip;

            if (row < 2) {
                originalColor = COLOR_VIP;
                tooltip = String.format("🎖️ VIP Seat %s - $%.2f", button.getText(), VIP_PRICE);
            } else if (row < 7) {
                originalColor = COLOR_REGULAR;
                tooltip = String.format("💺 Regular Seat %s - $%.2f", button.getText(), REGULAR_PRICE);
            } else {
                originalColor = COLOR_STANDARD;
                tooltip = String.format("💺 Standard Seat %s - $%.2f", button.getText(), STANDARD_PRICE);
            }

            button.setBackground(originalColor);
            button.setToolTipText(tooltip);
            button.setBorder(BorderFactory.createRaisedBevelBorder());

            if (!button.isEnabled()) {
                button.setEnabled(true);
                button.setBackground(originalColor);
            }
        }
        updateSummary();
    }
    public double getTotalPrice() {
        double total = 0;
        for (Seat seat : selectedSeats) {
            if ("VIP".equals(seat.getSeatType())) {
                total += VIP_PRICE;
            } else if ("REGULAR".equals(seat.getSeatType())) {
                total += REGULAR_PRICE;
            } else {
                total += STANDARD_PRICE;
            }
        }
        return total;
    }
    public void debugSeatTotals() {
        System.out.println("=== SEAT SELECTION DEBUG ===");
        System.out.println("Selected Seats: " + selectedSeats.size());
        for (Seat seat : selectedSeats) {
            System.out.println("  Seat " + seat.getSeatNumber() + " - " + seat.getSeatType() + " - $" +
                    ("VIP".equals(seat.getSeatType()) ? VIP_PRICE :
                            ("REGULAR".equals(seat.getSeatType()) ? REGULAR_PRICE : STANDARD_PRICE)));
        }
        System.out.println("Total: $" + getTotalPrice());
    }
}