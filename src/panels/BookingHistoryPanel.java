package panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import util.DBConnection;
import service.BookingService;
import service.ScheduleService;
import service.MovieService;
import exception.DatabaseException;

public class BookingHistoryPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JTextField searchField;
    private JLabel summaryLabel;
    private Timer refreshTimer;

    public BookingHistoryPanel() {
        initComponents();
        loadBookings();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("📋 Booking History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterBookings();
            }
        });
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Confirmed", "Pending", "Cancelled", "Completed"});
        statusFilter.addActionListener(e -> filterBookings());
        filterPanel.add(statusFilter);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(41, 128, 185));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadBookings());
        filterPanel.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Booking ID", "Movie", "Date", "Customer", "Tickets", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsTable.setRowHeight(35);
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Status cell renderer
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        summaryLabel = new JLabel("Loading bookings...");
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        summaryLabel.setForeground(Color.GRAY);
        panel.add(summaryLabel);

        return panel;
    }

    private void loadBookings() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);

            try {
                Connection conn = DBConnection.getConnection();

                String sql = "SELECT b.id, m.title, DATE(b.booking_time) as book_date, " +
                        "b.customer_name, b.total_amount, b.status " +
                        "FROM bookings b " +
                        "JOIN schedules s ON b.schedule_id = s.id " +
                        "JOIN movies m ON s.movie_id = m.id " +
                        "ORDER BY b.booking_time DESC";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                int totalBookings = 0;
                double totalRevenue = 0;

                while (rs.next()) {
                    totalBookings++;
                    totalRevenue += rs.getDouble("total_amount");

                    tableModel.addRow(new Object[]{
                            "INV-" + String.format("%06d", rs.getInt("id")),
                            rs.getString("title"),  // This should now show the correct movie
                            rs.getString("book_date"),
                            rs.getString("customer_name"),
                            "2 tickets",
                            "$" + String.format("%.2f", rs.getDouble("total_amount")),
                            formatStatus(rs.getString("status"))
                    });
                }

                summaryLabel.setText(String.format("📊 Total Bookings: %d | Total Revenue: $%.2f",
                        totalBookings, totalRevenue));

                rs.close();
                stmt.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
                loadDemoBookings();
            }
        });
    }
    // FIXED: Renamed method to match the call
    private void loadDemoBookings() {
        tableModel.setRowCount(0);

        // Get all movies from database or use defaults
        String[] movies = {"Inception", "The Dark Knight", "Interstellar", "John Wick", "The Matrix"};

        Object[][] bookings = {
                {"INV-001001", movies[0], "2024-01-15", "John Doe", "2 tickets", "$24.00", "✅ Confirmed", "View"},
                {"INV-001002", movies[1], "2024-01-14", "Jane Smith", "3 tickets", "$36.00", "✅ Confirmed", "View"},
                {"INV-001003", movies[2], "2024-01-13", "Mike Johnson", "1 ticket", "$12.00", "⏳ Pending", "View"},
                {"INV-001004", movies[3], "2024-01-12", "Sarah Williams", "4 tickets", "$48.00", "✅ Confirmed", "View"},
                {"INV-001005", movies[4], "2024-01-11", "Tom Brown", "2 tickets", "$24.00", "❌ Cancelled", "View"}
        };

        for (Object[] booking : bookings) {
            tableModel.addRow(booking);
        }

        summaryLabel.setText("📊 Demo Mode - 5 Bookings | Total Revenue: $144.00");
    }

    private void filterBookings() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> searchFilter = null;
        RowFilter<DefaultTableModel, Object> statusRowFilter = null;

        if (!searchText.isEmpty()) {
            searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 3);
        }

        if (selectedStatus != null && !selectedStatus.equals("All")) {
            String statusKeyword = "";
            switch (selectedStatus) {
                case "Confirmed": statusKeyword = "✅ Confirmed"; break;
                case "Pending": statusKeyword = "⏳ Pending"; break;
                case "Cancelled": statusKeyword = "❌ Cancelled"; break;
                case "Completed": statusKeyword = "🎬 Completed"; break;
            }
            if (!statusKeyword.isEmpty()) {
                statusRowFilter = RowFilter.regexFilter(statusKeyword, 6);
            }
        }

        if (searchFilter != null && statusRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(searchFilter, statusRowFilter)));
        } else if (searchFilter != null) {
            sorter.setRowFilter(searchFilter);
        } else if (statusRowFilter != null) {
            sorter.setRowFilter(statusRowFilter);
        } else {
            sorter.setRowFilter(null);
        }

        summaryLabel.setText("📊 Showing " + bookingsTable.getRowCount() + " bookings");
    }

    private String formatStatus(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "CONFIRMED": return "✅ Confirmed";
            case "PENDING": return "⏳ Pending";
            case "CANCELLED": return "❌ Cancelled";
            case "COMPLETED": return "🎬 Completed";
            default: return status;
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(30000, e -> loadBookings());
        refreshTimer.start();
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";

            if (!isSelected) {
                if (status.contains("✅")) {
                    setForeground(new Color(46, 204, 113));
                } else if (status.contains("⏳")) {
                    setForeground(new Color(241, 196, 15));
                } else if (status.contains("❌")) {
                    setForeground(new Color(231, 76, 60));
                } else if (status.contains("🎬")) {
                    setForeground(new Color(52, 152, 219));
                }
                setFont(new Font("Segoe UI", Font.BOLD, 11));
            }
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return c;
        }
    }

    public void refresh() {
        loadBookings();
    }
}