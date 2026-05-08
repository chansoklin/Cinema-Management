package panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import service.BookingService;
import exception.DatabaseException;

public class RecentSalesPanel extends JPanel {
    private JTable recentSalesTable;
    private DefaultTableModel recentSalesModel;
    private BookingService bookingService;

    public RecentSalesPanel() {
        this.bookingService = new BookingService();
        initComponents();
        loadRecentSales();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "🕐 Recent Sales",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(44, 62, 80)
        ));

        String[] columns = {"Time", "Movie", "Tickets", "Amount", "Staff"};
        recentSalesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentSalesTable = new JTable(recentSalesModel);
        recentSalesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recentSalesTable.setRowHeight(32);
        recentSalesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(recentSalesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = createButton("🔄 Refresh", new Color(41, 128, 185));
        refreshBtn.addActionListener(e -> loadRecentSales());
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void loadRecentSales() {
        recentSalesModel.setRowCount(0);

        try {
            java.util.List<Object[]> sales = bookingService.getRecentSales(10);
            for (Object[] sale : sales) {
                recentSalesModel.addRow(sale);
            }
        } catch (DatabaseException e) {
            // Demo data
            Object[][] sales = {
                    {"14:30", "Inception", 2, "$24.00", "John"},
                    {"14:15", "The Dark Knight", 3, "$36.00", "Sarah"},
                    {"14:00", "Interstellar", 1, "$12.00", "Mike"},
                    {"13:45", "John Wick", 4, "$48.00", "Emma"},
                    {"13:30", "The Matrix", 2, "$24.00", "David"},
                    {"13:15", "Inception", 2, "$24.00", "Lisa"},
                    {"13:00", "The Dark Knight", 1, "$12.00", "Tom"}
            };
            for (Object[] sale : sales) {
                recentSalesModel.addRow(sale);
            }
        }
    }

    public void refresh() {
        loadRecentSales();
    }
}