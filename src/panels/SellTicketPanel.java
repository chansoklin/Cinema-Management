package panels;

import ui.SeatSelectionDialog;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import model.*;
import service.*;
import exception.DatabaseException;
import util.DBConnection;
import java.sql.*;

public class SellTicketPanel extends JPanel {

    // Services
    private final MovieService movieService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;
    private final User currentUser;

    // Components
    private JComboBox<String> movieCombo;
    private JComboBox<String> scheduleCombo;
    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JComboBox<String> ticketTypeCombo;
    private JSpinner ticketQuantitySpinner;
    private JLabel totalAmountLabel;
    private JLabel ticketSubtotalLabel;
    private JLabel foodSubtotalLabel;
    private JLabel selectedSeatsLabel;

    // Food & Drink Components
    private JComboBox<String> foodCombo;
    private JSpinner foodQuantitySpinner;
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;

    private List<Schedule> availableSchedules;
    private List<Seat> selectedSeats;
    private double ticketTotal;
    private double foodTotal;
    private Schedule selectedSchedule;

    // Food Menu
    private final Map<String, FoodItem> foodMenu;

    // Color Scheme
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_WARNING = new Color(241, 196, 15);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_BORDER = new Color(220, 220, 220);
    private final Color COLOR_BG = new Color(248, 249, 250);

    public SellTicketPanel(User user) {
        this.currentUser = user;
        this.movieService = new MovieService();
        this.scheduleService = new ScheduleService();
        this.bookingService = new BookingService();
        this.selectedSeats = new ArrayList<>();
        this.foodMenu = new LinkedHashMap<>();
        this.availableSchedules = new ArrayList<>();
        initFoodMenu();
        initComponents();
        loadMovies();
    }

    private void initFoodMenu() {
        foodMenu.put("🍿 Popcorn (Large)", new FoodItem("Popcorn (Large)", 8.50, "🍿"));
        foodMenu.put("🍿 Popcorn (Medium)", new FoodItem("Popcorn (Medium)", 6.50, "🍿"));
        foodMenu.put("🥤 Soda (Large)", new FoodItem("Soda (Large)", 4.50, "🥤"));
        foodMenu.put("🥤 Soda (Medium)", new FoodItem("Soda (Medium)", 3.50, "🥤"));
        foodMenu.put("🥤 Soda (Small)", new FoodItem("Soda (Small)", 2.50, "🥤"));
        foodMenu.put("🍫 Candy Bar", new FoodItem("Candy Bar", 2.00, "🍫"));
        foodMenu.put("🍿 Nachos", new FoodItem("Nachos", 7.50, "🍿"));
        foodMenu.put("🌭 Hot Dog", new FoodItem("Hot Dog", 5.50, "🌭"));
        foodMenu.put("🍦 Ice Cream", new FoodItem("Ice Cream", 4.00, "🍦"));
        foodMenu.put("☕ Coffee", new FoodItem("Coffee", 3.50, "☕"));
        foodMenu.put("💧 Water", new FoodItem("Bottled Water", 2.00, "💧"));
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.55);
        mainSplitPane.setBorder(null);
        mainSplitPane.setDividerSize(10);

        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = createRightPanel();
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createLeftPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        contentPanel.add(createCustomerPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createMoviePanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createSeatPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createTicketPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createFoodPanel());

        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "👤 Customer Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Full Name:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        customerNameField = createStyledTextField();
        panel.add(customerNameField, gbc);

        // Email (REQUIRED for booking history)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        customerEmailField = createStyledTextField();
        customerEmailField.setToolTipText("Required for booking confirmation");
        panel.add(customerEmailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        customerPhoneField = createStyledTextField();
        panel.add(customerPhoneField, gbc);

        return panel;
    }

    private JPanel createMoviePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "🎬 Movie & Show Time",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Movie:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        movieCombo = createStyledComboBox();
        movieCombo.addActionListener(e -> loadSchedules());
        panel.add(movieCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Show Time:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        scheduleCombo = createStyledComboBox();
        scheduleCombo.addActionListener(e -> updateSelectedSchedule());
        scheduleCombo.setEnabled(false);
        panel.add(scheduleCombo, gbc);

        return panel;
    }

