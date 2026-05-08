package ui;

import javax.swing.*;
import java.awt.*;
import model.*;
import panels.*;
import util.DBConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookingWizardDialog extends JDialog {
    private Movie movie;
    private User currentUser;
    private boolean bookingConfirmed = false;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int currentStep = 0;

    // USE YOUR SEPARATE PANEL CLASSES
    private ShowtimeSelectionPanel showtimePanel;
    private SeatSelectionPanel seatPanel;
    private FoodOrderPanel foodPanel;
    private CustomerDetailsPanel customerPanel;
    private PaymentPanel paymentPanel;
    private BookingConfirmationPanel confirmationPanel;

    // Data holders
    private Schedule selectedSchedule;
    private double totalAmount = 0;
    private String paymentMethod = "Credit Card";
    private double ticketTotal = 0;
    private double foodTotal = 0;

    // UI Components
    private JLabel stepLabel;
    private JButton prevBtn;
    private JButton nextBtn;

    public BookingWizardDialog(Frame parent, Movie movie, User user) {
        super(parent, "Book Tickets - " + movie.getTitle(), true);
        this.movie = movie;
        this.currentUser = user;
        initComponents();
        setSize(950, 750);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Content with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // CREATE YOUR SEPARATE PANEL INSTANCES
        showtimePanel = new ShowtimeSelectionPanel(movie);
        seatPanel = new SeatSelectionPanel(null, 10, 15);
        foodPanel = new FoodOrderPanel();
        customerPanel = new CustomerDetailsPanel();
        paymentPanel = new PaymentPanel();
        confirmationPanel = new BookingConfirmationPanel();

        // ADD THEM TO THE CARD LAYOUT
        contentPanel.add(showtimePanel, "step1");
        contentPanel.add(seatPanel, "step2");
        contentPanel.add(foodPanel, "step3");
        contentPanel.add(customerPanel, "step4");
        contentPanel.add(paymentPanel, "step5");
        contentPanel.add(confirmationPanel, "step6");

        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        cardLayout.show(contentPanel, "step1");
        updateStepDisplay();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🎬 " + movie.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        stepLabel = new JLabel("Step 1 of 6: Select Showtime");
        stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stepLabel.setForeground(Color.WHITE);
        header.add(stepLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        prevBtn = new JButton("← Previous");
        prevBtn.setEnabled(false);
        prevBtn.addActionListener(e -> goToPrevious());

        nextBtn = new JButton("Next →");
        nextBtn.setBackground(new Color(46, 204, 113));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nextBtn.addActionListener(e -> goToNext());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        panel.add(prevBtn);
        panel.add(nextBtn);
        panel.add(cancelBtn);

        return panel;
    }

    private void goToNext() {
        if (currentStep < 5) {
            if (!validateCurrentStep()) {
                return;
            }
            currentStep++;
            updateStepDisplay();
            cardLayout.next(contentPanel);
            prevBtn.setEnabled(true);

            if (currentStep == 5) {
                nextBtn.setText("Confirm Booking");
                updatePaymentSummary();
                // Force refresh of payment panel display
                paymentPanel.refresh();
            }
        } else if (currentStep == 5) {
            if (validateCurrentStep()) {
                saveBookingToDatabase();
                bookingConfirmed = true;
                currentStep++;
                updateStepDisplay();
                cardLayout.next(contentPanel);
                nextBtn.setEnabled(false);
                updateConfirmationDetails();
            }
        }
    }

    private void goToPrevious() {
        if (currentStep > 0) {
            currentStep--;
            updateStepDisplay();
            cardLayout.previous(contentPanel);
            prevBtn.setEnabled(currentStep > 0);
            nextBtn.setEnabled(true);
            nextBtn.setText(currentStep == 5 ? "Confirm Booking" : "Next →");

            // If going back to payment step, refresh the summary
            if (currentStep == 4) {
                updatePaymentSummary();
                paymentPanel.refresh();
            }
        }
    }

    private void updateStepDisplay() {
        String[] stepNames = {
                "Select Showtime", "Select Seats", "Food & Drinks",
                "Customer Details", "Payment", "Confirmation"
        };
        stepLabel.setText("Step " + (currentStep + 1) + " of 6: " + stepNames[currentStep]);
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 0: // Showtime
                selectedSchedule = showtimePanel.getSelectedSchedule();
                if (selectedSchedule == null) {
                    JOptionPane.showMessageDialog(this, "Please select a show time!");
                    return false;
                }
                seatPanel.updateSchedule(selectedSchedule);
                return true;

            case 1: // Seats
                if (seatPanel.getSelectedSeats().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select at least one seat!");
                    return false;
                }
                // Update totals after seat selection
                updatePaymentSummary();
                return true;

            case 2: // Food - optional
                // Update totals after food selection
                updatePaymentSummary();
                return true;

            case 3: // Customer Details - DIRECT VALIDATION WITHOUT BUTTON
                String name = customerPanel.getCustomerName();
                String email = customerPanel.getCustomerEmail();
                String phone = customerPanel.getCustomerPhone();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter your full name!");
                    return false;
                }

                if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid email address!");
                    return false;
                }

                if (phone.isEmpty() || phone.length() < 10 || !phone.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid phone number (10-11 digits)!");
                    return false;
                }

                return true;

            case 4: // Payment
                // Just need to confirm payment, no card details needed
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirm payment of $" + String.format("%.2f", totalAmount) +
                                " via " + paymentPanel.getSelectedPaymentMethod() + "?",
                        "Confirm Payment",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    paymentMethod = paymentPanel.getSelectedPaymentMethod();
                    JOptionPane.showMessageDialog(this, "✅ Payment Successful!");
                    return true;
                }
                return false;
            default:
                return true;
        }
    }

    private void updatePaymentSummary() {
        // Get ticket total from seat panel
        ticketTotal = seatPanel.getTotalPrice();

        // Get food total from food panel
        foodTotal = 0;
        List<FoodOrderPanel.FoodItem> selectedFoods = foodPanel.getSelectedFoods();
        for (FoodOrderPanel.FoodItem item : selectedFoods) {
            foodTotal += item.getPrice();
        }

        // Calculate grand total
        totalAmount = ticketTotal + foodTotal;

        // Update payment panel
        paymentPanel.setAmount(totalAmount);

        // Debug output
        System.out.println("========== PAYMENT SUMMARY ==========");
        System.out.println("Selected Seats: " + seatPanel.getSelectedSeats().size());
        System.out.println("Ticket Total: $" + ticketTotal);
        System.out.println("Food Items: " + selectedFoods.size());
        System.out.println("Food Total: $" + foodTotal);
        System.out.println("Grand Total: $" + totalAmount);
        System.out.println("======================================");
    }

    private void updateConfirmationDetails() {
        // Get fresh customer data
        String customerName = customerPanel.getCustomerName();
        String customerEmail = customerPanel.getCustomerEmail();
        String customerPhone = customerPanel.getCustomerPhone();

        confirmationPanel.setBookingDetails(
                movie,
                selectedSchedule,
                seatPanel.getSelectedSeats(),
                foodPanel.getSelectedFoods(),
                totalAmount,
                customerName,
                customerEmail,
                customerPhone,
                paymentMethod
        );
    }

    private void saveBookingToDatabase() {
        try {
            java.sql.Connection conn = DBConnection.getConnection();

            // First, get or create a schedule_id (for demo, use 1)
            int scheduleId = selectedSchedule != null ? selectedSchedule.getId() : 1;

            // Insert booking
            String sql = "INSERT INTO bookings (user_id, schedule_id, customer_name, customer_email, customer_phone, " +
                    "total_amount, status, payment_method, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, currentUser != null ? currentUser.getId() : 0);
            ps.setInt(2, scheduleId);
            ps.setString(3, customerPanel.getCustomerName());
            ps.setString(4, customerPanel.getCustomerEmail());
            ps.setString(5, customerPanel.getCustomerPhone());
            ps.setDouble(6, totalAmount);
            ps.setString(7, "CONFIRMED");
            ps.setString(8, paymentMethod);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                int bookingId = 0;
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                }

                // Insert tickets for each selected seat
                if (bookingId > 0 && seatPanel.getSelectedSeats() != null) {
                    String ticketSql = "INSERT INTO tickets (booking_id, seat_id, price, ticket_type) VALUES (?, ?, ?, 'ADULT')";
                    PreparedStatement ticketPs = conn.prepareStatement(ticketSql);

                    // For demo, use seat IDs 1,2,3... (you may need to map actual seat IDs)
                    int seatCounter = 1;
                    for (Seat seat : seatPanel.getSelectedSeats()) {
                        ticketPs.setInt(1, bookingId);
                        ticketPs.setInt(2, seatCounter++); // Simplified seat_id mapping
                        ticketPs.setDouble(3, seatPanel.getTotalPrice() / seatPanel.getSelectedSeats().size());
                        ticketPs.addBatch();
                    }
                    ticketPs.executeBatch();
                    ticketPs.close();
                }

                JOptionPane.showMessageDialog(this,
                        "✅ Booking saved successfully!\n" +
                                "Booking ID: " + bookingId + "\n" +
                                "Confirmation sent to: " + customerPanel.getCustomerEmail(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving booking: " + e.getMessage() + "\n\n" +
                            "Please check your database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public boolean isBookingConfirmed() {
        return bookingConfirmed;
    }
}