package panels;

import javax.swing.*;
import java.awt.*;
import model.User;
import service.UserService;
import exception.DatabaseException;
import util.Constants;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private UserService userService;
    private LoginListener loginListener;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color BORDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_LIGHT = Color.WHITE;
    private final Color TEXT_GRAY = new Color(127, 140, 141);

    public interface LoginListener {
        void onLoginSuccess(User user);
    }

    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;
        this.userService = new UserService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);

        // Create main card panel
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo / Icon
        JLabel logoLabel = new JLabel("🎬", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cardPanel.add(logoLabel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Cinema Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        cardPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        cardPanel.add(subtitleLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridy = 3;
        cardPanel.add(separator, gbc);

        // Username field
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(TEXT_DARK);
        cardPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(18);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.add(usernameField, gbc);

        // Password field
        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT_DARK);
        cardPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.add(passwordField, gbc);

        // Role selection
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel roleLabel = new JLabel("Login as");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(TEXT_DARK);
        cardPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        String[] roles = {"👑 Administrator", "📊 Manager", "🎫 Front Desk", "🎬 Customer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleComboBox.setBackground(CARD_COLOR);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.add(roleComboBox, gbc);

        // Login button
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = createStyledButton("Sign In", PRIMARY_COLOR, TEXT_LIGHT);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.addActionListener(e -> login());
        cardPanel.add(loginButton, gbc);

        // Demo info collapsible panel
        JPanel demoPanel = createDemoPanel();
        gbc.gridy = 8;
        gbc.insets = new Insets(15, 10, 5, 10);
        cardPanel.add(demoPanel, gbc);

        // Clear button
        gbc.gridy = 9;
        JButton clearButton = createStyledButton("Clear Fields", SECONDARY_COLOR, TEXT_LIGHT);
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.addActionListener(e -> clearFields());
        cardPanel.add(clearButton, gbc);

        // Add card panel to center of screen
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        add(cardPanel, mainGbc);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JPanel createDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel demoTitle = new JLabel("📋 Demo Accounts");
        demoTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        demoTitle.setForeground(SECONDARY_COLOR);
        panel.add(demoTitle, BorderLayout.NORTH);

        JPanel accountsPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        accountsPanel.setBackground(new Color(245, 245, 245));

        accountsPanel.add(createAccountLabel("👑 Admin"));
        accountsPanel.add(createAccountLabel("admin / admin123"));
        accountsPanel.add(createAccountLabel("📊 Manager"));
        accountsPanel.add(createAccountLabel("manager1 / manager123"));
        accountsPanel.add(createAccountLabel("🎫 Front Desk"));
        accountsPanel.add(createAccountLabel("staff1 / staff123"));
        accountsPanel.add(createAccountLabel("🎬 Customer"));
        accountsPanel.add(createAccountLabel("customer1 / customer123"));

        panel.add(accountsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createAccountLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_GRAY);
        return label;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        String selectedRole = (String) roleComboBox.getSelectedItem();
        String expectedRole = "";

        if (selectedRole.contains("Administrator")) expectedRole = "ADMIN";
        else if (selectedRole.contains("Manager")) expectedRole = "MANAGER";
        else if (selectedRole.contains("Front Desk")) expectedRole = "FRONT_DESK";
        else if (selectedRole.contains("Customer")) expectedRole = "CUSTOMER";

        // DEMO MODE - Allow login without database for testing
        User demoUser = new User();
        demoUser.setUsername(username);
        demoUser.setName(username.equals("admin") ? "Admin User" : username);
        demoUser.setRole(expectedRole);
        demoUser.setId(1);

        showSuccess("Demo Login: Welcome " + demoUser.getName() + "!\nRole: " + demoUser.getRole());

        if (loginListener != null) {
            loginListener.onLoginSuccess(demoUser);
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Demo Mode", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        usernameField.requestFocus();
    }
}