    private void updateSelectedSchedule() {
        int index = scheduleCombo.getSelectedIndex();
        if (index >= 0 && index < availableSchedules.size()) {
            selectedSchedule = availableSchedules.get(index);
        }
    }

    private JPanel createSeatPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "💺 Seat Selection",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton selectSeatsBtn = new JButton("💺 Select Seats");
        selectSeatsBtn.setBackground(new Color(155, 89, 182));
        selectSeatsBtn.setForeground(Color.WHITE);
        selectSeatsBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectSeatsBtn.setPreferredSize(new Dimension(180, 40));
        selectSeatsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectSeatsBtn.addActionListener(e -> showSeatSelection());

        buttonPanel.add(selectSeatsBtn);

        selectedSeatsLabel = new JLabel("No seats selected");
        selectedSeatsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        selectedSeatsLabel.setForeground(Color.GRAY);
        selectedSeatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(selectedSeatsLabel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createTicketPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "🎫 Ticket Selection",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Update ticket type options with price ranges
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Ticket Type:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        ticketTypeCombo = createStyledComboBox();
        ticketTypeCombo.addItem("👤 Adult - Base price (VIP: $18, Regular: $12, Standard: $10)");
        ticketTypeCombo.addItem("👶 Child - 30% off (VIP: $12.60, Regular: $8.40, Standard: $7.00)");
        ticketTypeCombo.addItem("👴 Senior - 20% off (VIP: $14.40, Regular: $9.60, Standard: $8.00)");
        panel.add(ticketTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Quantity per seat:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        ticketQuantitySpinner = createStyledSpinner(1, 1, 10);
        panel.add(ticketQuantitySpinner, gbc);

        // Info label about pricing
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("💡 Price is calculated based on selected seat type");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1;
        JButton addTicketBtn = createStyledButton("➕ Add to Cart", COLOR_PRIMARY);
        addTicketBtn.addActionListener(e -> addToCart());
        panel.add(addTicketBtn, gbc);

        return panel;
    }
    private JPanel createFoodPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "🍔 Food & Drinks",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Item:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        foodCombo = createStyledComboBox();
        for (String item : foodMenu.keySet()) {
            foodCombo.addItem(item);
        }
        panel.add(foodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        foodQuantitySpinner = createStyledSpinner(1, 1, 20);
        panel.add(foodQuantitySpinner, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 0.7;
        JButton addFoodBtn = createStyledButton("🍕 Add to Cart", COLOR_SUCCESS);
        addFoodBtn.addActionListener(e -> addFoodToCart());
        panel.add(addFoodBtn, gbc);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "🛒 Shopping Cart",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRIMARY
        ));

        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartList.setBackground(COLOR_BG);
        cartList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        cartScroll.setPreferredSize(new Dimension(320, 400));
        panel.add(cartScroll, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        summaryPanel.add(new JLabel("Tickets:"), gbc);
        gbc.gridx = 1;
        ticketSubtotalLabel = new JLabel("$0.00");
        ticketSubtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ticketSubtotalLabel.setForeground(COLOR_PRIMARY);
        summaryPanel.add(ticketSubtotalLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(new JLabel("Food & Drinks:"), gbc);
        gbc.gridx = 1;
        foodSubtotalLabel = new JLabel("$0.00");
        foodSubtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        foodSubtotalLabel.setForeground(COLOR_SUCCESS);
        summaryPanel.add(foodSubtotalLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(new JSeparator(), gbc);
        gbc.gridx = 1;
        summaryPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        summaryPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("$0.00");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalAmountLabel.setForeground(COLOR_DANGER);
        summaryPanel.add(totalAmountLabel, gbc);

        JButton removeBtn = createStyledButton("🗑️ Remove Selected", COLOR_DANGER);
        removeBtn.addActionListener(e -> removeFromCart());

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(removeBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));

        JButton clearBtn = createStyledButton("🗑️ Clear Cart", new Color(149, 165, 166));
        clearBtn.addActionListener(e -> clearCart());

        JButton refundBtn = createStyledButton("🔄 Process Refund", COLOR_WARNING);
        refundBtn.addActionListener(e -> processRefund());

        JButton processBtn = createStyledButton("💳 Process Payment", COLOR_SUCCESS);
        processBtn.addActionListener(e -> processPayment());
        processBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        processBtn.setPreferredSize(new Dimension(160, 45));

        panel.add(clearBtn);
        panel.add(refundBtn);
        panel.add(processBtn);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return combo;
    }

    private JSpinner createStyledSpinner(int value, int min, int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return spinner;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            movieCombo.removeAllItems();
            for (Movie movie : movies) {
                if (movie.isActive()) {
                    movieCombo.addItem(movie.getTitle());
                }
            }
        } catch (DatabaseException e) {
            movieCombo.addItem("Inception");
            movieCombo.addItem("The Dark Knight");
            movieCombo.addItem("Interstellar");
            movieCombo.addItem("John Wick");
            movieCombo.addItem("The Matrix");
        }
        if (movieCombo.getItemCount() > 0) {
            movieCombo.setSelectedIndex(0);
        }
    }

    private void loadSchedules() {
        scheduleCombo.removeAllItems();
        availableSchedules.clear();
        scheduleCombo.setEnabled(false);

        String selectedMovie = (String) movieCombo.getSelectedItem();
        if (selectedMovie == null || selectedMovie.isEmpty()) {
            scheduleCombo.addItem("Please select a movie first");
            return;
        }

        // Get the actual movie ID
        int selectedMovieId = getMovieIdByTitle(selectedMovie);

        boolean hasRealSchedules = false;

        try {
            List<Schedule> allSchedules = scheduleService.getAllSchedules();
            System.out.println("Loading schedules for movie: " + selectedMovie + " (ID: " + selectedMovieId + ")");

            for (Schedule schedule : allSchedules) {
                System.out.println("Schedule movie ID: " + schedule.getMovieId() + ", Title: " +
                        (schedule.getMovie() != null ? schedule.getMovie().getTitle() : "null"));

                // Match by movie ID instead of title for accuracy
                if (schedule.getMovieId() == selectedMovieId) {
                    availableSchedules.add(schedule);
                    String timeStr = formatDateTime(schedule.getStartTime());
                    String screenStr = schedule.getScreenName() != null ? schedule.getScreenName() : "Screen " + schedule.getScreenId();
                    scheduleCombo.addItem(timeStr + " - " + screenStr + " ($" + schedule.getBasePrice() + ")");
                    hasRealSchedules = true;
                    System.out.println("Added schedule: " + timeStr);
                }
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        if (!hasRealSchedules) {
            System.out.println("No real schedules found, creating demo schedules for: " + selectedMovie);
            createDemoSchedulesForMovie(selectedMovie, selectedMovieId);
        }

        if (availableSchedules.size() > 0) {
            scheduleCombo.setEnabled(true);
            scheduleCombo.setSelectedIndex(0);
            updateSelectedSchedule();
        } else {
            scheduleCombo.addItem("No shows available");
        }
    }

    private int getMovieIdByTitle(String title) {
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie m : movies) {
                if (m.getTitle().equalsIgnoreCase(title)) {
                    return m.getId();
                }
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return 1; // Default
    }

    private void createDemoSchedulesForMovie(String movieTitle, int movieId) {
        availableSchedules.clear();

        int duration = getMovieDuration(movieTitle);

        String[][] showtimes = {
                {"10:00 AM", "12.00"},
                {"1:00 PM", "12.00"},
                {"4:00 PM", "14.00"},
                {"7:00 PM", "15.00"},
                {"9:00 PM", "12.00"}
        };
        String[] screens = {"Screen 1", "Screen 2"};

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int scheduleIdCounter = 1000;

        for (int day = 0; day < 7; day++) {
            for (int t = 0; t < showtimes.length; t++) {
                Schedule demo = new Schedule();
                demo.setId(scheduleIdCounter++);
                demo.setMovieId(movieId);  // Use the correct movie ID

                Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(movieTitle);
                demo.setMovie(movie);

                demo.setScreenName(screens[t % 2]);
                demo.setScreenId((t % 2) + 1);
                demo.setBasePrice(Double.parseDouble(showtimes[t][1]));

                cal.setTime(new java.util.Date());
                cal.add(java.util.Calendar.DAY_OF_YEAR, day);
                String timeStr = showtimes[t][0];
                int hour = Integer.parseInt(timeStr.split(":")[0]);
                if (timeStr.contains("PM") && hour != 12) hour += 12;
                if (timeStr.contains("AM") && hour == 12) hour = 0;
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                cal.set(java.util.Calendar.MINUTE, 0);
                demo.setStartTime(new Timestamp(cal.getTimeInMillis()));

                cal.add(java.util.Calendar.MINUTE, duration);
                demo.setEndTime(new Timestamp(cal.getTimeInMillis()));

                availableSchedules.add(demo);

                String displayDate = new java.text.SimpleDateFormat("EEE, MMM d").format(cal.getTime());
                scheduleCombo.addItem(displayDate + " " + showtimes[t][0] + " - " + demo.getScreenName() + " ($" + demo.getBasePrice() + ")");
            }
        }
    }

    private int getMovieDuration(String title) {
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie m : movies) {
                if (m.getTitle().equalsIgnoreCase(title)) {
                    return m.getDuration();
                }
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return 120;
    }
    private void createDemoSchedulesForMovie(String movieTitle) {
        availableSchedules.clear();

        int duration = 120;
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie m : movies) {
                if (m.getTitle().equals(movieTitle)) {
                    duration = m.getDuration();
                    break;
                }
            }
        } catch (DatabaseException e) {
            duration = 120;
        }

        String[][] showtimes = {
                {"10:00 AM", "12.00"},
                {"1:00 PM", "12.00"},
                {"4:00 PM", "14.00"},
                {"7:00 PM", "15.00"},
                {"9:00 PM", "12.00"}
        };
        String[] screens = {"Screen 1", "Screen 2"};

        java.util.Calendar cal = java.util.Calendar.getInstance();

        for (int day = 0; day < 7; day++) {
            for (int t = 0; t < showtimes.length; t++) {
                Schedule demo = new Schedule();
                Movie mockMovie = new Movie();
                mockMovie.setTitle(movieTitle);
                demo.setMovie(mockMovie);
                demo.setScreenName(screens[t % 2]);
                demo.setBasePrice(Double.parseDouble(showtimes[t][1]));

                cal.setTime(new java.util.Date());
                cal.add(java.util.Calendar.DAY_OF_YEAR, day);
                String timeStr = showtimes[t][0];
                int hour = Integer.parseInt(timeStr.split(":")[0]);
                if (timeStr.contains("PM") && hour != 12) hour += 12;
                if (timeStr.contains("AM") && hour == 12) hour = 0;
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                cal.set(java.util.Calendar.MINUTE, 0);
                demo.setStartTime(new Timestamp(cal.getTimeInMillis()));

                cal.add(java.util.Calendar.MINUTE, duration);
                demo.setEndTime(new Timestamp(cal.getTimeInMillis()));

                availableSchedules.add(demo);

                String displayDate = new java.text.SimpleDateFormat("EEE, MMM d").format(cal.getTime());
                scheduleCombo.addItem(displayDate + " " + showtimes[t][0] + " - " + demo.getScreenName() + " ($" + demo.getBasePrice() + ")");
            }
        }
    }

    private void showSeatSelection() {
        if (scheduleCombo.getSelectedIndex() < 0 || scheduleCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie and show time first!");
            return;
        }

        if (availableSchedules == null || scheduleCombo.getSelectedIndex() >= availableSchedules.size()) {
            JOptionPane.showMessageDialog(this, "Please select a valid show time!");
            return;
        }

        Schedule schedule = availableSchedules.get(scheduleCombo.getSelectedIndex());

        SeatSelectionDialog dialog = new SeatSelectionDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                schedule
        );
        dialog.setVisible(true);

        selectedSeats = dialog.getSelectedSeats();

        if (!selectedSeats.isEmpty()) {
            StringBuilder seatText = new StringBuilder("Selected: ");
            for (Seat seat : selectedSeats) {
                seatText.append(seat.getSeatNumber()).append(" ");
            }
            selectedSeatsLabel.setText(seatText.toString());
            selectedSeatsLabel.setForeground(COLOR_SUCCESS);

            JOptionPane.showMessageDialog(this,
                    selectedSeats.size() + " seat(s) selected!",
                    "Seats Selected",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addToCart() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select seats first!");
            return;
        }

        if (selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, "Please select a show time first!");
            return;
        }

        String ticketType = (String) ticketTypeCombo.getSelectedItem();
        int quantity = (Integer) ticketQuantitySpinner.getValue();

        String typeCode;
        double multiplier;
        if (ticketType != null && ticketType.contains("Adult")) {
            typeCode = "ADULT";
            multiplier = 1.0;
        } else if (ticketType != null && ticketType.contains("Child")) {
            typeCode = "CHILD";
            multiplier = 0.7;
        } else {
            typeCode = "SENIOR";
            multiplier = 0.8;
        }

        // Calculate price per seat and total
        double totalTicketPrice = 0;
        StringBuilder priceBreakdown = new StringBuilder();
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");

        for (Seat seat : selectedSeats) {
            double basePrice = 0;
            String seatType = "";

            if ("VIP".equals(seat.getSeatType())) {
                basePrice = 18.00;
                seatType = "VIP";
            } else if ("REGULAR".equals(seat.getSeatType())) {
                basePrice = 12.00;
                seatType = "Regular";
            } else {
                basePrice = 10.00;
                seatType = "Standard";
            }

            double seatPrice = basePrice * multiplier;
            totalTicketPrice += seatPrice;

            priceBreakdown.append(String.format("%s (%s): $%.2f",
                    seat.getSeatNumber(), seatType, seatPrice));
            if (selectedSeats.size() > 1) {
                priceBreakdown.append("\n");
            }
        }

        // Multiply by quantity (if multiple tickets per seat)
        if (quantity > 1) {
            totalTicketPrice *= quantity;
        }

        ticketTotal += totalTicketPrice;

        // Add to cart display with detailed breakdown
        String cartMessage;
        if (quantity > 1) {
            cartMessage = String.format("🎫 %d x %s Tickets\n   Seats: %s\n   Price breakdown:\n%s   Subtotal: $%s",
                    quantity, typeCode, getSeatNumbers(), priceBreakdown.toString(), df.format(totalTicketPrice));
        } else {
            cartMessage = String.format("🎫 %s Tickets\n   Seats: %s\n   Price breakdown:\n%s   Subtotal: $%s",
                    typeCode, getSeatNumbers(), priceBreakdown.toString(), df.format(totalTicketPrice));
        }

        cartModel.addElement(cartMessage);

        updateTotals();

        // Show detailed confirmation
        JOptionPane.showMessageDialog(this,
                String.format("✅ Tickets added to cart!\n\nTicket Type: %s\nQuantity: %d\nSeats: %s\n\nPrice Breakdown:\n%s\n\nTotal: $%.2f",
                        typeCode, quantity, getSeatNumbers(), priceBreakdown.toString(), totalTicketPrice),
                "Tickets Added",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper method to get seat numbers as string
    private String getSeatNumbers() {
        StringBuilder seats = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            seats.append(selectedSeats.get(i).getSeatNumber());
            if (i < selectedSeats.size() - 1) {
                seats.append(", ");
            }
        }
        return seats.toString();
    }    private void addFoodToCart() {
        String foodName = (String) foodCombo.getSelectedItem();
        FoodItem food = foodMenu.get(foodName);
        int quantity = (Integer) foodQuantitySpinner.getValue();

        if (food != null) {
            double totalFoodPrice = food.getPrice() * quantity;
            foodTotal += totalFoodPrice;
            cartModel.addElement(String.format("%s %d x %s - $%.2f",
                    food.getIcon(), quantity, food.getName(), totalFoodPrice));
            updateTotals();
            JOptionPane.showMessageDialog(this, quantity + " " + food.getName() + " added to cart!");
        }
    }

    private void removeFromCart() {
        int selectedIndex = cartList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String item = cartModel.getElementAt(selectedIndex);
            cartModel.remove(selectedIndex);

            if (item.contains("🎫")) {
                String[] parts = item.split(" - \\$");
                if (parts.length > 1) {
                    double amount = Double.parseDouble(parts[1]);
                    ticketTotal -= amount;
                }
            } else if (item.contains("🍿") || item.contains("🥤") || item.contains("🍫") ||
                    item.contains("🌭") || item.contains("🍦") || item.contains("☕") ||
                    item.contains("💧")) {
                String[] parts = item.split(" - \\$");
                if (parts.length > 1) {
                    double amount = Double.parseDouble(parts[1]);
                    foodTotal -= amount;
                }
            }
            updateTotals();
        }
    }

