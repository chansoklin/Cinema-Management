package panels;

import javax.swing.*;
import java.awt.*;

public class PaymentPanel extends JPanel {
    private JComboBox<String> paymentMethodCombo;
    private JLabel amountLabel;
    private double amount;

    public PaymentPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("💳 Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Complete your booking by selecting payment method");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 15, 20, 15);
        add(separator, gbc);

        gbc.insets = new Insets(15, 15, 15, 15);

        // Amount to Pay
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel amountTitleLabel = new JLabel("Total Amount:");
        amountTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountTitleLabel.setForeground(new Color(44, 62, 80));
        add(amountTitleLabel, gbc);

        gbc.gridx = 1;
        amountLabel = new JLabel("$0.00");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        amountLabel.setForeground(new Color(46, 204, 113));
        add(amountLabel, gbc);

        // Payment Method
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        paymentMethodCombo = new JComboBox<>(new String[]{
                "💳 Credit Card",
                "💳 Debit Card",
                "💵 Cash",
                "📱 Mobile Payment"
        });
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentMethodCombo.setPreferredSize(new Dimension(200, 35));
        add(paymentMethodCombo, gbc);

        // Pay Button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton payBtn = new JButton("Confirm & Pay");
        payBtn.setBackground(new Color(46, 204, 113));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.setPreferredSize(new Dimension(250, 50));
        payBtn.addActionListener(e -> processPayment());
        add(payBtn, gbc);

        // Info note
        gbc.gridy = 6;
        JLabel infoLabel = new JLabel("💡 Your payment is secure and encrypted");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        add(infoLabel, gbc);
    }

    public void setAmount(double amount) {
        this.amount = amount;
        amountLabel.setText(String.format("$%.2f", amount));
    }

    public boolean processPayment() {
        String method = (String) paymentMethodCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment of $" + String.format("%.2f", amount) + " via " + method + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "✅ Payment Successful!\n\n" +
                            "Your booking has been confirmed.\n" +
                            "A confirmation email has been sent to your email address.",
                    "Payment Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    public String getSelectedPaymentMethod() {
        String method = (String) paymentMethodCombo.getSelectedItem();
        // Remove emoji for database storage
        return method.replace("💳 ", "").replace("💵 ", "").replace("📱 ", "");
    }

    public void refresh() {
        amountLabel.setText(String.format("$%.2f", amount));
        amountLabel.repaint();
    }
}