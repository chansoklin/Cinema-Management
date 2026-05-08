package panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.*;
import service.*;
import exception.DatabaseException;
import ui.SeatSelectionDialog;

public class ScheduleManagementPanel extends JPanel {
    private ScheduleService scheduleService;
    private MovieService movieService;
    private BookingService bookingService;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> movieFilter;
    private JComboBox<String> statusFilter;
    private JTextField searchField;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton viewSeatsBtn;
    private JLabel statusLabel;

    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color INFO_COLOR = new Color(52, 152, 219);
    private final Color BG_WHITE = Color.WHITE;
    private final Color BG_GRAY = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(220, 220, 220);

    public ScheduleManagementPanel() {
        this.scheduleService = new ScheduleService();
        this.movieService = new MovieService();
        this.bookingService = new BookingService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Schedule Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BG_WHITE);

        filterPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterSchedules(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterSchedules(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterSchedules(); }
        });
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Movie:"));
        movieFilter = new JComboBox<>();
        movieFilter.addItem("All Movies");
        movieFilter.addActionListener(e -> filterSchedules());
        filterPanel.add(movieFilter);

        filterPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Upcoming", "Now Showing", "Completed"});
        statusFilter.addActionListener(e -> filterSchedules());
        filterPanel.add(statusFilter);

        JButton refreshBtn = createButton("🔄 Refresh", INFO_COLOR);
        refreshBtn.addActionListener(e -> loadData());
        filterPanel.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        String[] columns = {"ID", "Movie", "Screen", "Start Time", "End Time", "Price", "Status", "Booked", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scheduleTable.setRowHeight(40);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        scheduleTable.getTableHeader().setBackground(new Color(52, 73, 94));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);

        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        sorter = new TableRowSorter<>(tableModel);
        scheduleTable.setRowSorter(sorter);

        scheduleTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_WHITE : BG_GRAY);
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        scheduleTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = createButton("➕ Add Schedule", SUCCESS_COLOR);
        addBtn.addActionListener(e -> showAddScheduleDialog());

        editBtn = createButton("✏️ Edit Schedule", WARNING_COLOR);
        editBtn.addActionListener(e -> showEditScheduleDialog());

        deleteBtn = createButton("🗑️ Delete Schedule", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteSchedule());

        viewSeatsBtn = createButton("💺 View Seat Map", new Color(155, 89, 182));
        viewSeatsBtn.addActionListener(e -> viewSeatMap());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(viewSeatsBtn);

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = scheduleTable.getSelectedRow() >= 0;
            editBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
            viewSeatsBtn.setEnabled(hasSelection);
        });
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewSeatsBtn.setEnabled(false);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("✅ Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(Color.GRAY);
        panel.add(statusLabel);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);

                List<Movie> movies = movieService.getAllMovies();
                movieFilter.removeAllItems();
                movieFilter.addItem("All Movies");
                for (Movie m : movies) {
                    if (m.isActive()) {
                        movieFilter.addItem(m.getTitle());
                    }
                }

                List<Schedule> schedules = scheduleService.getAllSchedules();
                Timestamp now = new Timestamp(System.currentTimeMillis());

                for (Schedule s : schedules) {
                    String status;
                    if (s.getStartTime().after(now)) {
                        status = "🟢 Upcoming";
                    } else if (s.getStartTime().before(now) && s.getEndTime().after(now)) {
                        status = "🔴 Now Showing";
                    } else {
                        status = "⚫ Completed";
                    }

                    int bookedSeats = 0;
                    int capacity = 150;
                    try {
                        bookedSeats = bookingService.getBookedSeats(s.getId()).size();
                    } catch (DatabaseException e) {
                        bookedSeats = 0;
                    }
                    int availableSeats = capacity - bookedSeats;

                    tableModel.addRow(new Object[]{
                            s.getId(),
                            s.getMovie() != null ? s.getMovie().getTitle() : "N/A",
                            s.getScreenName() != null ? s.getScreenName() : "Screen " + s.getScreenId(),
                            formatDateTime(s.getStartTime()),
                            formatDateTime(s.getEndTime()),
                            "$" + s.getBasePrice(),
                            status,
                            bookedSeats,
                            availableSeats
                    });
                }

                statusLabel.setText("✅ Loaded " + schedules.size() + " schedules");

            } catch (DatabaseException e) {
                statusLabel.setText("❌ Error: " + e.getMessage());
                loadDemoData();
            }
        });
    }

    private void loadDemoData() {
        Object[][] data = {
                {1, "Inception", "Screen 1", "2024-01-15 10:00", "2024-01-15 12:28", "$12.00", "🟢 Upcoming", 45, 105},
                {2, "The Dark Knight", "Screen 2", "2024-01-15 13:00", "2024-01-15 15:32", "$12.00", "🟢 Upcoming", 78, 72},
                {3, "Interstellar", "Screen 1", "2024-01-15 16:00", "2024-01-15 18:49", "$14.00", "🟢 Upcoming", 112, 38},
                {4, "John Wick", "Screen 2", "2024-01-15 19:00", "2024-01-15 20:41", "$15.00", "🟢 Upcoming", 95, 55},
                {5, "The Matrix", "Screen 1", "2024-01-15 21:00", "2024-01-15 22:56", "$12.00", "🟢 Upcoming", 134, 16}
        };
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
        statusLabel.setText("📊 Demo Mode - " + tableModel.getRowCount() + " schedules");
    }

    private void filterSchedules() {
        String searchText = searchField.getText().toLowerCase();
        String selectedMovie = (String) movieFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> searchFilter = null;
        RowFilter<DefaultTableModel, Object> movieRowFilter = null;
        RowFilter<DefaultTableModel, Object> statusRowFilter = null;

        if (!searchText.isEmpty()) {
            searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1);
        }

        if (selectedMovie != null && !selectedMovie.equals("All Movies")) {
            movieRowFilter = RowFilter.regexFilter(selectedMovie, 1);
        }

        if (selectedStatus != null && !selectedStatus.equals("All")) {
            String keyword = "";
            switch (selectedStatus) {
                case "Upcoming": keyword = "🟢 Upcoming"; break;
                case "Now Showing": keyword = "🔴 Now Showing"; break;
                case "Completed": keyword = "⚫ Completed"; break;
            }
            if (!keyword.isEmpty()) {
                statusRowFilter = RowFilter.regexFilter(keyword, 6);
            }
        }

        if (searchFilter != null && movieRowFilter != null && statusRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(searchFilter, movieRowFilter, statusRowFilter)));
        } else if (searchFilter != null && movieRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(searchFilter, movieRowFilter)));
        } else if (searchFilter != null && statusRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(searchFilter, statusRowFilter)));
        } else if (movieRowFilter != null && statusRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(movieRowFilter, statusRowFilter)));
        } else if (searchFilter != null) {
            sorter.setRowFilter(searchFilter);
        } else if (movieRowFilter != null) {
            sorter.setRowFilter(movieRowFilter);
        } else if (statusRowFilter != null) {
            sorter.setRowFilter(statusRowFilter);
        } else {
            sorter.setRowFilter(null);
        }

        statusLabel.setText("📊 Showing " + scheduleTable.getRowCount() + " schedules");
    }

    private void viewSeatMap() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to view seat map!");
            return;
        }

        try {
            int scheduleId = (int) tableModel.getValueAt(row, 0);
            Schedule selectedSchedule = scheduleService.getScheduleById(scheduleId);

            // Use the existing SeatSelectionDialog
            SeatSelectionDialog seatDialog = new SeatSelectionDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    selectedSchedule
            );
            seatDialog.setVisible(true);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading schedule: " + e.getMessage());
        }
    }

    private void showAddScheduleDialog() {
        JOptionPane.showMessageDialog(this,
                "➕ Add Schedule\n\nSelect movie, screen, date, time, and price",
                "Add Schedule",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditScheduleDialog() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) return;
        JOptionPane.showMessageDialog(this,
                "✏️ Edit Schedule\n\nSchedule ID: " + tableModel.getValueAt(row, 0) +
                        "\nMovie: " + tableModel.getValueAt(row, 1) +
                        "\nScreen: " + tableModel.getValueAt(row, 2) +
                        "\nTime: " + tableModel.getValueAt(row, 3),
                "Edit Schedule",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete schedule for '" + tableModel.getValueAt(row, 1) + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                if (scheduleService.deleteSchedule(id)) {
                    JOptionPane.showMessageDialog(this, "✅ Schedule deleted!");
                    loadData();
                }
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private String formatDateTime(Timestamp ts) {
        if (ts == null) return "N/A";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";

            if (!isSelected) {
                if (status.contains("🟢")) {
                    setForeground(SUCCESS_COLOR);
                } else if (status.contains("🔴")) {
                    setForeground(DANGER_COLOR);
                } else if (status.contains("⚫")) {
                    setForeground(Color.GRAY);
                }
                setFont(new Font("Segoe UI", Font.BOLD, 11));
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    public void refresh() {
        loadData();
    }
}