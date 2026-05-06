package main;

import manager.Movie;
import model.User;
import panels.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class CinemaManagementSystem extends JFrame {
    // System components
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private User currentUser = null;

    // Data collections
    public static List<User> users = new ArrayList<>();
    private static final List<String> logs = new ArrayList<>();
    private final List<LogEntry> operationLogs = new ArrayList<>();
    private final List<Movie> movies = new ArrayList<>();
    private final List<Ticket> tickets = new ArrayList<>();
    private final List<Schedule> schedules = new ArrayList<>();

    // Panels
    private PasswordManagementPanel passwordManagementPanel;
    private MovieManagementPanel movieManagementPanel;
    private ScheduleManagementPanel scheduleManagementPanel;
    private SalesDataPanel salesDataPanel;
    private OperationLogPanel operationLogPanel;
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private ManagerPanel managerPanel;
    private FrontDeskPanel frontDeskPanel;



    public List<Ticket> getTickets() {
        return this.tickets;
    }
    static {
        initializeSystemUsers();
    }


    public CinemaManagementSystem() {
        configureMainFrame();
        initializePanels();
        setupCardLayout();
        // Initialize sample schedules if empty
        if (schedules.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            schedules.add(new Schedule("SC001", "流浪地球", "1号厅",
                    now.plusHours(2), 45.0));
            schedules.add(new Schedule("SC002", "长津湖", "VIP厅",
                    now.plusHours(4), 60.0));
            schedules.add(new Schedule("SC003", "你好，李焕英", "2号厅",
                    now.plusDays(1).withHour(14), 40.0));
        }
    }
    public List<Schedule> getSchedulesForMovie(Movie movie) {
        List<Schedule> result = new ArrayList<>();
        if (movie == null || movie.getTitle() == null) {
            return result;
        }

        for (Schedule schedule : schedules) {
            if (schedule != null && movie.getTitle().equals(schedule.getMovieTitle())) {
                result.add(schedule);
            }
        }
        return result;
    }
    private void configureMainFrame() {
        setTitle("万东电影院管理系统");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }


    private void initializePanels() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this, users, logs);
        adminPanel = new AdminPanel(this, users, logs, null);
        managerPanel = new ManagerPanel(this, null);
        frontDeskPanel = new FrontDeskPanel(this, null);

        passwordManagementPanel = new PasswordManagementPanel(this, null);
        movieManagementPanel = new MovieManagementPanel(this, null);
        scheduleManagementPanel = new ScheduleManagementPanel(this, null);
        salesDataPanel = new SalesDataPanel(this, null);
        operationLogPanel = new OperationLogPanel(this, operationLogs);
    }

    private void setupCardLayout() {
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(adminPanel, "ADMIN");
        cardPanel.add(managerPanel, "MANAGER");
        cardPanel.add(frontDeskPanel, "FRONT_DESK");
        cardPanel.add(passwordManagementPanel, "PASSWORD_MANAGEMENT");
        cardPanel.add(movieManagementPanel, "MOVIE_MANAGEMENT");
        cardPanel.add(scheduleManagementPanel, "SCHEDULE_MANAGEMENT");
        cardPanel.add(salesDataPanel, "SALES_DATA");
        cardPanel.add(operationLogPanel, "OPERATION_LOG");

        add(cardPanel);
        showLoginPanel();
    }

    private static void initializeSystemUsers() {
        users.add(new User("U" + (users.size() + 1000), "admin", "ynuinfo#777", "管理员", "13800138000"));
        users.add(new User("U" + (users.size() + 1000), "manager", "123456", "经理", "13900139000"));
        users.add(new User("U" + (users.size() + 1000), "frontdesk", "123456", "前台", "13700137000"));
    }

    // Navigation methods
    public void showLoginPanel() {
        cardLayout.show(cardPanel, "LOGIN");
    }

    public void showAdminPanel(User user) {
        currentUser = user;
        adminPanel.setCurrentUser(user);
        cardLayout.show(cardPanel, "ADMIN");
    }

    public void showManagerPanel(User user) {
        if (user == null) {
            showLoginPanel();
            return;
        }

        currentUser = user;
        if (managerPanel == null) {
            managerPanel = new ManagerPanel(this, user);
            cardPanel.add(managerPanel, "MANAGER");
        } else {
            managerPanel.setCurrentUser(user);
        }
        cardLayout.show(cardPanel, "MANAGER");
    }

    public void showFrontDeskPanel(User user) {
        currentUser = user;
        frontDeskPanel.setCurrentUser(user);
        cardLayout.show(cardPanel, "FRONT_DESK");
    }

    public void showPasswordManagementPanel(User currentUser) {
        passwordManagementPanel.setCurrentUser(this.currentUser);
        cardLayout.show(cardPanel, "PASSWORD_MANAGEMENT");
    }

    public void showMovieManagementPanel(User currentUser) {
        movieManagementPanel.refreshMovieData();
        cardLayout.show(cardPanel, "MOVIE_MANAGEMENT");
    }

    public void showScheduleManagementPanel(User currentUser) {
        if (scheduleManagementPanel == null) {
            scheduleManagementPanel = new ScheduleManagementPanel(this, currentUser);
            cardPanel.add(scheduleManagementPanel, "SCHEDULE_MANAGEMENT");
        } else {
            scheduleManagementPanel.setCurrentUser(currentUser);
            scheduleManagementPanel.refreshScheduleData();
        }
        cardLayout.show(cardPanel, "SCHEDULE_MANAGEMENT");
    }

    public void showSalesDataPanel(User currentUser) {
        salesDataPanel.refreshSalesData();
        cardLayout.show(cardPanel, "SALES_DATA");
    }

    public void showOperationLogPanel(User currentUser) {
        operationLogPanel.refreshLogData();
        cardLayout.show(cardPanel, "OPERATION_LOG");
    }

    // Business logic methods
    public Ticket sellTicket(String movieTitle, String scheduleId, String seats, boolean isChild) {
        Movie movie = movies.stream()
                .filter(m -> m.getTitle().equals(movieTitle))
                .findFirst()
                .orElse(null);

        Schedule schedule = schedules.stream()
                .filter(s -> s.getId().equals(scheduleId))
                .findFirst()
                .orElse(null);

        if (movie == null || schedule == null) {
            return null;
        }

        double price = schedule.getPrice() * (isChild ? 0.7 : 1.0);

        Ticket ticket = new Ticket(
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                movie.getTitle(),
                schedule.getId(),
                schedule.getHall(),
                schedule.getShowTime(),
                seats,
                price,
                LocalDateTime.now()
        );

        tickets.add(ticket);
        return ticket;
    }
    public boolean refundTicket(String ticketId, double refundAmount) {
        // Find and remove the ticket
        Ticket ticket = tickets.stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElse(null);

        if (ticket != null) {
            // Free up the seats
            Schedule schedule = getScheduleById(ticket.getScheduleId());
            if (schedule != null) {
                schedule.freeSeats(ticket.getSeats());
            }
            tickets.remove(ticket);

            // Record the refund transaction
            recordTransaction("REFUND", ticketId, -refundAmount);
            return true;
        }
        return false;
    }

    private void recordTransaction(String type, String ticketId, double amount) {
        // Create a timestamp for the transaction
        LocalDateTime timestamp = LocalDateTime.now();

        // Format the transaction details
        String transactionDetails = String.format(
                "%s - 票号: %s, 金额: %.2f, 操作员: %s",
                type,
                ticketId,
                amount,
                currentUser != null ? currentUser.getUsername() : "系统"
        );

        // Add to operation logs
        operationLogs.add(new LogEntry(
                timestamp,
                "财务交易",
                transactionDetails,
                currentUser != null ? currentUser.getUsername() : "系统"
        ));

        // Also add to a separate transaction log if you have one
        // transactions.add(new Transaction(timestamp, type, ticketId, amount));

        // Print to console for debugging (optional)
        System.out.println("记录交易: " + transactionDetails);
    }




    public Schedule getScheduleById(String scheduleId) {
        return schedules.stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst()
                .orElse(null);
    }

    // Logging methods
    public void logEvent(String operationType, String details) {
        String log = String.format("[%s] %s - %s (%s)",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                operationType,
                details,
                currentUser.getUsername());

        operationLogs.add(new LogEntry(
                LocalDateTime.now(),
                operationType,
                details,
                currentUser.getUsername()
        ));
        addLog("添加用户", log);
    }

    public void logOperation(User user, String action, String details) {
        String username = (user != null) ? user.getUsername() : "SYSTEM";
        String role = (user != null) ? user.getRole() : "SYSTEM";

        String logMessage = String.format("[%s] %s (%s): %s - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                username,
                role,
                action,
                details);

        operationLogs.add(new LogEntry(
                LocalDateTime.now(),
                action,
                details,
                username
        ));
        System.out.println(logMessage);
    }

    public List<LogEntry> getFilteredLogs(String filterType) {
        if ("all".equalsIgnoreCase(filterType)) {
            return new ArrayList<>(operationLogs);
        }

        List<LogEntry> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (LogEntry log : operationLogs) {
            if ("today".equalsIgnoreCase(filterType) &&
                    log.getTimestamp().toLocalDate().equals(today)) {
                filtered.add(log);
            } else if (log.getOperationType().equalsIgnoreCase(filterType)) {
                filtered.add(log);
            }
        }
        return filtered;
    }

    // Utility methods
    public static void addUser(User user) {
        users.add(user);
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    public static void addLog(String operation, String log) {
        logs.add(log);
    }

    public void exitApplication() {
        System.exit(0);
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public static List<String> getLogs() {
        return logs;
    }

    public List<Movie> getMovieList() {
        return movies;
    }

    // Main method
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "未处理异常: " + throwable.getMessage(),
                    "系统错误",
                    JOptionPane.ERROR_MESSAGE);
        });

        SwingUtilities.invokeLater(() -> {
            CinemaManagementSystem system = new CinemaManagementSystem();
            system.setVisible(true);
        });
    }
    public Ticket getTicketById(String ticketId) {
        // Implement this to return your Ticket object
        for (Ticket ticket : tickets) {  // Assuming you have a List<Ticket> tickets
            if (ticket.getId().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }

    // Inner classes for data models
    public static class LogEntry {
        private final LocalDateTime timestamp;
        private final String operationType;
        private final String details;
        private final String operator;

        public LogEntry(LocalDateTime timestamp, String operationType, String details, String operator) {
            this.timestamp = timestamp;
            this.operationType = operationType;
            this.details = details;
            this.operator = operator;
        }

        public String getFormattedLog() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format("[%s] %s - %s (%s)",
                    timestamp.format(formatter),
                    operationType,
                    details,
                    operator);
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getOperationType() { return operationType; }
        public String getDetails() { return details; }
        public String getOperator() { return operator; }
    }

    public static class Schedule {
        private String id;
        private String movieTitle;
        private String hall;
        private LocalDateTime showTime;
        private double basePrice;
        private Set<String> bookedSeats;
        private int rows;
        private int cols;
        private boolean[][] seatAvailability;

        public Schedule(String id, String movieTitle, String hall,
                        LocalDateTime showTime, double basePrice) {
            this.id = id;
            this.movieTitle = movieTitle;
            this.hall = hall;
            this.showTime = showTime;
            this.basePrice = basePrice;
            this.bookedSeats = new HashSet<>();

            // Initialize seat arrangement based on hall type
            switch(hall) {
                case "黄金厅":
                    this.rows = 8;
                    this.cols = 10;
                    break;
                case "白银厅":
                    this.rows = 10;
                    this.cols = 12;
                    break;
                case "翡翠厅":
                case "钻石厅":
                    this.rows = 7;
                    this.cols = 8;
                    break;
                case "宝石厅":
                    this.rows = 8;
                    this.cols = 9;
                    break;
                default:
                    this.rows = 5;
                    this.cols = 5;
            }

            this.seatAvailability = new boolean[rows][cols];
            // Initialize all seats as available
            for (boolean[] row : seatAvailability) {
                Arrays.fill(row, true);
            }
        }

        @Override
        public String toString() {
            return getShowTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) +
                    " - " + hall + " (" + getAvailableSeatsCount() + "/" +
                    getTotalSeats() + "座) ¥" + basePrice;
        }

        // Getters
        public String getId() { return id; }
        public String getMovieTitle() { return movieTitle; }
        public String getHall() { return hall; }
        public LocalDateTime getShowTime() { return showTime; }
        public double getPrice() { return basePrice; }
        public int getRows() { return rows; }
        public int getCols() { return cols; }
        public int getTotalSeats() { return rows * cols; }

        // Seat availability methods
        public boolean isSeatAvailable(int row, int col) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                return seatAvailability[row][col];
            }
            return false;
        }

        public boolean isSeatAvailable(String seat) {
            try {
                String[] parts = seat.split("排|座");
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                return isSeatAvailable(row, col);
            } catch (Exception e) {
                return false;
            }
        }

        public int getAvailableSeatsCount() {
            int count = 0;
            for (boolean[] row : seatAvailability) {
                for (boolean available : row) {
                    if (available) count++;
                }
            }
            return count;
        }

        // Seat booking methods
        public boolean bookSeat(int row, int col) {
            if (isSeatAvailable(row, col)) {
                seatAvailability[row][col] = false;
                bookedSeats.add((row+1) + "排" + (col+1) + "座");
                return true;
            }
            return false;
        }

        public boolean bookSeat(String seat) {
            try {
                String[] parts = seat.split("排|座");
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                return bookSeat(row, col);
            } catch (Exception e) {
                return false;
            }
        }

        public boolean bookSeats(String seats) {
            String[] seatArray = seats.split(",");
            // First check all seats are available
            for (String seat : seatArray) {
                if (!isSeatAvailable(seat.trim())) {
                    return false;
                }
            }
            // Then book all seats
            for (String seat : seatArray) {
                bookSeat(seat.trim());
            }
            return true;
        }

        // Seat freeing methods
        public void freeSeat(int row, int col) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                seatAvailability[row][col] = true;
                bookedSeats.remove((row+1) + "排" + (col+1) + "座");
            }
        }

        public void freeSeats(String seats) {
            String[] seatArray = seats.split(",");
            for (String seat : seatArray) {
                try {
                    String[] parts = seat.trim().split("排|座");
                    int row = Integer.parseInt(parts[0]) - 1;
                    int col = Integer.parseInt(parts[1]) - 1;
                    freeSeat(row, col);
                } catch (Exception e) {
                    // Ignore invalid seat formats
                }
            }
        }

        // Setters
        public void setHall(String hall) {
            this.hall = hall;
            // Reinitialize seating when hall changes
            new Schedule(id, movieTitle, hall, showTime, basePrice);
        }
        public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
        public void setPrice(double price) { this.basePrice = price; }
    }

    public static class Ticket {
        private String id;
        private String movieTitle;
        private String scheduleId;
        private String hall;
        private LocalDateTime showTime;
        private String seats;
        private double price;
        private LocalDateTime purchaseTime;


        public Ticket(String id, String movieTitle, String scheduleId, String hall,
                      LocalDateTime showTime, String seats, double price, LocalDateTime purchaseTime) {
            this.id = id;
            this.movieTitle = movieTitle;
            this.scheduleId = scheduleId;
            this.hall = hall;
            this.showTime = showTime;
            this.seats = seats;
            this.price = price;
            this.purchaseTime = purchaseTime;
        }


        // Getters
        public String getId() { return id; }
        public String getMovieTitle() { return movieTitle; }
        public String getScheduleId() { return this.scheduleId;}
        public String getSeats() { return seats; }
        public double getPrice() { return this.price; }

    }

    // Panel classes
    class PasswordManagementPanel extends JPanel {
        private final CinemaManagementSystem system;
        private User currentUser;
        private JTextField usernameField;
        private JPasswordField currentPasswordField;
        private JPasswordField newPasswordField;
        private JPasswordField confirmPasswordField;

        public PasswordManagementPanel(CinemaManagementSystem system, User user) {
            this.system = system;
            this.currentUser = user;
            initUI();
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titleLabel = new JLabel("密码管理", JLabel.CENTER);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            add(titleLabel, BorderLayout.NORTH);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("修改密码", createChangePasswordPanel());
            tabbedPane.addTab("重置前台密码", createResetPasswordPanel());
            add(tabbedPane, BorderLayout.CENTER);

            JButton backButton = new JButton("返回经理面板");
            backButton.addActionListener(e -> system.showManagerPanel(currentUser));
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(backButton);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private JPanel createChangePasswordPanel() {
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("当前密码:"));
            currentPasswordField = new JPasswordField();
            panel.add(currentPasswordField);

            panel.add(new JLabel("新密码:"));
            newPasswordField = new JPasswordField();
            panel.add(newPasswordField);

            panel.add(new JLabel("确认密码:"));
            confirmPasswordField = new JPasswordField();
            panel.add(confirmPasswordField);

            panel.add(new JLabel());
            JButton submitButton = new JButton("修改密码");
            submitButton.addActionListener(e -> changePassword());
            panel.add(submitButton);

            return panel;
        }

        private JPanel createResetPasswordPanel() {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("前台用户名:"));
            usernameField = new JTextField();
            panel.add(usernameField);

            panel.add(new JLabel("重置后密码:"));
            JLabel defaultPassword = new JLabel("123456 (用户首次登录需修改)");
            defaultPassword.setForeground(Color.RED);
            panel.add(defaultPassword);

            panel.add(new JLabel());
            JButton resetButton = new JButton("重置密码");
            resetButton.addActionListener(e -> resetPassword());
            panel.add(resetButton);

            return panel;
        }

        private void changePassword() {
            char[] currentPassword = currentPasswordField.getPassword();
            char[] newPassword = newPasswordField.getPassword();
            char[] confirmPassword = confirmPasswordField.getPassword();

            if (this.currentUser == null) {
                JOptionPane.showMessageDialog(this, "No user is currently logged in!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentPassword.length == 0 || newPassword.length == 0 || confirmPassword.length == 0) {
                JOptionPane.showMessageDialog(this, "所有字段都必须填写", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!String.valueOf(newPassword).equals(String.valueOf(confirmPassword))) {
                JOptionPane.showMessageDialog(this, "新密码和确认密码不匹配", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!String.valueOf(currentPassword).equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(this, "当前密码不正确", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser.setPassword(String.valueOf(newPassword));
            JOptionPane.showMessageDialog(this, "密码修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }

        private void resetPassword() {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入用户名", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User targetUser = null;
            for (User user : CinemaManagementSystem.users) {
                if (user.getUsername().equals(username) && "前台".equals(user.getRole())) {
                    targetUser = user;
                    break;
                }
            }

            if (targetUser == null) {
                JOptionPane.showMessageDialog(this, "未找到前台用户: " + username, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            targetUser.setPassword("123456");
            targetUser.setPasswordResetRequired(true);
            system.logEvent("密码重置", "重置前台用户 " + username + " 的密码");
            JOptionPane.showMessageDialog(this, "密码已重置为123456", "成功", JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText("");
        }

        public void setCurrentUser(User user) {
            this.currentUser = user;
        }
    }

    static class MovieManagementPanel extends JPanel {
        private final CinemaManagementSystem system;
        private User currentUser;
        private JTable movieTable;
        private DefaultTableModel tableModel;
        private final List<Movie> movieList = new ArrayList<>();

        // Form components
        private JTextField titleField, directorField, actorsField, durationField, ratingField;
        private JTextArea descriptionArea;
        private JCheckBox screeningCheckBox;

        public MovieManagementPanel(CinemaManagementSystem system, User user) {
            this.system = system;
            this.currentUser = user;
            initUI();
            loadMovieData();
        }

        private void initUI() {
            setLayout(new BorderLayout());
            setBackground(new Color(240, 245, 250));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            add(createHeaderPanel(), BorderLayout.NORTH);
            add(createTabbedPane(), BorderLayout.CENTER);
            add(createFooterPanel(), BorderLayout.SOUTH);
        }

        private JPanel createHeaderPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(50, 100, 150));
            panel.setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel("影片管理系统");
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("管理影院所有影片信息");
            subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            subtitle.setForeground(new Color(200, 220, 240));

            panel.add(titleLabel);
            panel.add(subtitle);
            return panel;
        }

        private JTabbedPane createTabbedPane() {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(Color.WHITE);
            tabbedPane.setForeground(new Color(70, 130, 180));
            tabbedPane.setFont(new Font("微软雅黑", Font.BOLD, 14));

            tabbedPane.addTab("影片列表", createMovieListPanel());
            tabbedPane.addTab("添加影片", createAddMoviePanel());
            tabbedPane.addTab("搜索影片", createSearchPanel());

            return tabbedPane;
        }

        private JPanel createMovieListPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 5, 10, 5));

            // Toolbar
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setBorder(new EmptyBorder(0, 0, 10, 0));

            JButton refreshBtn = createToolButton("刷新", "🔄", e -> refreshMovieData());
            JButton editBtn = createToolButton("编辑", "✏️", e -> editSelectedMovie());
            JButton deleteBtn = createToolButton("删除", "🗑️", e -> deleteSelectedMovie());

            toolBar.add(refreshBtn);
            toolBar.add(editBtn);
            toolBar.add(deleteBtn);

            // Table
            String[] columns = {"ID", "片名", "导演", "主演", "时长", "评分", "上映状态"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            movieTable = new JTable(tableModel);
            movieTable.setRowHeight(30);
            movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            movieTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));

            JScrollPane scrollPane = new JScrollPane(movieTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));

            panel.add(toolBar, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JButton createToolButton(String text, String icon, ActionListener listener) {
            JButton button = new JButton(text + " " + icon);
            button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            button.addActionListener(listener);
            button.setFocusPainted(false);
            return button;
        }

        private JPanel createAddMoviePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBorder(BorderFactory.createTitledBorder("影片信息"));

            // Form fields
            titleField = createFormField("片名:", 25);
            directorField = createFormField("导演:", 20);
            actorsField = createFormField("主演:", 30);
            durationField = createFormField("时长(分钟):", 10);
            ratingField = createFormField("评分(0-10):", 10);

            screeningCheckBox = new JCheckBox("正在上映");
            screeningCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

            JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            checkPanel.add(screeningCheckBox);

            descriptionArea = new JTextArea(5, 30);
            descriptionArea.setLineWrap(true);
            JScrollPane descScroll = new JScrollPane(descriptionArea);

            // Add components to form
            formPanel.add(createFieldPanel("片名:", titleField));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("导演:", directorField));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("主演:", actorsField));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("时长:", durationField));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("评分:", ratingField));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(checkPanel);
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("剧情简介:", descScroll));

            // Submit button
            JButton submitBtn = new JButton("添加影片");
            submitBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            submitBtn.setBackground(new Color(70, 130, 180));
            submitBtn.setForeground(Color.WHITE);
            submitBtn.addActionListener(e -> addNewMovie());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(submitBtn);

            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JTextField createFormField(String placeholder, int columns) {
            JTextField field = new JTextField(columns);
            field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            return field;
        }

        private JPanel createFieldPanel(String label, Component field) {
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            JLabel jLabel = new JLabel(label);
            jLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

            panel.add(jLabel, BorderLayout.WEST);
            panel.add(field, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createSearchPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Search controls
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

            JPanel criteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            String[] searchTypes = {"按ID", "按片名", "按导演", "按主演", "按评分"};
            JComboBox<String> searchType = new JComboBox<>(searchTypes);
            JTextField searchField = new JTextField(20);
            JButton searchBtn = new JButton("搜索");

            criteriaPanel.add(new JLabel("搜索方式:"));
            criteriaPanel.add(searchType);
            criteriaPanel.add(Box.createHorizontalStrut(10));
            criteriaPanel.add(searchField);
            criteriaPanel.add(Box.createHorizontalStrut(10));
            criteriaPanel.add(searchBtn);

            // Results table
            DefaultTableModel resultsModel = new DefaultTableModel(
                    new String[]{"ID", "片名", "导演", "主演", "评分", "上映状态"}, 0);
            JTable resultsTable = new JTable(resultsModel);
            resultsTable.setRowHeight(30);

            searchBtn.addActionListener(e -> {
                String query = searchField.getText();
                String type = (String)searchType.getSelectedItem();
                searchMovies(query, type, resultsModel);
            });

            searchPanel.add(criteriaPanel);
            searchPanel.add(Box.createVerticalStrut(15));
            searchPanel.add(new JScrollPane(resultsTable));

            panel.add(searchPanel, BorderLayout.CENTER);
            return panel;
        }

        private void searchMovies(String query, String type, DefaultTableModel resultsModel) {
            resultsModel.setRowCount(0);

            if (query == null || query.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入搜索内容",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Movie> results = movieList.stream()
                    .filter(m -> {
                        if (m == null) return false;
                        switch (type) {
                            case "按ID":
                                return m.getId() != null && m.getId().toString().contains(query);
                            case "按片名":
                                return m.getTitle() != null && m.getTitle().contains(query);
                            case "按导演":
                                return m.getDirector() != null && m.getDirector().contains(query);
                            case "按主演":
                                return m.getActors() != null && m.getActors().contains(query);
                            case "按评分":
                                try {
                                    double minRating = Double.parseDouble(query);
                                    return m.getRating() >= minRating;
                                } catch (NumberFormatException e) {
                                    return false;
                                }
                            default: return false;
                        }
                    })
                    .toList();

            for (Movie m : results) {
                resultsModel.addRow(new Object[]{
                        m.getId(),
                        m.getTitle(),
                        m.getDirector(),
                        m.getActors(),
                        m.getRating(),
                        m.isScreening() ? "上映中" : "已下架"
                });
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "没有找到匹配的影片",
                        "搜索结果", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        private JPanel createFooterPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel.setBorder(new EmptyBorder(10, 0, 0, 0));

            JButton backBtn = new JButton("返回主菜单");
            backBtn.addActionListener(e -> system.showManagerPanel(currentUser));

            panel.add(backBtn);
            return panel;
        }

        private void loadMovieData() {
            movieList.clear();
            movieList.add(new Movie("M001", "流浪地球", "郭帆", "吴京,屈楚萧", 125, 8.5, "科幻冒险", true));
            movieList.add(new Movie("M002", "长津湖", "陈凯歌", "吴京,易烊千玺", 176, 9.0, "战争历史", true));
// Add these to your movie initialization code
            movieList.add(new Movie("M003", "你好，李焕英", "贾玲", "贾玲,张小斐,沈腾", 128, 8.2, "喜剧亲情", true));
            movieList.add(new Movie("M004", "哪吒之魔童降世", "饺子", "吕艳婷,囧森瑟夫,瀚墨", 110, 8.4, "动画奇幻", true));
            movieList.add(new Movie("M005", "少年的你", "曾国祥", "周冬雨,易烊千玺", 135, 8.3, "青春剧情", true));
            movieList.add(new Movie("M006", "唐人街探案3", "陈思诚", "王宝强,刘昊然,妻夫木聪", 136, 7.1, "喜剧悬疑", true));
            movieList.add(new Movie("M007", "我和我的祖国", "陈凯歌等", "黄渤,张译,吴京", 158, 9.2, "主旋律剧情", true));
            refreshTable();
        }

        private void refreshTable() {
            tableModel.setRowCount(0);
            for (Movie movie : movieList) {
                String id = movie.getId() != null ? movie.getId().toString() : "N/A";
                tableModel.addRow(new Object[]{
                        id,
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getActors(),
                        movie.getDuration(),
                        movie.getRating(),
                        movie.isScreening() ? "上映中" : "已下架"
                });
            }
        }

        private void addNewMovie() {
            try {
                if (titleField.getText().isEmpty()) {
                    throw new Exception("片名不能为空");
                }

                String newId = generateNewMovieId();

                Movie movie = new Movie(
                        newId,
                        titleField.getText(),
                        directorField.getText(),
                        actorsField.getText(),
                        Integer.parseInt(durationField.getText()),
                        Double.parseDouble(ratingField.getText()),
                        descriptionArea.getText(),
                        screeningCheckBox.isSelected()
                );

                movieList.add(movie);
                refreshTable();
                clearForm();

                JOptionPane.showMessageDialog(this, "影片添加成功!", "成功",
                        JOptionPane.INFORMATION_MESSAGE);

                system.logOperation(currentUser, "影片管理", "添加影片: " + movie.getTitle());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage(),
                        "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private String generateNewMovieId() {
            int maxId = 0;
            for (Movie m : movieList) {
                if (m.getId() != null) {
                    String id = m.getId().toString();
                    if (id.startsWith("M")) {
                        try {
                            int num = Integer.parseInt(id.substring(1));
                            if (num > maxId) maxId = num;
                        } catch (NumberFormatException e) {
                            // Ignore malformed IDs
                        }
                    }
                }
            }
            return String.format("M%03d", maxId + 1);
        }

        private void clearForm() {
            titleField.setText("");
            directorField.setText("");
            actorsField.setText("");
            durationField.setText("");
            ratingField.setText("");
            descriptionArea.setText("");
            screeningCheckBox.setSelected(false);
        }

        private void editSelectedMovie() {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要编辑的影片",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String movieId = movieTable.getValueAt(selectedRow, 0).toString();
            if (movieId.equals("N/A")) {
                JOptionPane.showMessageDialog(this, "无效的影片ID",
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Movie movie = findMovieById(movieId);
            if (movie == null) {
                JOptionPane.showMessageDialog(this, "找不到指定的影片",
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog editDialog = new JDialog();
            editDialog.setTitle("编辑影片");
            editDialog.setSize(500, 500);
            editDialog.setModal(true);
            editDialog.setLayout(new BorderLayout());

            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JTextField editTitle = new JTextField(movie.getTitle());
            JTextField editDirector = new JTextField(movie.getDirector());
            JTextField editActors = new JTextField(movie.getActors());
            JTextField editDuration = new JTextField(String.valueOf(movie.getDuration()));
            JTextField editRating = new JTextField(String.valueOf(movie.getRating()));
            JTextArea editDescription = new JTextArea(movie.getDescription());
            JCheckBox editScreening = new JCheckBox("正在上映", movie.isScreening());

            editPanel.add(createFieldPanel("片名:", editTitle));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(createFieldPanel("导演:", editDirector));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(createFieldPanel("主演:", editActors));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(createFieldPanel("时长:", editDuration));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(createFieldPanel("评分:", editRating));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(createFieldPanel("剧情简介:", new JScrollPane(editDescription)));
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(editScreening);

            JButton saveBtn = new JButton("保存修改");
            saveBtn.addActionListener(e -> {
                try {
                    movie.setTitle(editTitle.getText());
                    movie.setDirector(editDirector.getText());
                    movie.setActors(editActors.getText());
                    movie.setDuration(Integer.parseInt(editDuration.getText()));
                    movie.setRating(Double.parseDouble(editRating.getText()));
                    movie.setDescription(editDescription.getText());
                    movie.setScreening(editScreening.isSelected());

                    refreshTable();
                    editDialog.dispose();

                    system.logOperation(currentUser, "影片管理", "修改影片: " + movie.getTitle());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "错误: " + ex.getMessage(),
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveBtn);

            editDialog.add(editPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);
            editDialog.setVisible(true);
        }

        private void deleteSelectedMovie() {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的影片",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String movieId = movieTable.getValueAt(selectedRow, 0).toString();
            if (movieId.equals("N/A")) {
                JOptionPane.showMessageDialog(this, "无效的影片ID",
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Movie movie = findMovieById(movieId);
            if (movie == null) {
                JOptionPane.showMessageDialog(this, "找不到指定的影片",
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除影片 '" + movie.getTitle() + "' 吗?",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (hasSchedules(movie)) {
                    JOptionPane.showMessageDialog(this,
                            "无法删除: 影片已有排片信息!",
                            "删除失败",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                movieList.remove(movie);
                refreshTable();

                system.logOperation(currentUser, "影片管理", "删除影片: " + movie.getTitle());
            }
        }

        private boolean hasSchedules(Movie movie) {
            return system.getSchedules().stream()
                    .anyMatch(s -> s.getMovieTitle().equals(movie.getTitle()));
        }

        private Movie findMovieById(String id) {
            if (id == null) return null;

            return movieList.stream()
                    .filter(m -> m != null && id.equals(m.getId()))
                    .findFirst()
                    .orElse(null);
        }

        public void refreshMovieData() {
            loadMovieData();
        }
    }
    class SalesDataPanel extends JPanel {
        private final CinemaManagementSystem system;
        private JTabbedPane tabbedPane;

        public SalesDataPanel(CinemaManagementSystem system, User user) {
            this.system = system;
            initUI();
            loadSalesData();
        }

        private SalesDataPanel loadSalesData() {
            return salesDataPanel;
        }

        private void initUI() {
            setLayout(new BorderLayout());

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("当日销售", createSalesPanel("当日"));
            tabbedPane.addTab("当周销售", createSalesPanel("当周"));
            tabbedPane.addTab("当月销售", createSalesPanel("当月"));
            add(tabbedPane, BorderLayout.CENTER);

            JButton backButton = new JButton("返回经理面板");
            backButton.addActionListener(e -> system.showManagerPanel(system.getCurrentUser()));
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(backButton);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private JPanel createSalesPanel(String period) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(createSalesTable(period), BorderLayout.CENTER);
            return panel;
        }

        private JComponent createSalesTable(String period) {
            String[] columns = {"场次ID", "影片名称", "放映厅", "放映时间", "销售票数", "总金额", "退票数", "退票金额"};
            // Table data with all 8 screenings across 5 halls
            Object[][] data = {
                    {"SC001", "流浪地球", "黄金厅", "2023-05-01 14:00", 120, 4800.0, 5, 200.0},
                    {"SC002", "哪吒之魔童降世", "白银厅", "2023-05-01 16:30", 95, 3800.0, 3, 120.0},
                    {"SC003", "Cinderella", "翡翠厅", "2025-05-02 18:30", 95, 3800.0, 3, 120.0},
                    {"SC004", "你好，李焕英", "钻石厅", "2023-05-01 19:45", 105, 4200.0, 2, 80.0},
                    {"SC005", "长津湖", "宝石厅", "2023-05-02 10:00", 150, 6000.0, 8, 320.0},
                    {"SC006", "少年的你", "黄金厅", "2023-05-02 13:15", 90, 3600.0, 4, 160.0},
                    {"SC007", "唐人街探案3", "白银厅", "2023-05-02 16:00", 110, 4400.0, 6, 240.0},
                    {"SC008", "我和我的祖国", "翡翠厅", "2023-05-02 20:00", 130, 5200.0, 1, 40.0}
            };
            return new JTable(data, columns);
        }

        public void refreshSalesData() {

        }
    }

    class OperationLogPanel extends JPanel {
        private final CinemaManagementSystem system;
        private final List<LogEntry> logEntries;
        private JTable logTable;
        private LogTableModel tableModel;

        public OperationLogPanel(CinemaManagementSystem system, List<LogEntry> logEntries) {
            this.system = system;
            this.logEntries = logEntries;
            initUI();
            loadLogData();
        }

        private void initUI() {
            setLayout(new BorderLayout());

            tableModel = new LogTableModel();
            logTable = new JTable(tableModel);
            add(new JScrollPane(logTable), BorderLayout.CENTER);

            JButton backButton = new JButton("返回经理面板");
            backButton.addActionListener(e -> system.showManagerPanel(currentUser));
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(backButton);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private void loadLogData() {
            tableModel.setLogEntries(logEntries);
        }

        public void refreshLogData() {
            tableModel.fireTableDataChanged();
        }
    }

    static class LogTableModel extends AbstractTableModel {
        private final String[] columnNames = {"时间", "操作类型", "详情", "操作人"};
        private List<LogEntry> logEntries = new ArrayList<>();

        public void setLogEntries(List<LogEntry> logEntries) {
            this.logEntries = logEntries;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return logEntries.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LogEntry log = logEntries.get(rowIndex);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            return switch (columnIndex) {
                case 0 -> log.getTimestamp().format(formatter);
                case 1 -> log.getOperationType();
                case 2 -> log.getDetails();
                case 3 -> log.getOperator();
                default -> null;
            };
        }
    }

    class ScheduleManagementPanel extends JPanel {
        private final CinemaManagementSystem system;
        private User currentUser;
        private JTable scheduleTable;
        private DefaultTableModel tableModel;
        private JComboBox<Movie> movieComboBox;
        private JComboBox<String> hallComboBox;
        private JSpinner dateSpinner, timeSpinner, priceSpinner;

        public ScheduleManagementPanel(CinemaManagementSystem system, User user) {
            this.system = system;
            this.currentUser = user;
            // Initialize with sample movies if empty
            if (system.getMovies().isEmpty()) {
                system.getMovies().addAll(Arrays.asList(
                        new Movie("M001", "流浪地球", "郭帆", "吴京,屈楚萧", 125, 8.5, "科幻冒险", true),
                        new Movie("M002", "长津湖", "陈凯歌", "吴京,易烊千玺", 176, 9.0, "战争历史", true),
                        new Movie("M003", "你好，李焕英", "贾玲", "贾玲,张小斐,沈腾", 128, 8.2, "喜剧亲情", true),
                        new Movie("M004", "哪吒之魔童降世", "饺子", "吕艳婷,囧森瑟夫,瀚墨", 110, 8.4, "动画奇幻", true),
                        new Movie("M005", "少年的你", "曾国祥", "周冬雨,易烊千玺", 135, 8.3, "青春剧情", true),
                        new Movie("M006", "唐人街探案3", "陈思诚", "王宝强,刘昊然,妻夫木聪", 136, 7.1, "喜剧悬疑", true),
                        new Movie("M007", "我和我的祖国", "陈凯歌等", "黄渤,张译,吴京", 158, 9.2, "主旋律剧情", true)
                ));
            }
            initUI();
            loadScheduleData();
        }
        private void initUI() {
            setLayout(new BorderLayout());
            setBackground(new Color(240, 245, 250));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            add(createHeaderPanel(), BorderLayout.NORTH);
            add(createMainPanel(), BorderLayout.CENTER);
            add(createFooterPanel(), BorderLayout.SOUTH);
        }
        // Add these sample movies at the class level
        private List<Movie> sampleMovies = Arrays.asList(
                new Movie("M001", "流浪地球", "郭帆", "吴京,屈楚萧", 125, 8.5, "科幻冒险", true),
                new Movie("M002", "长津湖", "陈凯歌", "吴京,易烊千玺", 176, 9.0, "战争历史", true),
                new Movie("M003", "你好，李焕英", "贾玲", "贾玲,张小斐,沈腾", 128, 8.2, "喜剧亲情", true),
                new Movie("M004", "哪吒之魔童降世", "饺子", "吕艳婷,囧森瑟夫,瀚墨", 110, 8.4, "动画奇幻", true),
                new Movie("M005", "少年的你", "曾国祥", "周冬雨,易烊千玺", 135, 8.3, "青春剧情", true),
                new Movie("M006", "唐人街探案3", "陈思诚", "王宝强,刘昊然,妻夫木聪", 136, 7.1, "喜剧悬疑", true),
                new Movie("M007", "我和我的祖国", "陈凯歌等", "黄渤,张译,吴京", 158, 9.2, "主旋律剧情", true),
                new Movie("M008", "战狼2", "吴京", "吴京,弗兰克·格里罗", 123, 7.6, "动作军事", true),
                new Movie("M009", "红海行动", "林超贤", "张译,黄景瑜", 138, 8.3, "军事动作", true),
                new Movie("M010", "我不是药神", "文牧野", "徐峥,周一围", 117, 9.0, "社会剧情", true)
        );

        // Add these sample schedules
        private List<Schedule> sampleSchedules = Arrays.asList(
                // 流浪地球 (The Wandering Earth) - Multiple showtimes
                new Schedule("SC001", "流浪地球", "黄金厅",
                        LocalDateTime.now().plusHours(2), 45.0),
                new Schedule("SC002", "流浪地球", "翡翠厅",
                        LocalDateTime.now().plusHours(5), 45.0),

                // 长津湖 (The Battle at Lake Changjin) - Premium showings
                new Schedule("SC003", "长津湖", "宝石厅",
                        LocalDateTime.now().plusHours(3), 60.0),
                new Schedule("SC004", "长津湖", "钻石厅",
                        LocalDateTime.now().plusDays(1).withHour(19), 70.0),

                // 你好，李焕英 (Hi, Mom)
                new Schedule("SC005", "你好，李焕英", "白银厅",
                        LocalDateTime.now().plusHours(1), 40.0),
                new Schedule("SC006", "你好，李焕英", "黄金厅",
                        LocalDateTime.now().plusDays(1).withHour(14), 40.0),

                // 哪吒之魔童降世 (Ne Zha)
                new Schedule("SC007", "哪吒之魔童降世", "翡翠厅",
                        LocalDateTime.now().plusHours(4), 35.0),
                new Schedule("SC008", "哪吒之魔童降世", "宝石厅",
                        LocalDateTime.now().plusDays(2).withHour(16), 35.0),

                // 少年的你 (Better Days)
                new Schedule("SC009", "少年的你", "钻石厅",
                        LocalDateTime.now().plusHours(6), 45.0),
                new Schedule("SC010", "少年的你", "白银厅",
                        LocalDateTime.now().plusDays(1).withHour(21), 45.0),

                // 唐人街探案3 (Detective Chinatown 3)
                new Schedule("SC011", "唐人街探案3", "黄金厅",
                        LocalDateTime.now().plusHours(2).plusMinutes(30), 50.0),
                new Schedule("SC012", "唐人街探案3", "宝石厅",
                        LocalDateTime.now().plusDays(3).withHour(15), 50.0),

                // 我和我的祖国 (My People, My Country)
                new Schedule("SC013", "我和我的祖国", "钻石厅",
                        LocalDateTime.now().plusDays(1).withHour(10), 55.0),
                new Schedule("SC014", "我和我的祖国", "白银厅",
                        LocalDateTime.now().plusDays(2).withHour(20), 55.0)
        );;


        private JPanel createHeaderPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(70, 130, 180));
            panel.setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel("排片管理系统");
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("管理影院放映场次");
            subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            subtitle.setForeground(new Color(200, 220, 240));

            panel.add(titleLabel);
            panel.add(subtitle);
            return panel;
        }


        private JPanel createMainPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 5, 10, 5));

            // Toolbar
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setBorder(new EmptyBorder(0, 0, 10, 0));

            JButton addBtn = createToolButton("添加场次", "➕", e -> showAddScheduleDialog());
            JButton editBtn = createToolButton("修改场次", "✏️", e -> editSelectedSchedule());
            JButton deleteBtn = createToolButton("删除场次", "🗑️", e -> deleteSelectedSchedule());
            JButton autoBtn = createToolButton("自动排片", "⚡", e -> autoSchedule());

            toolBar.add(addBtn);
            toolBar.add(editBtn);
            toolBar.add(deleteBtn);
            toolBar.add(autoBtn);

            // Table
            String[] columns = {"场次ID", "影片名称", "放映厅", "放映时间", "价格", "已售座位"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            scheduleTable = new JTable(tableModel);
            scheduleTable.setRowHeight(30);
            scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scheduleTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));

            JScrollPane scrollPane = new JScrollPane(scheduleTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));

            panel.add(toolBar, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JButton createToolButton(String text, String icon, ActionListener listener) {
            JButton button = new JButton(text + " " + icon);
            button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            button.addActionListener(listener);
            button.setFocusPainted(false);
            return button;
        }

        private JPanel createFooterPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel.setBorder(new EmptyBorder(10, 0, 0, 0));

            JButton backBtn = new JButton("返回主菜单");
            backBtn.addActionListener(e -> system.showManagerPanel(currentUser));

            panel.add(backBtn);
            return panel;
        }

        private void loadScheduleData() {
            schedules.clear();
            // Add sample schedules
            schedules.addAll(sampleSchedules);

            // Refresh the table
            refreshTable();
            List<Movie> movies = system.getMovies();

            if (movies.isEmpty()) {
                refreshTable();
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            // Only add schedules if we have movies
            schedules.add(new Schedule(
                    "SC001",
                    movies.get(0).getTitle(),
                    "1号厅",
                    now.plusDays(1).withHour(14).withMinute(0),
                    45.0
            ));

            if (movies.size() > 1) {
                schedules.add(new Schedule(
                        "SC002",
                        movies.get(1).getTitle(),
                        "VIP厅",
                        now.plusDays(1).withHour(19).withMinute(30),
                        60.0
                ));
            }

            refreshTable();
        }
        private void refreshTable() {
            tableModel.setRowCount(0);
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Schedule schedule : schedules) {
                // Find the movie to get its duration
                Movie movie = sampleMovies.stream()
                        .filter(m -> m.getTitle().equals(schedule.getMovieTitle()))
                        .findFirst()
                        .orElse(null);

                String duration = movie != null ? movie.getDuration() + "分钟" : "N/A";

                tableModel.addRow(new Object[]{
                        schedule.getId(),
                        schedule.getMovieTitle(),
                        schedule.getHall(),
                        schedule.getShowTime().format(timeFormat),
                        String.format("¥%.2f", schedule.getPrice()),
                        "0/100", // Placeholder for sold seats
                        duration  // Add duration column
                });
            }        }

        private void showAddScheduleDialog() {
            JDialog dialog = new JDialog();
            dialog.setTitle("添加新场次");
            dialog.setModal(true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);

            // Movie selection - use sample movies
            movieComboBox = new JComboBox<>();
            for (Movie movie : sampleMovies) {
                movieComboBox.addItem(movie);
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Form components
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

            // Movie selection
            movieComboBox = new JComboBox<>();
            system.getMovies().forEach(movieComboBox::addItem);

            // Hall selection
            hallComboBox = new JComboBox<>(new String[]{"黄金厅", "白银厅", "翡翠厅", "钻石厅","玉石厅"});

            // Date and time
            dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);

            timeSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
            timeSpinner.setEditor(timeEditor);

            // Price
            priceSpinner = new JSpinner(new SpinnerNumberModel(50.0, 30.0, 200.0, 5.0));

            // Add fields to form
            formPanel.add(createFieldPanel("选择影片:", movieComboBox));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("放映厅:", hallComboBox));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("放映日期:", dateSpinner));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("放映时间:", timeSpinner));
            formPanel.add(Box.createVerticalStrut(10));
            formPanel.add(createFieldPanel("票价:", priceSpinner));
            // Movie selection
            movieComboBox = new JComboBox<>();
            List<Movie> availableMovies = system.getMovies();
            if (availableMovies.isEmpty()) {
                JOptionPane.showMessageDialog(this, "没有可用的影片，请先添加影片",
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            availableMovies.forEach(movieComboBox::addItem);

            // Submit button
            JButton submitBtn = new JButton("添加场次");
            submitBtn.addActionListener(e -> {
                addNewSchedule();
                dialog.dispose();
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(submitBtn);

            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        }

        private JPanel createFieldPanel(String label, Component field) {
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            JLabel jLabel = new JLabel(label);
            jLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

            panel.add(jLabel, BorderLayout.WEST);
            panel.add(field, BorderLayout.CENTER);

            return panel;
        }

        private void addNewSchedule() {
            try {
                Movie selectedMovie = (Movie)movieComboBox.getSelectedItem();
                if (selectedMovie == null) {
                    throw new Exception("请先选择影片");
                }

                String hall = (String)hallComboBox.getSelectedItem();
                if (hall == null || hall.isEmpty()) {
                    throw new Exception("请选择放映厅");
                }

                // Rest of your existing code...
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage(),
                        "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        private boolean hasScheduleConflict(String hall, LocalDateTime showTime, String id) {
            return schedules.stream()
                    .anyMatch(s -> s.getHall().equals(hall) &&
                            Math.abs(s.getShowTime().until(showTime, java.time.temporal.ChronoUnit.MINUTES)) < 120);
        }

        private void editSelectedSchedule() {
            // 1. Validate selection
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("请先选择要修改的场次", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Get selected schedule
            String scheduleId = (String) scheduleTable.getValueAt(selectedRow, 0);
            Schedule schedule = findScheduleById(scheduleId);
            if (schedule == null) {
                showMessage("找不到选定的场次", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Create simple edit dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("修改场次信息");
            dialog.setLayout(new BorderLayout());

            // Form components
            JTextField movieField = new JTextField(schedule.getMovieTitle());
            movieField.setEditable(false);

            JComboBox<String> hallBox = new JComboBox<>(new String[]{"1号厅", "2号厅", "VIP厅"});
            hallBox.setSelectedItem(schedule.getHall());

            JSpinner dateSpinner = createDateTimeSpinner(schedule.getShowTime(), "yyyy-MM-dd");
            JSpinner timeSpinner = createDateTimeSpinner(schedule.getShowTime(), "HH:mm");
            JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(schedule.getPrice(), 30, 200, 5));

            // Build form
            JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
            form.add(new JLabel("影片名称:"));
            form.add(movieField);
            form.add(new JLabel("放映厅:"));
            form.add(hallBox);
            form.add(new JLabel("放映日期:"));
            form.add(dateSpinner);
            form.add(new JLabel("放映时间:"));
            form.add(timeSpinner);
            form.add(new JLabel("票价:"));
            form.add(priceSpinner);

            // Submit button
            JButton saveBtn = new JButton("保存");
            saveBtn.addActionListener(e -> saveScheduleChanges(
                    dialog, schedule, hallBox, dateSpinner, timeSpinner, priceSpinner));

            dialog.add(form, BorderLayout.CENTER);
            dialog.add(saveBtn, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        // Helper method to create date/time spinners
        private JSpinner createDateTimeSpinner(LocalDateTime time, String format) {
            JSpinner spinner = new JSpinner(new SpinnerDateModel(
                    Date.from(time.atZone(ZoneId.systemDefault()).toInstant()),
                    null, null,
                    format.equals("yyyy-MM-dd") ? Calendar.DAY_OF_MONTH : Calendar.HOUR_OF_DAY));
            spinner.setEditor(new JSpinner.DateEditor(spinner, format));
            return spinner;
        }

        // Helper method to save changes
        private void saveScheduleChanges(JDialog dialog, Schedule schedule,
                                         JComboBox<String> hallBox,
                                         JSpinner dateSpinner,
                                         JSpinner timeSpinner,
                                         JSpinner priceSpinner) {
            try {
                // Combine date and time
                LocalDateTime newTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(((Date)dateSpinner.getValue()).getTime()),
                                ZoneId.systemDefault())
                        .withHour(((Date)timeSpinner.getValue()).getHours())
                        .withMinute(((Date)timeSpinner.getValue()).getMinutes());

                // Check for conflicts
                if (hasScheduleConflict((String)hallBox.getSelectedItem(), newTime, schedule.getId())) {
                    showMessage("该放映厅在选定时间已有其他场次", "冲突", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update schedule
                schedule.setHall((String)hallBox.getSelectedItem());
                schedule.setShowTime(newTime);
                schedule.setPrice((Double)priceSpinner.getValue());

                refreshTable();
                dialog.dispose();
                showMessage("场次修改成功!", "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                showMessage("错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Simple message display
        private void showMessage(String message, String title, int messageType) {
            JOptionPane.showMessageDialog(this, message, title, messageType);
        }
        private boolean hasTicketsSold(Schedule schedule) {
            return system.getTickets().stream()
                    .anyMatch(t -> t.getScheduleId().equals(schedule.getId()));
        }

        private void deleteSelectedSchedule() {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的场次",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String scheduleId = (String)scheduleTable.getValueAt(selectedRow, 0);
            Schedule schedule = findScheduleById(scheduleId);

            if (hasTicketsSold(schedule)) {
                JOptionPane.showMessageDialog(this, "该场次已有售票，不能删除",
                        "操作受限", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除场次 '" + schedule.getMovieTitle() + "' 吗?",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                schedules.remove(schedule);
                refreshTable();

                system.logOperation(currentUser, "排片管理",
                        "删除场次: " + schedule.getMovieTitle());
            }
        }

        private void autoSchedule() {
            // Simple auto-scheduling algorithm:
            // 1. Get all movies not yet scheduled today
            // 2. Sort by rating (highest first)
            // 3. Assign best time slots to highest rated movies

            LocalDate today = LocalDate.now();
            List<Movie> unscheduledMovies = system.getMovies().stream()
                    .filter(movie -> schedules.stream()
                            .noneMatch(s -> s.getMovieTitle().equals(movie.getTitle()) &&
                                    s.getShowTime().toLocalDate().equals(today)))
                    .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                    .toList();

            if (unscheduledMovies.isEmpty()) {
                JOptionPane.showMessageDialog(this, "所有影片今天都已排片",
                        "自动排片", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Assign prime time slots (evening)
            String[] halls = {"1号厅", "2号厅", "VIP厅"};
            int hallIndex = 0;
            int added = 0;

            for (Movie movie : unscheduledMovies) {
                if (added >= 3) break; // Max 3 auto schedules

                LocalDateTime showTime = LocalDateTime.of(
                        today, LocalTime.of(19 + added, 0)); // 19:00, 20:00, etc.

                double price = 40 + (movie.getRating() - 6) * 10; // Price based on rating

                Schedule schedule = new Schedule(
                        "SC" + (schedules.size() + 1000),
                        movie.getTitle(),
                        halls[hallIndex],
                        showTime,
                        price
                );

                if (!hasScheduleConflict(schedule.getHall(), schedule.getShowTime(), schedule.getId())) {
                    schedules.add(schedule);
                    added++;
                    hallIndex = (hallIndex + 1) % halls.length;
                }
            }

            refreshTable();

            JOptionPane.showMessageDialog(this,
                    "自动排片完成，添加了 " + added + " 个场次",
                    "自动排片",
                    JOptionPane.INFORMATION_MESSAGE);

            system.logOperation(currentUser, "排片管理", "执行自动排片");
        }


        private Schedule findScheduleById(String id) {
            return schedules.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        public void refreshScheduleData() {
            loadScheduleData();
        }

        public void setCurrentUser(User currentUser) {
            this.currentUser = currentUser;  // Remove the recursive call
        }

    }





}