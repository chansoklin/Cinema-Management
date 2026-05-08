package panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import service.BookingService;
import exception.DatabaseException;

public class TopMoviesPanel extends JPanel {
    private JTable topMoviesTable;
    private DefaultTableModel topMoviesModel;
    private BookingService bookingService;

    public TopMoviesPanel() {
        this.bookingService = new BookingService();
        initComponents();
        loadTopMovies();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "⭐ Top 5 Movies Today",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(44, 62, 80)
        ));

        String[] columns = {"Rank", "Movie", "Tickets", "Revenue"};
        topMoviesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        topMoviesTable = new JTable(topMoviesModel);
        topMoviesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topMoviesTable.setRowHeight(35);
        topMoviesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Custom cell renderer for rank
        topMoviesTable.getColumnModel().getColumn(0).setCellRenderer(new RankCellRenderer());

        JScrollPane scrollPane = new JScrollPane(topMoviesTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadTopMovies() {
        topMoviesModel.setRowCount(0);

        try {
            java.util.List<Object[]> topMovies = bookingService.getTopMovies(5);
            int rank = 1;
            for (Object[] movie : topMovies) {
                topMoviesModel.addRow(new Object[]{
                        rank++,
                        movie[0],
                        movie[1],
                        "$" + movie[2]
                });
            }
        } catch (DatabaseException e) {
            // Demo data
            Object[][] movies = {
                    {1, "Inception", 156, "1,872"},
                    {2, "The Dark Knight", 142, "1,704"},
                    {3, "Interstellar", 128, "1,536"},
                    {4, "John Wick", 98, "1,176"},
                    {5, "The Matrix", 87, "1,044"}
            };
            for (Object[] movie : movies) {
                topMoviesModel.addRow(movie);
            }
        }
    }

    class RankCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            int rank = (int) value;
            setHorizontalAlignment(CENTER);

            if (!isSelected) {
                if (rank == 1) {
                    setBackground(new Color(255, 215, 0, 50));
                    setForeground(new Color(184, 134, 11));
                    setText("🥇 " + rank);
                } else if (rank == 2) {
                    setBackground(new Color(192, 192, 192, 50));
                    setForeground(new Color(128, 128, 128));
                    setText("🥈 " + rank);
                } else if (rank == 3) {
                    setBackground(new Color(205, 127, 50, 50));
                    setForeground(new Color(160, 82, 45));
                    setText("🥉 " + rank);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    setText("#" + rank);
                }
            }

            return c;
        }
    }

    public void refresh() {
        loadTopMovies();
    }
}