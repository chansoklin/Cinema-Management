package panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import model.*;
import service.*;
import exception.DatabaseException;

public class MovieManagementPanel extends JPanel {
    private MovieService movieService;
    private User currentUser;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> genreFilter;
    private JComboBox<String> statusFilter;

    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_WHITE = Color.WHITE;
    private final Color BG_GRAY = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(220, 220, 220);

    public MovieManagementPanel(User user) {
        this.currentUser = user;
        this.movieService = new MovieService();
        initComponents();
        loadMovies();
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
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Movie Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BG_WHITE);

        filterPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterMovies(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterMovies(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterMovies(); }
        });
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Genre:"));
        genreFilter = new JComboBox<>(new String[]{"All", "Action", "Comedy", "Drama", "Sci-Fi", "Horror"});
        genreFilter.addActionListener(e -> filterMovies());
        filterPanel.add(genreFilter);

        JButton refreshBtn = createButton("🔄 Refresh", new Color(41, 128, 185));
        refreshBtn.addActionListener(e -> loadMovies());
        filterPanel.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        String[] columns = {"ID", "Title", "Duration", "Genre", "Rating", "Status"};
        tableModel = new DefaultTableModel(columns, 0);

        movieTable = new JTable(tableModel);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        movieTable.setRowHeight(40);
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        movieTable.getTableHeader().setBackground(new Color(52, 73, 94));
        movieTable.getTableHeader().setForeground(Color.WHITE);

        sorter = new TableRowSorter<>(tableModel);
        movieTable.setRowSorter(sorter);

        movieTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(movieTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = createButton("➕ Add Movie", SUCCESS_COLOR);
        addBtn.addActionListener(e -> addMovie());

        JButton editBtn = createButton("✏️ Edit Movie", WARNING_COLOR);
        editBtn.addActionListener(e -> editMovie());

        JButton deleteBtn = createButton("🗑️ Delete Movie", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteMovie());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        movieTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = movieTable.getSelectedRow() >= 0;
            editBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
        });
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

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

    private void loadMovies() {
        try {
            tableModel.setRowCount(0);
            List<Movie> movies = movieService.getAllMovies();
            for (Movie movie : movies) {
                tableModel.addRow(new Object[]{
                        movie.getId(), movie.getTitle(), movie.getDuration() + " min",
                        movie.getGenre(), movie.getRating(), movie.isActive() ? "Active" : "Inactive"
                });
            }
        } catch (DatabaseException e) {
            loadDemoMovies();
        }
    }

    private void loadDemoMovies() {
        Object[][] movies = {
                {1, "Inception", "148 min", "Sci-Fi", "PG-13", "Active"},
                {2, "The Dark Knight", "152 min", "Action", "PG-13", "Active"},
                {3, "Interstellar", "169 min", "Sci-Fi", "PG-13", "Active"}
        };
        for (Object[] movie : movies) tableModel.addRow(movie);
    }

    private void filterMovies() {
        String text = searchField.getText();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
    }

    private void addMovie() {
        JTextField title = new JTextField();
        JTextField duration = new JTextField();
        JTextField genre = new JTextField();
        JTextField rating = new JTextField();
        JTextArea desc = new JTextArea(3, 20);

        Object[] fields = {"Title:", title, "Duration (min):", duration, "Genre:", genre, "Rating:", rating, "Description:", new JScrollPane(desc)};

        if (JOptionPane.showConfirmDialog(this, fields, "Add Movie", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Movie movie = new Movie();
                movie.setTitle(title.getText());
                movie.setDuration(Integer.parseInt(duration.getText()));
                movie.setGenre(genre.getText());
                movie.setRating(rating.getText());
                movie.setDescription(desc.getText());
                movie.setActive(true);
                if (movieService.createMovie(movie)) {
                    JOptionPane.showMessageDialog(this, "✅ Movie added!");
                    loadMovies();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void editMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Movie movie = movieService.getMovieById(id);

            JTextField title = new JTextField(movie.getTitle());
            JTextField duration = new JTextField(String.valueOf(movie.getDuration()));
            JTextField genre = new JTextField(movie.getGenre());
            JTextField rating = new JTextField(movie.getRating());
            JTextArea desc = new JTextArea(movie.getDescription(), 3, 20);
            JCheckBox active = new JCheckBox("Active", movie.isActive());

            Object[] fields = {"Title:", title, "Duration:", duration, "Genre:", genre, "Rating:", rating, "Description:", new JScrollPane(desc), "Status:", active};

            if (JOptionPane.showConfirmDialog(this, fields, "Edit Movie", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                movie.setTitle(title.getText());
                movie.setDuration(Integer.parseInt(duration.getText()));
                movie.setGenre(genre.getText());
                movie.setRating(rating.getText());
                movie.setDescription(desc.getText());
                movie.setActive(active.isSelected());
                if (movieService.updateMovie(movie)) {
                    JOptionPane.showMessageDialog(this, "✅ Movie updated!");
                    loadMovies();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;

        String title = (String) tableModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete '" + title + "'?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                if (movieService.deleteMovie(id)) {
                    JOptionPane.showMessageDialog(this, "✅ Movie deleted!");
                    loadMovies();
                }
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    public void refresh() {
        loadMovies();
    }
}