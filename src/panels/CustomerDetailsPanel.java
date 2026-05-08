package panels;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CustomerDetailsPanel extends JPanel {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private boolean validated = false;
    private JButton validateBtn;
    private JLabel statusLabel;

    // Validation flags
    private boolean nameValid = false;
    private boolean emailValid = false;
    private boolean phoneValid = false;

    // Modern colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color ERROR_COLOR = new Color(231, 76, 60);
    private final Color BORDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_DARK = new Color(44, 62, 80);

    public CustomerDetailsPanel() {
        initComponents();
        setupDocumentListeners();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title with icon
        JLabel titleLabel = new JLabel("📝 Customer Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please provide your contact details");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 12, 15, 12);
        add(separator, gbc);

        gbc.insets = new Insets(10, 12, 10, 12);

        // Full Name
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel nameLabel = new JLabel("Full Name *");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_DARK);
        add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = createStyledTextField();
        nameField.setToolTipText("Enter your full name");
        add(nameField, gbc);

        // Email
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email Address *");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(TEXT_DARK);
        add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = createStyledTextField();
        emailField.setToolTipText("Enter your email address");
        add(emailField, gbc);

        // Phone
        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel phoneLabel = new JLabel("Phone Number *");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLabel.setForeground(TEXT_DARK);
        add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = createStyledTextField();
        phoneField.setToolTipText("Enter your phone number (10-11 digits)");
        add(phoneField, gbc);

        // Status label
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, gbc);

        // Validate button
        gbc.gridy = 7;
        validateBtn = createStyledButton("✓ Validate Information", SUCCESS_COLOR);
        validateBtn.addActionListener(e -> validateInfo());
        add(validateBtn, gbc);

        // Info note
        gbc.gridy = 8;
        JLabel infoLabel = new JLabel("💡 Your tickets will be sent to this email");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(infoLabel, gbc);
    }

    private void setupDocumentListeners() {
        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { checkFieldValidation(); }
            public void removeUpdate(DocumentEvent e) { checkFieldValidation(); }
            public void insertUpdate(DocumentEvent e) { checkFieldValidation(); }
        };

        nameField.getDocument().addDocumentListener(listener);
        emailField.getDocument().addDocumentListener(listener);
        phoneField.getDocument().addDocumentListener(listener);
    }

    private void checkFieldValidation() {
        // Check name
        String name = nameField.getText().trim();
        nameValid = !name.isEmpty();

        // Check email
        String email = emailField.getText().trim();
        emailValid = !email.isEmpty() && email.contains("@") && email.contains(".");

        // Check phone
        String phone = phoneField.getText().trim();
        phoneValid = !phone.isEmpty() && phone.length() >= 10 && phone.matches("\\d+");

        // Update field borders
        updateFieldBorder(nameField, nameValid);
        updateFieldBorder(emailField, emailValid);
        updateFieldBorder(phoneField, phoneValid);

        // Update status label
        if (nameValid && emailValid && phoneValid) {
            statusLabel.setText("✓ All fields are valid! Click 'Validate Information' to continue.");
            statusLabel.setForeground(SUCCESS_COLOR);
            validateBtn.setEnabled(true);
        } else {
            statusLabel.setText("⚠️ Please fill all fields correctly before validating.");
            statusLabel.setForeground(ERROR_COLOR);
            validateBtn.setEnabled(false);
            validated = false;
        }
    }

    private void updateFieldBorder(JTextField field, boolean isValid) {
        if (isValid) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)
            ));
        } else if (!field.getText().trim().isEmpty()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ERROR_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)
            ));
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
        }
    }

    private void resetValidation() {
        validated = false;
        validateBtn.setBackground(SUCCESS_COLOR);
        validateBtn.setText("✓ Validate Information");
        validateBtn.setEnabled(nameValid && emailValid && phoneValid);

        if (nameValid && emailValid && phoneValid) {
            statusLabel.setText("✓ All fields are valid! Click 'Validate Information' to continue.");
            statusLabel.setForeground(SUCCESS_COLOR);
        } else {
            statusLabel.setText("⚠️ Please fill all fields correctly before validating.");
            statusLabel.setForeground(ERROR_COLOR);
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setEnabled(false); // Initially disabled until fields are valid

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });

        return button;
    }

    private void validateInfo() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Double-check validation before proceeding
        if (name.isEmpty()) {
            showError("Please enter your full name!");
            nameField.requestFocus();
            return;
        }

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return;
        }

        if (phone.isEmpty() || phone.length() < 10 || !phone.matches("\\d+")) {
            showError("Please enter a valid phone number (10-11 digits)!");
            phoneField.requestFocus();
            return;
        }

        validated = true;
        validateBtn.setBackground(SUCCESS_COLOR.darker());
        validateBtn.setText("✓ Validated!");
        validateBtn.setEnabled(false);

        statusLabel.setText("✓ Information validated successfully! You can proceed to payment.");
        statusLabel.setForeground(SUCCESS_COLOR);

        showSuccess("Information validated successfully!\nProceed to payment.");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isValidated() {
        return validated;
    }

    public String getCustomerName() {
        return nameField.getText().trim();
    }

    public String getCustomerEmail() {
        return emailField.getText().trim();
    }

    public String getCustomerPhone() {
        return phoneField.getText().trim();
    }

    public void reset() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        validated = false;
        nameValid = false;
        emailValid = false;
        phoneValid = false;
        validateBtn.setBackground(SUCCESS_COLOR);
        validateBtn.setText("✓ Validate Information");
        validateBtn.setEnabled(false);

        // Reset borders
        updateFieldBorder(nameField, false);
        updateFieldBorder(emailField, false);
        updateFieldBorder(phoneField, false);

        statusLabel.setText("⚠️ Please fill all fields correctly before validating.");
        statusLabel.setForeground(ERROR_COLOR);
    }
}