package panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.*;

public class BookingConfirmationPanel extends JPanel {
    private JTextArea confirmationArea;

    public BookingConfirmationPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Booking Confirmed!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        confirmationArea = new JTextArea();
        confirmationArea.setEditable(false);
        confirmationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        confirmationArea.setBackground(new Color(248, 249, 250));
        JScrollPane scrollPane = new JScrollPane(confirmationArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton printBtn = new JButton("🖨️ Print Ticket");
        printBtn.addActionListener(e -> printTicket());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setBookingDetails(Movie movie, Schedule schedule, List<Seat> seats,
                                  List<FoodOrderPanel.FoodItem> foods, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        BOOKING CONFIRMATION\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("🎬 Movie: ").append(movie.getTitle()).append("\n");
        sb.append("📅 Date: ").append(schedule.getStartTime()).append("\n");
        sb.append("💺 Seats: ");
        for (Seat seat : seats) {
            sb.append(seat.getSeatNumber()).append(" ");
        }
        sb.append("\n\n🍿 Food Items:\n");
        for (FoodOrderPanel.FoodItem food : foods) {
            sb.append("   • ").append(food.getName()).append(" - $").append(food.getPrice()).append("\n");
        }
        sb.append("\n💰 Total Amount: $").append(String.format("%.2f", total)).append("\n");
        sb.append("\n📧 A confirmation email has been sent to your email address.\n");
        sb.append("📱 Please show this ticket at the entrance.\n");
        sb.append("\n═══════════════════════════════════════\n");
        sb.append("     Thank you for choosing us!\n");
        sb.append("═══════════════════════════════════════\n");

        confirmationArea.setText(sb.toString());
    }

    private void printTicket() {
        try {
            boolean complete = confirmationArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Ticket printed successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage());
        }
    }
    public void setBookingDetails(Movie movie, Schedule schedule, List<Seat> seats,
                                  List<FoodOrderPanel.FoodItem> foods, double total,
                                  String customerName, String customerEmail,
                                  String customerPhone, String paymentMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        BOOKING CONFIRMATION\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("👤 Customer: ").append(customerName).append("\n");
        sb.append("📧 Email: ").append(customerEmail).append("\n");
        sb.append("📞 Phone: ").append(customerPhone).append("\n\n");
        sb.append("🎬 Movie: ").append(movie.getTitle()).append("\n");
        sb.append("📅 Date: ").append(schedule.getStartTime()).append("\n");
        sb.append("💺 Seats: ");
        for (Seat seat : seats) {
            sb.append(seat.getSeatNumber()).append(" ");
        }
        sb.append("\n\n🍿 Food Items:\n");
        for (FoodOrderPanel.FoodItem food : foods) {
            sb.append("   • ").append(food.getName()).append(" - $").append(food.getPrice()).append("\n");
        }
        sb.append("\n💰 Total Amount: $").append(String.format("%.2f", total)).append("\n");
        sb.append("💳 Payment Method: ").append(paymentMethod).append("\n");
        sb.append("\n📧 A confirmation email has been sent to your email address.\n");
        sb.append("📱 Please show this ticket at the entrance.\n");
        sb.append("\n═══════════════════════════════════════\n");
        sb.append("     Thank you for choosing us!\n");
        sb.append("═══════════════════════════════════════\n");

        confirmationArea.setText(sb.toString());
    }
}