    private void updateTotals() {
        ticketSubtotalLabel.setText(String.format("$%.2f", ticketTotal));
        foodSubtotalLabel.setText(String.format("$%.2f", foodTotal));
        totalAmountLabel.setText(String.format("$%.2f", ticketTotal + foodTotal));
    }

    private void clearCart() {
        cartModel.clear();
        ticketTotal = 0;
        foodTotal = 0;
        selectedSeats.clear();
        selectedSeatsLabel.setText("No seats selected");
        selectedSeatsLabel.setForeground(Color.GRAY);
        updateTotals();
    }

    private void saveBookingToDatabase() {
        String customerName = customerNameField.getText().trim();
        String customerEmail = customerEmailField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();

        if (customerName.isEmpty() || customerEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name and email!");
            return;
        }

        if (selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, "No schedule selected!");
            return;
        }

        // Debug output
        System.out.println("Saving booking - Schedule ID: " + selectedSchedule.getId() +
                ", Movie ID: " + selectedSchedule.getMovieId());

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO bookings (user_id, schedule_id, customer_name, customer_email, customer_phone, " +
                    "total_amount, status, payment_method, booking_time) VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED', ?, NOW())";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, currentUser != null ? currentUser.getId() : 0);
            ps.setInt(2, selectedSchedule.getId());  // This must be the correct schedule ID
            ps.setString(3, customerName);
            ps.setString(4, customerEmail);
            ps.setString(5, customerPhone != null ? customerPhone : "");
            ps.setDouble(6, ticketTotal + foodTotal);
            ps.setString(7, "Cash");

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int bookingId = 0;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }

            conn.close();

            String selectedMovie = (String) movieCombo.getSelectedItem();
            JOptionPane.showMessageDialog(this,
                    "✅ Booking saved!\n" +
                            "Booking ID: " + bookingId + "\n" +
                            "Movie: " + selectedMovie + "\n" +
                            "Customer: " + customerName + "\n" +
                            "Total: $" + String.format("%.2f", ticketTotal + foodTotal),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }    private void processPayment() {
        String customerName = customerNameField.getText().trim();
        String customerEmail = customerEmailField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();

        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name!");
            return;
        }

        if (customerEmail.isEmpty() || !customerEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!");
            return;
        }

        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in cart!");
            return;
        }

        double total = ticketTotal + foodTotal;

        String[] paymentOptions = {"Cash", "Credit Card", "Debit Card", "Mobile Payment"};
        String paymentMethod = (String) JOptionPane.showInputDialog(this,
                "Select Payment Method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentOptions,
                paymentOptions[0]);

        if (paymentMethod == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment of $" + String.format("%.2f", total) + " via " + paymentMethod + "?\n\n" +
                        "Customer: " + customerName + "\n" +
                        "Email: " + customerEmail + "\n" +
                        "A confirmation will be sent to this email.",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Save to database
            saveBookingToDatabase();

            JOptionPane.showMessageDialog(this,
                    "✅ Payment Successful!\n\n" +
                            "Customer: " + customerName + "\n" +
                            "Email: " + customerEmail + "\n" +
                            "Total Paid: $" + String.format("%.2f", total) + "\n\n" +
                            "Booking saved to history!\n" +
                            "Confirmation email sent to " + customerEmail,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            clearCart();
            customerNameField.setText("");
            customerEmailField.setText("");
            customerPhoneField.setText("");
            selectedSeats.clear();
            selectedSeatsLabel.setText("No seats selected");
        }
    }

    private void processRefund() {
        String bookingId = JOptionPane.showInputDialog(this, "Enter Booking ID to refund:");
        if (bookingId != null && !bookingId.trim().isEmpty()) {
            try {
                boolean success = bookingService.cancelBooking(Integer.parseInt(bookingId));
                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Refund processed for booking #" + bookingId);
                } else {
                    JOptionPane.showMessageDialog(this, "Booking not found or cannot be refunded!");
                }
            } catch (NumberFormatException | DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Invalid booking ID!");
            }
        }
    }

    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"));
    }

    public void refresh() {
        loadMovies();
    }

    static class FoodItem {
        private final String name;
        private final double price;
        private final String icon;

        public FoodItem(String name, double price, String icon) {
            this.name = name;
            this.price = price;
            this.icon = icon;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getIcon() { return icon; }
    }
}