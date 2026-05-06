package panels;

import manager.Movie;
import main.CinemaManagementSystem;
import model.User;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class FrontDeskPanel extends JPanel {
    private final CinemaManagementSystem system;
    private User currentUser;
    private JLabel titleLabel;
    private JTextField seatField;
    private final Map<String, Set<String>> soldSeatsMap = new HashMap<>();
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private final Color ACCENT_COLOR = new Color(255, 215, 0);
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private List<String> currentSelectedSeats = new ArrayList<>();

    // Update your seat selection handlers to modify this list
    public FrontDeskPanel(CinemaManagementSystem system, User user) {
        this.system = system;
        this.currentUser = user;
        initUI();
        refreshSoldSeats();
    }


    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    public void updateUI() {
        if (titleLabel != null) {
            titleLabel.setText("前台面板 - 登录用户: " +
                    (currentUser != null ? currentUser.getUsername() : "未登录"));
        }
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
                return icon;
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path + " - " + e.getMessage());
        }
        return null;
    }
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        titleLabel = new JLabel("前台面板 - 登录用户: " +
                (currentUser != null ? currentUser.getUsername() : "未登录"));
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Time display
        JLabel timeLabel = new JLabel(getCurrentDateTime());
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        timeLabel.setForeground(Color.BLACK);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contentPanel.setBackground(BACKGROUND_COLOR);

        String[] menuItems = {
                "售票", "退票", "查询影片",
                "查询场次", "座位查询", "退出程序"
        };

        for (String item : menuItems) {
            JButton button = createMenuButton(item);
            button.addActionListener(e -> handleAction(item));
            contentPanel.add(button);
        }

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 80, 60));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> system.showLoginPanel());
        footerPanel.add(logoutButton);

        add(footerPanel, BorderLayout.SOUTH);

        // Update time every minute
        new Timer(60000, e -> timeLabel.setText(getCurrentDateTime())).start();
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.black);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 110, 150), 2),
                new EmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
        });

        return button;
    }

    private void handleAction(String action) {
        switch (action) {
            case "售票":
                showTicketSalesDialog();
                break;
            case "退票":
                showRefundDialog();
                break;
            case "查询影片":
                showMoviesInfo();
                break;
            case "查询场次":
                showShowtimesDialog();
                break;
            case "座位查询":
                // Create a simple dialog to get schedule info first
                showSeatQueryDialog();
                break;
            case "退出程序":
                system.exitApplication();
                break;
        }
    }
    // Add a method to refresh sold seats from system data
    private void refreshSoldSeats() {
        soldSeatsMap.clear();
        for (CinemaManagementSystem.Ticket ticket : system.getTickets()) {
            String[] seats = ticket.getSeats().split(",");
            soldSeatsMap.computeIfAbsent(ticket.getScheduleId(), k -> new HashSet<>())
                    .addAll(Arrays.asList(seats));
        }
    }
    private void showTicketSalesDialog() {
        // Create dialog with modern design
        JDialog dialog = new JDialog(system, "电影售票系统", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 650); // Slightly larger
        dialog.setLocationRelativeTo(system);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Modern color scheme
        Color primaryColor = new Color(0, 102, 204); // Deep blue
        Color secondaryColor = new Color(240, 240, 245); // Light gray
        Color accentColor = new Color(255, 87, 34); // Orange accent
        Color textColor = new Color(60, 60, 60); // Dark gray text

        // Main content panel with modern styling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(secondaryColor);

        // Modern header panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, primaryColor.brighter(),
                        getWidth(), 0, primaryColor.darker()
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("电影售票", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Form container with modern card-like appearance
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create modern form rows with improved styling
        Font labelFont = new Font("微软雅黑", Font.BOLD, 16);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 15);
        Font smallFont = new Font("微软雅黑", Font.PLAIN, 13);

        // Movie selection
        JPanel moviePanel = createModernFormRow("选择电影", "请选择要观看的电影", labelFont, smallFont, textColor);
        JComboBox<String> movieComboBox = (JComboBox<String>) createModernComboBox(inputFont);
        for (Movie movie : system.getMovies()) {
            if (movie.isScreening()) {
                movieComboBox.addItem(movie.getTitle());
            }
        }
        moviePanel.add(movieComboBox);
        formPanel.add(moviePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Showtime selection
        JPanel showtimePanel = createModernFormRow("选择场次", "请选择放映时间", labelFont, smallFont, textColor);
        JComboBox<CinemaManagementSystem.Schedule> scheduleComboBox = (JComboBox<CinemaManagementSystem.Schedule>) createModernComboBox(inputFont);
        showtimePanel.add(scheduleComboBox);
        formPanel.add(showtimePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Update schedules when movie changes
        movieComboBox.addActionListener(e -> updateSchedules(movieComboBox, scheduleComboBox));

        // Seat selection
        JPanel seatPanel = createModernFormRow("选择座位", "请选择座位号", labelFont, smallFont, textColor);
        JTextField seatField = new JTextField();
        seatField.setFont(inputFont);
        seatField.setEditable(false);
        seatField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        seatField.setBackground(new Color(250, 250, 255));

        JButton selectSeatsButton = new JButton("选择座位");
        selectSeatsButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        selectSeatsButton.setBackground(primaryColor);
        selectSeatsButton.setForeground(Color.WHITE);
        selectSeatsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        selectSeatsButton.setFocusPainted(false);
        selectSeatsButton.addActionListener(e -> handleSeatSelection(dialog, scheduleComboBox, seatField));

        JPanel seatInputPanel = new JPanel(new BorderLayout(10, 0));
        seatInputPanel.add(seatField, BorderLayout.CENTER);
        seatInputPanel.add(selectSeatsButton, BorderLayout.EAST);
        seatInputPanel.setBackground(Color.WHITE);
        seatPanel.add(seatInputPanel);
        formPanel.add(seatPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Ticket type selection
        JPanel ticketTypePanel = createModernFormRow("票型", "请选择票的类型", labelFont, smallFont, textColor);
        ButtonGroup ticketGroup = new ButtonGroup();

        JRadioButton adultButton = createModernRadioButton("成人票", true, primaryColor, inputFont);
        JRadioButton childButton = createModernRadioButton("儿童票 (7折)", false, primaryColor, inputFont);

        // Age verification for child tickets
        JPanel childAgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        childAgePanel.setBackground(Color.WHITE);
        JLabel ageLabel = new JLabel("儿童年龄:");
        ageLabel.setFont(smallFont);
        ageLabel.setForeground(textColor);
        JSpinner ageSpinner = createModernSpinner(6, 0, 17, inputFont);
        ageSpinner.setEnabled(false);

        childAgePanel.add(ageLabel);
        childAgePanel.add(ageSpinner);

        // Toggle age spinner based on selection
        childButton.addActionListener(e -> ageSpinner.setEnabled(true));
        adultButton.addActionListener(e -> ageSpinner.setEnabled(false));

        ticketGroup.add(adultButton);
        ticketGroup.add(childButton);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(adultButton);
        radioPanel.add(childButton);
        radioPanel.add(childAgePanel);

        ticketTypePanel.add(radioPanel);
        formPanel.add(ticketTypePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Price display with modern styling
        JPanel pricePanel = createModernFormRow("价格", "应付金额", labelFont, smallFont, textColor);
        JLabel priceLabel = new JLabel("￥0.00");
        priceLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        priceLabel.setForeground(accentColor);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        pricePanel.add(priceLabel);
        formPanel.add(pricePanel);

        // Price update logic
        ActionListener updatePrice = e -> updatePriceDisplay(
                scheduleComboBox,
                childButton,
                adultButton,
                ageSpinner,
                priceLabel,
                dialog
        );

        // Add listeners for price updates
        scheduleComboBox.addActionListener(updatePrice);
        adultButton.addActionListener(updatePrice);
        childButton.addActionListener(updatePrice);
        ageSpinner.addChangeListener(e -> updatePrice.actionPerformed(null));

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalGlue());

        // Modern button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.setBackground(secondaryColor);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        cancelButton.setBackground(new Color(230, 230, 235));
        cancelButton.setForeground(textColor);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton confirmButton = new JButton("确认售票");
        confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
        confirmButton.setBackground(primaryColor);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        confirmButton.addActionListener(e -> handleTicketConfirmationWithoutImages(
                dialog,
                movieComboBox,
                scheduleComboBox,
                seatField,
                childButton,
                ageSpinner
        ));

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalGlue());

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add subtle shadow effect
        dialog.getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 2, 1, new Color(180, 180, 190)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));

        dialog.setVisible(true);
    }

    // Helper methods for modern components
    private JPanel createModernFormRow(String title, String subtitle, Font titleFont, Font subtitleFont, Color textColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(textColor.brighter());
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(titleLabel);
        panel.add(subtitleLabel);

        return panel;
    }

    private JComboBox<?> createModernComboBox(Font font) {
        JComboBox<Object> comboBox = new JComboBox<>();
        comboBox.setFont(font);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(font);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        return comboBox;
    }

    private JRadioButton createModernRadioButton(String text, boolean selected, Color color, Font font) {
        JRadioButton radioButton = new JRadioButton(text, selected);
        radioButton.setFont(font);
        radioButton.setBackground(Color.WHITE);
        radioButton.setForeground(color.darker());
        radioButton.setFocusPainted(false);
        radioButton.setIcon(new RadioButtonIcon(color));
        return radioButton;
    }

    private JSpinner createModernSpinner(int value, int min, int max, Font font) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(font);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(Color.WHITE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 230)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        return spinner;
    }

    // Custom radio button icon for modern look
    private static class RadioButtonIcon implements Icon {
        private final Color color;

        public RadioButtonIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();

            // Outer circle
            g2.setColor(color);
            g2.drawOval(x+1, y+1, 14, 14);

            // Inner circle when selected
            if (model.isSelected()) {
                g2.setColor(color);
                g2.fillOval(x+4, y+4, 8, 8);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }
    private void handleTicketConfirmationWithoutImages(JDialog dialog,
                                                       JComboBox<String> movieComboBox,
                                                       JComboBox<CinemaManagementSystem.Schedule> scheduleComboBox,
                                                       JTextField seatField,
                                                       JRadioButton childButton,
                                                       JSpinner ageSpinner) {

        CinemaManagementSystem.Schedule selectedSchedule = (CinemaManagementSystem.Schedule) scheduleComboBox.getSelectedItem();
        if (selectedSchedule == null || seatField.getText().isEmpty()) {
            showStyledErrorDialog(dialog, "请选择场次和座位", "错误");
            return;
        }

        // Validate child age if child ticket is selected
        if (childButton.isSelected()) {
            int age = (int) ageSpinner.getValue();
            if (age > 12) {
                showStyledWarningDialog(dialog, "13岁及以上请购买成人票", "年龄限制");
                return;
            }
        }

        // Sell ticket
        CinemaManagementSystem.Ticket ticket = system.sellTicket(
                (String) movieComboBox.getSelectedItem(),
                selectedSchedule.getId(),
                seatField.getText(),
                childButton.isSelected()
        );

        if (ticket != null) {
            showSuccessTicketDialog(dialog, ticket);
            dialog.dispose();
        } else {
            showStyledErrorDialog(dialog, "售票失败，请重试", "错误");
        }
    }

    private void showStyledErrorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
                "<html><div style='width:300px;color:red;'>" + message + "</div></html>",
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showStyledWarningDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
                "<html><div style='width:300px;color:orange;'>" + message + "</div></html>",
                title,
                JOptionPane.WARNING_MESSAGE);
    }
    private void showSuccessTicketDialog(JDialog parent, CinemaManagementSystem.Ticket ticket) {
        JDialog successDialog = new JDialog(parent, "售票成功", true);
        successDialog.setLayout(new BorderLayout());
        successDialog.setSize(500, 400);  // Increased size
        successDialog.setLocationRelativeTo(parent);

        // Main content panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with success icon
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(76, 175, 80)); // Green color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel successLabel = new JLabel("售票成功!", JLabel.CENTER);
        successLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));  // Larger font
        successLabel.setForeground(Color.WHITE);
        headerPanel.add(successLabel);

        // Add icon if available
        try {
            ImageIcon successIcon = new ImageIcon(getClass().getResource("/images/success.png"));
            successIcon.setImage(successIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
            successLabel.setIcon(successIcon);
            successLabel.setIconTextGap(15);
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Ticket details panel with scroll
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Larger detail rows
        addEnhancedDetailRow(detailsPanel, "电子票号:", ticket.getId());
        addEnhancedDetailRow(detailsPanel, "电影名称:", ticket.getMovieTitle());
        addEnhancedDetailRow(detailsPanel, "放映场次:", ticket.getScheduleId());
        addEnhancedDetailRow(detailsPanel, "放映时间:", getFormattedShowtime(ticket));
        addEnhancedDetailRow(detailsPanel, "座位号码:", ticket.getSeats());
        addEnhancedDetailRow(detailsPanel, "票务价格:", String.format("￥%.2f", ticket.getPrice()));

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane);

        // Bigger confirmation button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("确认");
        closeButton.setFont(new Font("微软雅黑", Font.BOLD, 18));  // Larger font
        closeButton.setPreferredSize(new Dimension(120, 50));  // Bigger button
        closeButton.setBackground(new Color(76, 175, 80));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> successDialog.dispose());
        buttonPanel.add(closeButton);

        successDialog.add(mainPanel, BorderLayout.CENTER);
        successDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add some styling to the dialog
        successDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        successDialog.setVisible(true);
    }

    private String getFormattedShowtime(CinemaManagementSystem.Ticket ticket) {
        // Implement this if you have access to the showtime in your Ticket class
        // Otherwise return empty string or get from schedule
        return "";
    }

    private void addEnhancedDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("微软雅黑", Font.BOLD, 16));  // Larger font
        labelComponent.setPreferredSize(new Dimension(120, 25));  // More width

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("微软雅黑", Font.PLAIN, 16));  // Larger font

        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(10));
    }
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }
    // Helper method to create form rows
    private JPanel createFormRow(String labelText, String hintText) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font("微软雅黑", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(100, 30));

        JLabel hint = new JLabel(hintText);
        hint.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hint.setForeground(Color.GRAY);

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(label, BorderLayout.NORTH);
        labelPanel.add(hint, BorderLayout.SOUTH);
        labelPanel.setBackground(Color.WHITE);

        panel.add(labelPanel, BorderLayout.WEST);
        return panel;
    }
    // Helper method to create styled buttons
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(66, 133, 244)); // Google blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(53, 122, 232), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(66, 133, 244)); // Google blue
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Helper method to create styled text field
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    // Helper method to create styled combo box
    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        return comboBox;
    }

    // Helper method to create styled radio button
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        radio.setBackground(Color.WHITE);
        radio.setSelected(selected);
        radio.setFocusPainted(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return radio;
    }

    // Helper method to create styled spinner
    private JSpinner createStyledSpinner(int value, int min, int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Make the spinner editor look better
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setColumns(3);
            tf.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        }

        return spinner;
    }

    // Helper method to update schedules
    private void updateSchedules(JComboBox<String> movieComboBox, JComboBox<CinemaManagementSystem.Schedule> scheduleComboBox) {
        scheduleComboBox.removeAllItems();
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        if (selectedMovie != null) {
            for (CinemaManagementSystem.Schedule schedule : system.getSchedules()) {
                if (schedule.getMovieTitle().equals(selectedMovie)) {
                    scheduleComboBox.addItem(schedule);
                }
            }
        }
    }

    // Helper method to handle seat selection
    private void handleSeatSelection(JDialog parent, JComboBox<CinemaManagementSystem.Schedule> scheduleComboBox, JTextField seatField) {
        CinemaManagementSystem.Schedule selectedSchedule = (CinemaManagementSystem.Schedule) scheduleComboBox.getSelectedItem();
        if (selectedSchedule != null) {
            showSeatSelectionDialog(selectedSchedule, seatField);
        } else {
            JOptionPane.showMessageDialog(parent, "请先选择场次", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to update price display
    private void updatePriceDisplay(JComboBox<CinemaManagementSystem.Schedule> scheduleComboBox,
                                    JRadioButton childButton,
                                    JRadioButton adultButton,
                                    JSpinner ageSpinner,
                                    JLabel priceLabel,
                                    JDialog dialog) {
        CinemaManagementSystem.Schedule selectedSchedule = (CinemaManagementSystem.Schedule) scheduleComboBox.getSelectedItem();
        if (selectedSchedule != null) {
            double price = selectedSchedule.getPrice();
            if (childButton.isSelected()) {
                int age = (int) ageSpinner.getValue();
                if (age < 3) {
                    price = 0; // Free for infants
                } else if (age <= 12) {
                    price *= 0.7; // 30% discount for children
                } else {
                    JOptionPane.showMessageDialog(dialog, "13岁及以上请购买成人票", "年龄限制", JOptionPane.WARNING_MESSAGE);
                    adultButton.setSelected(true);
                    ageSpinner.setEnabled(false);
                    price = selectedSchedule.getPrice();
                }
            }
            priceLabel.setText(String.format("￥%.2f", price));
        }
    }

    // Helper method to handle ticket confirmation

    private void showRefundDialog() {
        // Create panel with larger padding and white background
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        // Use larger font (18pt instead of 14)
        Font largeFont = new Font("微软雅黑", Font.PLAIN, 18);
        Font largeBoldFont = new Font("微软雅黑", Font.BOLD, 18);

        // Create larger label
        JLabel label = new JLabel("请输入电子票号:");
        label.setFont(largeBoldFont);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add space below label

        // Create larger text field
        JTextField ticketField = new JTextField();
        ticketField.setFont(largeFont);
        ticketField.setPreferredSize(new Dimension(400, 35)); // Wider and taller field
        ticketField.setMargin(new Insets(5, 10, 5, 10)); // Internal padding

        // Add components to panel
        panel.add(label, BorderLayout.NORTH);
        panel.add(ticketField, BorderLayout.CENTER);

        // Set larger font for option pane buttons
        UIManager.put("OptionPane.buttonFont", largeFont);
        UIManager.put("OptionPane.messageFont", largeFont);

        // Show dialog with larger title font
        int result = JOptionPane.showConfirmDialog(
                system,
                panel,
                "退票功能",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Reset UI defaults
        UIManager.put("OptionPane.buttonFont", null);
        UIManager.put("OptionPane.messageFont", null);

        if (result == JOptionPane.OK_OPTION) {
            String ticketId = ticketField.getText().trim();
            if (!ticketId.isEmpty()) {
                // Get the Ticket object (not String)
                CinemaManagementSystem.Ticket ticket = system.getTicketById(ticketId);

                if (ticket != null) {
                    // Get the schedule for this ticket
                    CinemaManagementSystem.Schedule schedule = system.getScheduleById(ticket.getScheduleId());

                    if (schedule != null) {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime showTime = schedule.getShowTime();

                        // Calculate time until show
                        long hoursUntilShow = Duration.between(now, showTime).toHours();
                        double refundAmount = ticket.getPrice();
                        String message = "退票成功! ";

                        if (hoursUntilShow > 24) {
                            message += "全额退款 ￥" + String.format("%.2f", refundAmount);
                        } else if (hoursUntilShow > 1) {
                            refundAmount *= 0.9;
                            message += "扣除10%手续费，退还 ￥" + String.format("%.2f", refundAmount);
                        } else if (hoursUntilShow > 0) {
                            refundAmount *= 0.7;
                            message += "扣除30%手续费，退还 ￥" + String.format("%.2f", refundAmount);
                        } else {
                            showLargeMessageDialog("电影已开始或已结束，无法退票", "退票失败", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        boolean success = system.refundTicket(ticketId, refundAmount);
                        if (success) {
                            showLargeMessageDialog(message, "退票成功", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            showLargeMessageDialog("退票处理失败，请稍后重试", "退票失败", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        showLargeMessageDialog("未找到对应的场次信息", "退票失败", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    showLargeMessageDialog("未找到该电子票号", "退票失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Helper method to show large message dialogs
    private void showLargeMessageDialog(String message, String title, int messageType) {
        Font largeFont = new Font("微软雅黑", Font.PLAIN, 16);
        UIManager.put("OptionPane.messageFont", largeFont);
        UIManager.put("OptionPane.buttonFont", largeFont);

        JOptionPane.showMessageDialog(
                system,
                message,
                title,
                messageType
        );

        UIManager.put("OptionPane.messageFont", null);
        UIManager.put("OptionPane.buttonFont", null);
    }    private void showMoviesInfo() {
        System.out.println("Movies count: " + system.getMovies().size());

        // Create table model
        String[] columns = {"片名", "导演", "主演", "时长(分钟)", "评分"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create filtered list of screening movies
        List<Movie> screeningMovies = new ArrayList<>();
        for (Movie movie : system.getMovies()) {
            if (movie.isScreening()) {
                screeningMovies.add(movie);
                model.addRow(new Object[]{
                        movie.getTitle() != null ? movie.getTitle() : "",
                        movie.getDirector() != null ? movie.getDirector() : "",
                        movie.getActors() != null ? movie.getActors() : "",
                        movie.getDuration(),
                        movie.getRating()
                });
            }
        }

        // Create table
        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click listener using filtered list
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < screeningMovies.size()) {
                        Movie movie = screeningMovies.get(row);
                        showMovieDetails(movie);
                    }
                }
            }
        });

        // Create proper scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        // Show dialog
        JOptionPane.showMessageDialog(
                this, // Use 'this' instead of 'system' for proper parent
                scrollPane,
                "正在上映影片 (" + screeningMovies.size() + "部)",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void showMovieDetails(Movie movie) {
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(movie.getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        detailPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        String plotSummary = movie.getPlotSummary() != null ? movie.getPlotSummary() : "暂无剧情简介";
        infoArea.setText(String.format(
                "导演: %s\n主演: %s\n时长: %d分钟\n评分: %.1f\n\n剧情简介:\n%s",
                movie.getDirector() != null ? movie.getDirector() : "未知",
                movie.getActors() != null ? movie.getActors() : "未知",
                movie.getDuration(),
                movie.getRating(),
                plotSummary
        ));

        detailPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(
                this,
                detailPanel,
                "影片详情: " + movie.getTitle(),
                JOptionPane.PLAIN_MESSAGE
        );
    }
    private void showShowtimesDialog() {
        JDialog dialog = new JDialog(system, "场次查询", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(system);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        filterPanel.add(new JLabel("选择时间范围:"));
        JComboBox<String> timeRangeCombo = new JComboBox<>(
                new String[]{"当日场次", "近3日场次", "近一周场次"});
        timeRangeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        filterPanel.add(timeRangeCombo);

        JButton refreshButton = new JButton("查询");
        styleButton(refreshButton, SECONDARY_COLOR);
        filterPanel.add(refreshButton);

        dialog.add(filterPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"场次ID", "影片名称", "放映厅", "放映时间", "价格"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Load data function
        Runnable loadData = () -> {
            model.setRowCount(0);
            String range = (String) timeRangeCombo.getSelectedItem();
            LocalDateTime now = LocalDateTime.now();

            for (CinemaManagementSystem.Schedule schedule : system.getSchedules()) {
                LocalDateTime showTime = schedule.getShowTime();
                boolean include = false;

                if (range.equals("当日场次")) {
                    include = showTime.toLocalDate().equals(now.toLocalDate());
                } else if (range.equals("近3日场次")) {
                    include = !showTime.isBefore(now) && showTime.isBefore(now.plusDays(3));
                } else if (range.equals("近一周场次")) {
                    include = !showTime.isBefore(now) && showTime.isBefore(now.plusWeeks(1));
                }

                if (include) {
                    model.addRow(new Object[]{
                            schedule.getId(),
                            schedule.getMovieTitle(),
                            schedule.getHall(),
                            showTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            String.format("￥%.2f", schedule.getPrice())
                    });
                }
            }
        };

        // Initial load
        loadData.run();

        // Refresh button action
        refreshButton.addActionListener(e -> loadData.run());

        dialog.setVisible(true);
    }
    private void showSeatSelectionDialog(CinemaManagementSystem.Schedule schedule, JTextField seatField) {
        currentSelectedSeats.clear();

        JDialog dialog = new JDialog(system, "座位选择 - " + schedule.getMovieTitle() + " (" + schedule.getHall() + ")", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 700);  // Slightly larger for better visibility
        dialog.setLocationRelativeTo(system);

        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header with movie and schedule info
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel movieLabel = new JLabel("影片: " + schedule.getMovieTitle());
        movieLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JLabel timeLabel = new JLabel("时间: " + schedule.getShowTime().format(formatter) +
                " | 放映厅: " + schedule.getHall() + getHallCapacityText(schedule.getHall()));
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        headerPanel.add(movieLabel);
        headerPanel.add(timeLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Seat map panel with scroll
        JPanel seatMapPanel = new JPanel();
        generateEnhancedSeatMap(seatMapPanel, schedule);

        JScrollPane scrollPane = new JScrollPane(seatMapPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("请选择座位"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel at bottom
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Selected seats display
        JLabel selectedSeatsLabel = new JLabel("已选座位: 无");
        selectedSeatsLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // Buttons
        JButton confirmButton = createStyledButton("确认选择", new Color(76, 175, 80));
        JButton cancelButton = createStyledButton("取消", new Color(220, 80, 60));

        confirmButton.addActionListener(e -> {
            if (!currentSelectedSeats.isEmpty()) {
                seatField.setText(String.join(", ", currentSelectedSeats));
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "请选择至少一个座位", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        controlPanel.add(selectedSeatsLabel);
        controlPanel.add(confirmButton);
        controlPanel.add(cancelButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);


        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private String getHallCapacityText(String hall) {
        switch(hall) {
            case "黄金厅": return " (8排×10座)";
            case "白银厅": return " (10排×12座)";
            case "翡翠厅":
            case "钻石厅": return " (7排×8座)";
            case "宝石厅": return " (8排×9座)";
            default: return "";
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void generateEnhancedSeatMap(JPanel seatMapPanel, CinemaManagementSystem.Schedule schedule) {
        seatMapPanel.removeAll();

        // Determine hall layout
        int rows, cols;
        switch(schedule.getHall()) {
            case "黄金厅": rows = 8; cols = 10; break;
            case "白银厅": rows = 10; cols = 12; break;
            case "翡翠厅":
            case "钻石厅": rows = 7; cols = 8; break;
            case "宝石厅": rows = 8; cols = 9; break;
            default: rows = 5; cols = 5; // fallback
        }

        // Use GridBagLayout for more precise control
        seatMapPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Add screen representation
        JLabel screenLabel = new JLabel("───── 银 幕 ─────", JLabel.CENTER);
        screenLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(70, 130, 180));
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = cols + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        seatMapPanel.add(screenLabel, gbc);

        // Add column headers
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // Empty corner
        gbc.gridx = 0;
        seatMapPanel.add(new JLabel(""), gbc);

        // Column numbers
        for (int col = 1; col <= cols; col++) {
            gbc.gridx = col;
            JLabel colLabel = new JLabel(String.valueOf(col));
            colLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            seatMapPanel.add(colLabel, gbc);
        }

        // Add seat buttons with row labels
        for (int row = 1; row <= rows; row++) {
            gbc.gridy = row + 1;

            // Row label
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel("排 " + row);
            rowLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            seatMapPanel.add(rowLabel, gbc);

            // Seat buttons
            for (int col = 1; col <= cols; col++) {
                gbc.gridx = col;
                String seatNumber = row + "排" + col + "座";
                JButton seatButton = createSeatButton(schedule, row, col, seatNumber);
                seatMapPanel.add(seatButton, gbc);
            }
        }

        // Add legend at the bottom
        gbc.gridy = rows + 2;
        gbc.gridx = 0;
        gbc.gridwidth = cols + 1;

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.add(createLegendLabel("可选", Color.GREEN, Color.BLACK));
        legendPanel.add(createLegendLabel("已选", Color.YELLOW, Color.BLACK));
        legendPanel.add(createLegendLabel("已售", Color.RED, Color.WHITE));
        legendPanel.add(createLegendLabel("VIP", Color.ORANGE, Color.BLACK));

        seatMapPanel.add(legendPanel, gbc);

        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private JButton createSeatButton(CinemaManagementSystem.Schedule schedule, int row, int col, String seatNumber) {
        JButton seatButton = new JButton(String.valueOf(col));
        seatButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        seatButton.setPreferredSize(new Dimension(40, 40));
        seatButton.setFocusPainted(false);

        // Check if seat is sold
        if (soldSeatsMap.containsKey(schedule.getId()) &&
                soldSeatsMap.get(schedule.getId()).contains(seatNumber)) {
            seatButton.setBackground(Color.RED);
            seatButton.setForeground(Color.WHITE);
            seatButton.setEnabled(false);
            seatButton.setToolTipText("已售出");
        }
        // Check if seat is VIP (first row in premium halls)
        else if ((schedule.getHall().equals("钻石厅") || schedule.getHall().equals("宝石厅")) && row == 1) {
            seatButton.setBackground(Color.ORANGE);
            seatButton.setForeground(Color.BLACK);
            seatButton.setToolTipText("VIP座位");
            seatButton.addActionListener(e -> toggleSeatSelection(seatButton, seatNumber));
        }
        // Regular available seat
        else {
            seatButton.setBackground(currentSelectedSeats.contains(seatNumber) ?
                    Color.YELLOW : Color.GREEN);
            seatButton.setForeground(Color.BLACK);
            seatButton.addActionListener(e -> toggleSeatSelection(seatButton, seatNumber));
        }

        return seatButton;
    }

    private JLabel createLegendLabel(String text, Color bgColor, Color fgColor) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(fgColor);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        label.setPreferredSize(new Dimension(70, 25));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    // Improved seat query dialog
    private void showSeatQueryDialog() {
        // Create input panel with better layout and padding
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Increased padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set larger font
        Font largerFont = new Font("Dialog", Font.PLAIN, 16);

        // Improved input fields with better naming and larger size
        JTextField movieField = createInputField("输入电影名称");
        movieField.setFont(largerFont);
        movieField.setPreferredSize(new Dimension(300, 30));  // Wider and taller field

        JTextField scheduleField = createInputField("输入场次ID");
        scheduleField.setFont(largerFont);
        scheduleField.setPreferredSize(new Dimension(300, 30));  // Wider and taller field

        // Create larger labels
        JLabel movieLabel = new JLabel("电影名称:");
        movieLabel.setFont(largerFont);
        JLabel scheduleLabel = new JLabel("场次ID:");
        scheduleLabel.setFont(largerFont);

        // Add components with proper layout constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(movieLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(movieField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(scheduleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(scheduleField, gbc);

        // Create larger buttons for the dialog
        UIManager.put("OptionPane.buttonFont", largerFont);

        // Show dialog with better title and options
        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "查询座位信息",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Reset the button font to avoid affecting other dialogs
        UIManager.put("OptionPane.buttonFont", null);

        if (result == JOptionPane.OK_OPTION) {
            String movieName = movieField.getText().trim();
            String scheduleId = scheduleField.getText().trim();

            // Validate inputs
            if (movieName.isEmpty() || scheduleId.isEmpty()) {
                showErrorDialog("请输入电影名称和场次ID");
                return;
            }

            try {
                CinemaManagementSystem.Schedule schedule = system.getScheduleById(scheduleId);

                if (schedule == null || !schedule.getMovieTitle().equals(movieName)) {
                    showErrorDialog("未找到匹配的场次");
                    return;
                }

                // Create and display seat map
                displaySeatMap(schedule);

            } catch (Exception e) {
                showErrorDialog("查询过程中发生错误: " + e.getMessage());
            }
        }
    }
    // Helper method to create consistent input fields
    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    // Helper method to display seat map
    private void displaySeatMap(CinemaManagementSystem.Schedule schedule) {
        JPanel seatMapPanel = new JPanel();
        generateSeatMap(seatMapPanel, schedule);

        // Configure scroll pane
        JScrollPane scrollPane = new JScrollPane(seatMapPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Custom dialog instead of JOptionPane for better control
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "座位图 - " + schedule.getMovieTitle());
        dialog.setContentPane(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    // Helper method for error messages
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
    }    // Unified seat map generation method

    private void toggleSeatSelection(JButton seatButton, String seatNumber) {
        if (currentSelectedSeats.contains(seatNumber)) {
            currentSelectedSeats.remove(seatNumber);
            seatButton.setBackground(Color.GREEN);
        } else {
            currentSelectedSeats.add(seatNumber);
            seatButton.setBackground(ACCENT_COLOR);
        }
    }

    private void generateSeatMap(JPanel seatMapPanel, CinemaManagementSystem.Schedule schedule) {
        seatMapPanel.removeAll();

        // Determine hall layout
        int rows = 8;
        int cols = 10;
        String hall = schedule.getHall();
        if (hall.contains("黄金")) {
            rows = 8;
            cols = 10;
        } else if (hall.contains("白银")) {
            rows = 10;
            cols = 12;
        } else if (hall.contains("翡翠") || hall.contains("钻石")) {
            rows = 7;
            cols = 8;
        } else if (hall.contains("宝石")) {
            rows = 8;
            cols = 9;
        }

        // Screen label
        JLabel screenLabel = new JLabel("—— 银幕 ——", JLabel.CENTER);
        screenLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(180, 180, 180));
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        screenLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        screenLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        seatMapPanel.add(screenLabel);
        seatMapPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Create seat map
        Set<String> soldSeats = soldSeatsMap.getOrDefault(schedule.getId(), new HashSet<>());

        for (int row = 1; row <= rows; row++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            rowPanel.setBackground(Color.WHITE);

            // Row label
            JLabel rowLabel = new JLabel("ROW " + row);
            rowLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            rowLabel.setPreferredSize(new Dimension(60, 40));
            rowPanel.add(rowLabel);

            for (int col = 1; col <= cols; col++) {
                String seat = row + "-" + col;
                JButton seatButton = new JButton(seat);
                seatButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
                seatButton.setPreferredSize(new Dimension(45, 45));
                seatButton.setFocusPainted(false);

                // Set seat status
                if (soldSeats.contains(seat)) {
                    seatButton.setBackground(Color.RED);
                    seatButton.setEnabled(false);
                } else {
                    seatButton.setBackground(Color.GREEN);
                    seatButton.addActionListener(e -> {
                        if (seatButton.getBackground() == ACCENT_COLOR) {
                            seatButton.setBackground(Color.GREEN);
                        } else {
                            seatButton.setBackground(ACCENT_COLOR);
                        }
                    });
                }

                rowPanel.add(seatButton);
            }
            seatMapPanel.add(rowPanel);
        }

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setBackground(Color.lightGray);

        legendPanel.add(createLegendItem("可选座位", Color.GREEN));
        legendPanel.add(createLegendItem("已选座位", ACCENT_COLOR));
        legendPanel.add(createLegendItem("已售座位", Color.RED));

        seatMapPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        seatMapPanel.add(legendPanel);

        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.lightGray);

        JLabel colorLabel = new JLabel();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setPreferredSize(new Dimension(20, 20));
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        panel.add(colorLabel);
        panel.add(textLabel);
        return panel;
    }
}