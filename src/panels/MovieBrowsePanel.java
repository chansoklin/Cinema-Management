package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.*;
import service.*;
import ui.BookingWizardDialog;
import service.MoviePosterService;
import exception.DatabaseException;

public class MovieBrowsePanel extends JPanel {
    private User currentUser;
    private MovieService movieService;
    private JPanel mainPanel;
    private JPanel nowShowingPanel;
    private JPanel upcomingPanel;
    private JTextField searchField;
    private JComboBox<String> genreFilter;
    private JLabel statusLabel;

    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color CARD_BG = new Color(32, 32, 32);
    private final Color TEXT_LIGHT = new Color(255, 255, 255);
    private final Color TEXT_GRAY = new Color(200, 200, 200);
    private final Color ACCENT_COLOR = new Color(229, 9, 20);

    public MovieBrowsePanel(User user) {
        this.currentUser = user;
        this.movieService = new MovieService();
        initComponents();
        loadMovies();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_DARK);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setBackground(BG_DARK);
        mainScrollPane.getViewport().setBackground(BG_DARK);

        add(mainScrollPane, BorderLayout.CENTER);

        // Status label at bottom
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BG_DARK);
        statusLabel = new JLabel("Loading movies...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(TEXT_GRAY);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel logoLabel = new JLabel("🎬 CINEMA STREAM");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(ACCENT_COLOR);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        searchPanel.setBackground(BG_DARK);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(new Color(64, 64, 64));
        searchField.setForeground(TEXT_LIGHT);
        searchField.setCaretColor(TEXT_LIGHT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(64, 64, 64)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterMovies();
            }
        });

        genreFilter = new JComboBox<>(new String[]{
                "All Genres", "Action", "Sci-Fi", "Drama", "Animation", "Comedy", "Horror", "Thriller"
        });
        genreFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        genreFilter.setBackground(new Color(64, 64, 64));
        genreFilter.setForeground(TEXT_LIGHT);
        genreFilter.addActionListener(e -> filterMovies());

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(ACCENT_COLOR);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        refreshBtn.addActionListener(e -> loadMovies());

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(searchField);
        searchPanel.add(genreFilter);
        searchPanel.add(refreshBtn);

        header.add(logoLabel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private void loadMovies() {
        mainPanel.removeAll();
        statusLabel.setText("Loading movies from database...");

        SwingUtilities.invokeLater(() -> {
            try {
                List<Movie> movies = movieService.getAllMovies();
                statusLabel.setText("Found " + movies.size() + " movies");

                // Separate movies into now showing and upcoming
                // For now, show first 8 as now showing, rest as upcoming
                int splitPoint = Math.min(8, movies.size());

                // Now Showing Section
                JPanel nowShowingSection = createSectionHeader("🎬 NOW SHOWING");
                mainPanel.add(nowShowingSection);
                mainPanel.add(Box.createVerticalStrut(10));

                nowShowingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
                nowShowingPanel.setBackground(BG_DARK);

                for (int i = 0; i < splitPoint && i < movies.size(); i++) {
                    Movie movie = movies.get(i);
                    if (movie.isActive()) {
                        nowShowingPanel.add(createMovieCard(movie, true));
                    }
                }

                // Upcoming Section
                JPanel upcomingSection = createSectionHeader("⏰ MORE MOVIES");
                mainPanel.add(upcomingSection);
                mainPanel.add(Box.createVerticalStrut(10));

                upcomingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
                upcomingPanel.setBackground(BG_DARK);

                for (int i = splitPoint; i < movies.size(); i++) {
                    Movie movie = movies.get(i);
                    if (movie.isActive()) {
                        upcomingPanel.add(createMovieCard(movie, true));
                    }
                }

                mainPanel.add(nowShowingPanel);
                mainPanel.add(Box.createVerticalStrut(30));
                mainPanel.add(upcomingPanel);
                mainPanel.add(Box.createVerticalStrut(30));

                if (movies.isEmpty()) {
                    JLabel noMoviesLabel = new JLabel("No movies found in database. Please add movies in Admin panel.");
                    noMoviesLabel.setForeground(TEXT_GRAY);
                    noMoviesLabel.setAlignmentX(CENTER_ALIGNMENT);
                    mainPanel.add(noMoviesLabel);
                }

            } catch (DatabaseException e) {
                statusLabel.setText("Error loading movies: " + e.getMessage());
                addDemoMovies();
            }

            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private void addDemoMovies() {
        statusLabel.setText("Demo mode - Showing sample movies");

        JPanel nowShowingSection = createSectionHeader("🎬 NOW SHOWING");
        mainPanel.add(nowShowingSection);
        mainPanel.add(Box.createVerticalStrut(10));

        nowShowingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        nowShowingPanel.setBackground(BG_DARK);

        String[][] demoMovies = {
                {"Inception", "148", "Sci-Fi", "PG-13"},
                {"The Dark Knight", "152", "Action", "PG-13"},
                {"Interstellar", "169", "Sci-Fi", "PG-13"},
                {"John Wick", "101", "Action", "R"},
                {"The Matrix", "136", "Sci-Fi", "R"}
        };

        for (String[] movieInfo : demoMovies) {
            Movie movie = new Movie();
            movie.setTitle(movieInfo[0]);
            movie.setDuration(Integer.parseInt(movieInfo[1]));
            movie.setGenre(movieInfo[2]);
            movie.setRating(movieInfo[3]);
            movie.setActive(true);
            nowShowingPanel.add(createMovieCard(movie, true));
        }

        mainPanel.add(nowShowingPanel);
        mainPanel.add(Box.createVerticalStrut(30));
    }

    private JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_LIGHT);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMovieCard(Movie movie, boolean isNowShowing) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(220, 380));
        card.setBorder(BorderFactory.createLineBorder(new Color(64, 64, 64), 1));

        // Poster Panel with better loading
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBackground(getPosterColor(movie.getTitle()));
        posterPanel.setPreferredSize(new Dimension(220, 280));

        // Create a custom painted poster
        JPanel posterContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Gradient background
                Color c1 = getPosterColor(movie.getTitle());
                Color c2 = c1.darker();
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Draw film strip
                g2d.setColor(new Color(255, 255, 255, 40));
                for (int i = 0; i < h; i += 40) {
                    g2d.fillRoundRect(0, i, w, 5, 3, 3);
                }

                // Movie title on poster
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                String title = movie.getTitle();
                if (fm.stringWidth(title) > w - 20) {
                    title = title.substring(0, 15) + "...";
                }
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (w - titleWidth) / 2, h - 50);

                // Rating info
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String rating = movie.getRating() + " • " + movie.getDuration() + " min";
                int ratingWidth = fm.stringWidth(rating);
                g2d.drawString(rating, (w - ratingWidth) / 2, h - 30);

                // Genre
                String genre = movie.getGenre();
                int genreWidth = fm.stringWidth(genre);
                g2d.drawString(genre, (w - genreWidth) / 2, h - 15);

                // Movie icon at center
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 50));
                fm = g2d.getFontMetrics();
                String icon = getMovieIcon(movie.getTitle());
                int iconWidth = fm.stringWidth(icon);
                g2d.drawString(icon, (w - iconWidth) / 2, h / 2 - 20);
            }
        };
        posterContent.setPreferredSize(new Dimension(220, 280));
        posterPanel.add(posterContent, BorderLayout.CENTER);

        card.add(posterPanel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel genreLabel = new JLabel(movie.getGenre());
        genreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        genreLabel.setForeground(TEXT_GRAY);
        genreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bookBtn = new JButton("🎟️ BOOK NOW");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.setBackground(ACCENT_COLOR);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> {
            System.out.println("Book button clicked for: " + movie.getTitle());
            openBookingWizard(movie);
        });

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(64, 64, 64), 1));
            }
        });

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(genreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(bookBtn);

        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
    }

    private Color getPosterColor(String title) {
        int hash = Math.abs(title.hashCode());
        return new Color(30 + (hash % 40), 50 + (hash % 60), 80 + (hash % 70));
    }

    private String getMovieIcon(String title) {
        switch(title) {
            case "Inception": return "🌀";
            case "The Dark Knight": return "🦇";
            case "Interstellar": return "🚀";
            case "John Wick": return "🔫";
            case "The Matrix": return "💊";
            default: return "🎬";
        }
    }

    private void filterMovies() {
        String searchText = searchField.getText().toLowerCase();
        String selectedGenre = (String) genreFilter.getSelectedItem();

        Component[] nowShowing = nowShowingPanel.getComponents();
        Component[] upcoming = upcomingPanel.getComponents();

        for (Component comp : nowShowing) {
            comp.setVisible(true);
        }
        for (Component comp : upcoming) {
            comp.setVisible(true);
        }
    }

    private void openBookingWizard(Movie movie) {
        try {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            BookingWizardDialog dialog = new BookingWizardDialog(parent, movie, currentUser);
            dialog.setVisible(true);

            if (dialog.isBookingConfirmed()) {
                JOptionPane.showMessageDialog(this,
                        "✅ Booking confirmed!\n\nYour e-ticket has been sent to your email.\n" +
                                "You can view your bookings in 'My Bookings' tab.",
                        "Booking Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error opening booking wizard: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        loadMovies();
    }
}