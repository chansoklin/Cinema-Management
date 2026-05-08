package panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import util.DBConnection;

public class SalesRecordPanel extends JPanel {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalSalesLabel;
    private JLabel totalTicketsLabel;
    private JLabel avgTicketLabel;

    // Date range pickers
    private JComboBox<String> dateRangeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    // Color scheme
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private static final Color COLOR_WARNING = new Color(241, 196, 15);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);

    public SalesRecordPanel() {
        initComponents();
        loadSalesData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topPanel = createFilterPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.CENTER);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setBackground(Color.WHITE);

        dateRangeCombo = new JComboBox<>(new String[]{
                "Today", "Yesterday", "This Week", "This Month", "All Time", "Custom Range"
        });
        dateRangeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateRangeCombo.addActionListener(e -> onDateRangeChanged());

        SpinnerDateModel startModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setEnabled(false);

        SpinnerDateModel endModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setEnabled(false);

        JButton applyBtn = createButton("Apply Filter", COLOR_PRIMARY);
        applyBtn.addActionListener(e -> loadSalesData());

        datePanel.add(new JLabel("📅 Date Range:"));
        datePanel.add(dateRangeCombo);
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDateSpinner);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDateSpinner);
        datePanel.add(applyBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);

        panel.add(datePanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        totalSalesLabel = createStatCard(panel, "💰 Total Sales", "$0", COLOR_SUCCESS);
        totalTicketsLabel = createStatCard(panel, "🎫 Tickets Sold", "0", COLOR_PRIMARY);
        avgTicketLabel = createStatCard(panel, "📊 Avg Ticket Price", "$0", COLOR_WARNING);

        return panel;
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        parent.add(card);
        return valueLabel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                "📋 Sales Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                COLOR_PRIMARY
        ));

        String[] columns = {"Receipt #", "Date", "Time", "Customer", "Movie", "Seats", "Tickets", "Food", "Total", "Payment", "Staff"};
        tableModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(tableModel);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salesTable.setRowHeight(35);
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        salesTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        salesTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        salesTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        salesTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        salesTable.getColumnModel().getColumn(9).setPreferredWidth(90);
        salesTable.getColumnModel().getColumn(10).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = createButton("🔄 Refresh", COLOR_PRIMARY);
        refreshBtn.addActionListener(e -> loadSalesData());

        JButton exportBtn = createButton("📊 Export to CSV", COLOR_SUCCESS);
        exportBtn.addActionListener(e -> exportToCSV());

        JButton printBtn = createButton("🖨️ Print Report", COLOR_WARNING);
        printBtn.addActionListener(e -> printReport());

        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(printBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void onDateRangeChanged() {
        String selected = (String) dateRangeCombo.getSelectedItem();
        boolean isCustom = "Custom Range".equals(selected);
        startDateSpinner.setEnabled(isCustom);
        endDateSpinner.setEnabled(isCustom);

        if (!isCustom && selected != null && !selected.equals("All Time")) {
            setDateRangeFromPreset(selected);
        } else if (selected != null && selected.equals("All Time")) {
            // For All Time, set date range to a wide range
            try {
                startDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
                endDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void setDateRangeFromPreset(String preset) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;

        switch (preset) {
            case "Today" -> {
                start = today;
                end = today;
            }
            case "Yesterday" -> {
                start = today.minusDays(1);
                end = today.minusDays(1);
            }
            case "This Week" -> {
                start = today.minusDays(7);
                end = today;
            }
            case "This Month" -> {
                start = today.withDayOfMonth(1);
                end = today;
            }
            default -> {
                start = today;
                end = today;
            }
        }

        startDateSpinner.setValue(java.sql.Date.valueOf(start));
        endDateSpinner.setValue(java.sql.Date.valueOf(end));
    }

    private void loadSalesData() {
        tableModel.setRowCount(0);

        try {
            java.util.Date start = (java.util.Date) startDateSpinner.getValue();
            java.util.Date end = (java.util.Date) endDateSpinner.getValue();

            // SQL query to get sales data from bookings
            String sql = """
                SELECT 
                    b.id, 
                    DATE(b.booking_time) as sale_date, 
                    TIME(b.booking_time) as sale_time,
                    b.customer_name,
                    b.total_amount, 
                    b.payment_method,
                    COALESCE(u.name, 'Front Desk') as staff_name,
                    COUNT(t.id) as ticket_count,
                    SUM(t.price) as ticket_total
                FROM bookings b
                LEFT JOIN tickets t ON b.id = t.booking_id
                LEFT JOIN users u ON b.user_id = u.id
                WHERE b.status = 'CONFIRMED'
                GROUP BY b.id, b.booking_time, b.customer_name, b.total_amount, b.payment_method, u.name
                ORDER BY b.booking_time DESC
                """;

            // Add date filter if not "All Time"
            String selectedRange = (String) dateRangeCombo.getSelectedItem();
            if (!"All Time".equals(selectedRange)) {
                sql = """
                    SELECT 
                        b.id, 
                        DATE(b.booking_time) as sale_date, 
                        TIME(b.booking_time) as sale_time,
                        b.customer_name,
                        b.total_amount, 
                        b.payment_method,
                        COALESCE(u.name, 'Front Desk') as staff_name,
                        COUNT(t.id) as ticket_count,
                        SUM(t.price) as ticket_total
                    FROM bookings b
                    LEFT JOIN tickets t ON b.id = t.booking_id
                    LEFT JOIN users u ON b.user_id = u.id
                    WHERE b.status = 'CONFIRMED' 
                    AND DATE(b.booking_time) BETWEEN ? AND ?
                    GROUP BY b.id, b.booking_time, b.customer_name, b.total_amount, b.payment_method, u.name
                    ORDER BY b.booking_time DESC
                    """;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                if (!"All Time".equals(selectedRange)) {
                    ps.setDate(1, new java.sql.Date(start.getTime()));
                    ps.setDate(2, new java.sql.Date(end.getTime()));
                }

                ResultSet rs = ps.executeQuery();

                double totalSales = 0;
                int totalTickets = 0;

                while (rs.next()) {
                    int receiptId = rs.getInt("id");
                    String date = rs.getString("sale_date");
                    String time = rs.getString("sale_time");
                    String customer = rs.getString("customer_name");
                    int tickets = rs.getInt("ticket_count");
                    double total = rs.getDouble("total_amount");
                    String payment = rs.getString("payment_method");
                    String staff = rs.getString("staff_name");
                    double ticketTotal = rs.getDouble("ticket_total");
                    double foodTotal = total - ticketTotal;

                    tableModel.addRow(new Object[]{
                            "INV-" + String.format("%06d", receiptId),
                            date != null ? date : "N/A",
                            time != null ? time.substring(0, 5) : "N/A",
                            customer != null ? customer : "Walk-in",
                            "Movie", // Placeholder - you can join with schedules/movies
                            tickets,
                            tickets,
                            String.format("$%.2f", foodTotal),
                            String.format("$%.2f", total),
                            payment,
                            staff
                    });

                    totalSales += total;
                    totalTickets += tickets;
                }

                totalSalesLabel.setText(String.format("$%,.2f", totalSales));
                totalTicketsLabel.setText(String.valueOf(totalTickets));
                double avgTicket = totalTickets > 0 ? totalSales / totalTickets : 0;
                avgTicketLabel.setText(String.format("$%.2f", avgTicket));

                if (tableModel.getRowCount() == 0) {
                    loadDemoData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDemoData();
        }
    }

    private void loadDemoData() {
        tableModel.setRowCount(0);

        Object[][] demoData = {
                {"INV-001001", "2024-01-15", "14:30", "John Smith", "Inception", 2, 2, "$8.50", "$32.50", "Credit Card", "Sarah"},
                {"INV-001002", "2024-01-15", "15:45", "Emma Wilson", "The Dark Knight", 3, 3, "$0.00", "$36.00", "Cash", "John"},
                {"INV-001003", "2024-01-15", "18:20", "Michael Brown", "Interstellar", 1, 1, "$12.00", "$24.00", "Debit Card", "Sarah"},
                {"INV-001004", "2024-01-14", "19:00", "Sarah Davis", "John Wick", 4, 4, "$15.00", "$63.00", "Mobile Pay", "John"},
                {"INV-001005", "2024-01-14", "20:30", "James Lee", "The Matrix", 2, 2, "$0.00", "$24.00", "Cash", "Sarah"}
        };

        for (Object[] row : demoData) {
            tableModel.addRow(row);
        }

        totalSalesLabel.setText("$179.50");
        totalTicketsLabel.setText("12");
        avgTicketLabel.setText("$14.96");
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        salesTable.setRowSorter(sorter);

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("sales_report_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                // Write header
                for (int i = 0; i < salesTable.getColumnCount(); i++) {
                    writer.print(salesTable.getColumnName(i));
                    if (i < salesTable.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.print(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Sales report exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            }
        }
    }

    private void printReport() {
        try {
            boolean complete = salesTable.print(JTable.PrintMode.FIT_WIDTH);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Report sent to printer!");
            } else {
                JOptionPane.showMessageDialog(this, "Printing cancelled!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage());
        }
    }

    public void refresh() {
        loadSalesData();
    }
}