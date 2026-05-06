package panels;

import main.CinemaManagementSystem;
import model.User;
import manager.Movie;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ManagerPanel extends JPanel {
    // Constants
    private static final Color HEADER_BG_COLOR = new Color(44, 62, 80);
    private static final Color MAIN_BG_COLOR = new Color(245, 248, 250);
    private static final Color CARD_BG_COLOR = Color.WHITE;
    private static final Color CARD_HOVER_COLOR = new Color(240, 248, 255);
    private static final Color CARD_BORDER_COLOR = new Color(230, 235, 240);
    private static final Color CARD_HOVER_BORDER_COLOR = new Color(100, 149, 237);

    // Instance variables
    private final CinemaManagementSystem system;
    private User currentUser;
    private JLabel titleLabel;

    public ManagerPanel(CinemaManagementSystem system, User user) {
        this.system = system;
        this.currentUser = user;
        initUI();
    }

    // Public methods
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    public void updateUI() {
        if (titleLabel != null) {
            titleLabel.setText("经理面板 - 当前用户: " +
                    (currentUser != null ? currentUser.getUsername() : "未登录"));
        }
        revalidate();
        repaint();
    }

    // UI Initialization
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(HEADER_BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        titleLabel = new JLabel("经理面板 - 当前用户: " +
                (currentUser != null ? currentUser.getUsername() : "未登录"));
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("影院管理系统 - 经理功能");
        subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 215, 230));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(5, 0, 0, 0));

        panel.add(titleLabel);
        panel.add(subtitle);
        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 25, 25));
        panel.setBorder(new EmptyBorder(35, 35, 35, 35));
        panel.setBackground(MAIN_BG_COLOR);

        String[] menuItems = {
                "密码管理", "影片管理", "排片管理",
                "销售数据", "操作日志", "退出系统"
        };

        String[] icons = {
                "🔑", "🎬", "📅",
                "💰", "📝", "🚪"
        };

        for (int i = 0; i < menuItems.length; i++) {
            panel.add(createCardPanel(menuItems[i], icons[i]));
        }

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(HEADER_BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel statusLabel = new JLabel("影院管理系统 v1.0 | 经理权限");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 200, 220));

        panel.add(statusLabel);
        return panel;
    }

    private JPanel createCardPanel(String title, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER_COLOR),
                new EmptyBorder(25, 15, 25, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Card hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(CARD_HOVER_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(CARD_HOVER_BORDER_COLOR),
                        new EmptyBorder(25, 15, 25, 15)
                ));
            }

            public void mouseExited(MouseEvent evt) {
                card.setBackground(CARD_BG_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(CARD_BORDER_COLOR),
                        new EmptyBorder(25, 15, 25, 15)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuAction(title);
            }
        });

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        iconLabel.setForeground(new Color(70, 130, 180));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(60, 75, 100));

        card.add(iconLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "密码管理":
                system.showPasswordManagementPanel(currentUser);
                system.logOperation(currentUser, "操作", "访问密码管理");
                break;
            case "影片管理":
                system.showMovieManagementPanel(currentUser);
                system.logOperation(currentUser, "操作", "访问影片管理");
                break;
            case "排片管理":
                system.showScheduleManagementPanel(currentUser);
                system.logOperation(currentUser, "操作", "访问排片管理");
                break;
            case "销售数据":
                system.showSalesDataPanel(currentUser);
                system.logOperation(currentUser, "操作", "访问销售数据");
                break;
            case "操作日志":
                system.showOperationLogPanel(currentUser);
                system.logOperation(currentUser, "操作", "查看操作日志");
                break;
            case "退出系统":
                confirmLogout();
                break;
            default:
                JOptionPane.showMessageDialog(system, "未知操作: " + action,
                        "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要退出系统吗?",
                "退出确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            system.logOperation(currentUser, "系统", "用户退出登录");
            system.showLoginPanel();
        }
    }

    // Movie Management Panel Inner Class
    public static class MovieManagementPanel extends JPanel {
        // Constants
        private static final Color DARK_BG_COLOR = new Color(30, 30, 40);
        private static final Color PANEL_BG_COLOR = new Color(40, 40, 55);
        private static final Color TABLE_BG_COLOR = new Color(50, 50, 70);
        private static final Color TABLE_HEADER_BG_COLOR = new Color(50, 50, 70);
        private static final Color TABLE_HEADER_FG_COLOR = new Color(255, 215, 0);

        // Instance variables
        private final CinemaManagementSystem system;
        private final User currentUser;
        private JTable movieTable;
        private DefaultTableModel tableModel;
        private final List<Movie> movieList = new ArrayList<>();

        public MovieManagementPanel(CinemaManagementSystem system, User user) {
            this.system = system;
            this.currentUser = user;
            initUI();
            refreshMovieTable();
        }

        private void initUI() {
            setLayout(new BorderLayout());
            setBackground(DARK_BG_COLOR);
            setBorder(new EmptyBorder(15, 15, 15, 15));

            add(createHeaderPanel(), BorderLayout.NORTH);
            add(createTabbedPane(), BorderLayout.CENTER);
            add(createFooterPanel(), BorderLayout.SOUTH);
        }

        private JPanel createHeaderPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(DARK_BG_COLOR);
            panel.setBorder(new EmptyBorder(0, 0, 20, 0));

            JLabel titleLabel = new JLabel("影片管理");
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
            titleLabel.setForeground(TABLE_HEADER_FG_COLOR);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel subtitle = new JLabel("管理影院影片信息 - 添加、编辑、删除和查询影片");
            subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            subtitle.setForeground(new Color(180, 180, 200));
            subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            subtitle.setBorder(new EmptyBorder(5, 0, 0, 0));

            panel.add(titleLabel);
            panel.add(subtitle);
            return panel;
        }

        private JTabbedPane createTabbedPane() {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(new Color(45, 45, 60));
            tabbedPane.setForeground(Color.WHITE);
            tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));

            tabbedPane.addTab("影片列表", createMovieListPanel());
            tabbedPane.addTab("添加新影片", createAddMoviePanel());
            tabbedPane.addTab("查询影片", createSearchMoviePanel());

            return tabbedPane;
        }

        private JPanel createMovieListPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Toolbar
            panel.add(createToolbarPanel(), BorderLayout.NORTH);

            // Movie table
            String[] columnNames = {"片名", "导演", "主演", "时长(分钟)", "评分", "剧情简介"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            movieTable = createMovieTable();
            JScrollPane scrollPane = createTableScrollPane(movieTable);

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(createStatusPanel(), BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createToolbarPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panel.setBackground(PANEL_BG_COLOR);

            JButton refreshButton = createStyledButton("刷新列表", new Color(70, 130, 180),
                    e -> refreshMovieTable());
            JButton editButton = createStyledButton("编辑影片", new Color(46, 204, 113),
                    e -> editSelectedMovie());
            JButton deleteButton = createStyledButton("删除影片", new Color(231, 76, 60),
                    e -> deleteSelectedMovie());

            panel.add(refreshButton);
            panel.add(editButton);
            panel.add(deleteButton);

            return panel;
        }

        private JTable createMovieTable() {
            JTable table = new JTable(tableModel);
            table.setRowHeight(35);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
            table.getTableHeader().setBackground(TABLE_HEADER_BG_COLOR);
            table.getTableHeader().setForeground(TABLE_HEADER_FG_COLOR);
            table.setBackground(TABLE_BG_COLOR);
            table.setForeground(Color.WHITE);
            table.setGridColor(new Color(70, 70, 90));
            table.setShowGrid(true);

            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
            table.getColumnModel().getColumn(4).setPreferredWidth(60);
            table.getColumnModel().getColumn(5).setPreferredWidth(300);

            return table;
        }

        private JScrollPane createTableScrollPane(JTable table) {
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(new LineBorder(new Color(60, 60, 80), 1));
            scrollPane.getViewport().setBackground(TABLE_BG_COLOR);
            return scrollPane;
        }

        private JPanel createStatusPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(10, 5, 5, 5));

            JLabel statusLabel = new JLabel("共加载 " + movieList.size() + " 部影片");
            statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            statusLabel.setForeground(new Color(180, 180, 200));

            panel.add(statusLabel);
            return panel;
        }

        private JPanel createAddMoviePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            panel.add(createFormPanel(), BorderLayout.CENTER);
            panel.add(createSubmitButtonPanel(), BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createFormPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(60, 60, 80), 1),
                    new EmptyBorder(20, 20, 20, 20)
            ));

            panel.add(createFormField("片名:", new JTextField(), 200));
            panel.add(Box.createVerticalStrut(15));
            panel.add(createFormField("导演:", new JTextField(), 200));
            panel.add(Box.createVerticalStrut(15));
            panel.add(createFormField("主演:", new JTextField(), 200));
            panel.add(Box.createVerticalStrut(15));
            panel.add(createFormField("时长(分钟):", new JTextField(), 200));
            panel.add(Box.createVerticalStrut(15));
            panel.add(createFormField("评分(0-10):", new JTextField(), 200));
            panel.add(Box.createVerticalStrut(15));
            panel.add(createPlotPanel());

            return panel;
        }

        private JPanel createFormField(String label, JComponent field, int width) {
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            panel.setBackground(PANEL_BG_COLOR);

            JLabel jLabel = new JLabel(label);
            jLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            jLabel.setForeground(Color.WHITE);
            jLabel.setPreferredSize(new Dimension(100, 25));

            field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            if (field instanceof JTextField) {
                ((JTextField) field).setColumns(20);
            }
            field.setPreferredSize(new Dimension(width, 30));
            field.setMaximumSize(new Dimension(width, 30));

            panel.add(jLabel, BorderLayout.WEST);
            panel.add(field, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createPlotPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(10, 0, 10, 0));

            JLabel plotLabel = new JLabel("剧情简介:");
            plotLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            plotLabel.setForeground(Color.WHITE);

            JTextArea plotTextArea = new JTextArea(5, 30);
            plotTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            plotTextArea.setLineWrap(true);
            plotTextArea.setWrapStyleWord(true);
            JScrollPane plotScroll = new JScrollPane(plotTextArea);
            plotScroll.setBorder(new LineBorder(new Color(100, 100, 120), 1));

            panel.add(plotLabel, BorderLayout.NORTH);
            panel.add(plotScroll, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createSubmitButtonPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton submitButton = createStyledButton("添加影片", new Color(46, 204, 113), e -> {
                // TODO: Implement add movie logic
            });

            panel.add(submitButton);
            return panel;
        }

        private JPanel createSearchMoviePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            panel.add(createSearchPanel(), BorderLayout.NORTH);
            panel.add(createResultsPanel(), BorderLayout.CENTER);

            return panel;
        }

        private JPanel createSearchPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(new EmptyBorder(0, 0, 20, 0));

            JLabel typeLabel = new JLabel("搜索类型:");
            typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            typeLabel.setForeground(Color.WHITE);

            String[] types = {"片名", "导演", "主演", "组合查询"};
            JComboBox<String> searchTypeComboBox = new JComboBox<>(types);
            searchTypeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            searchTypeComboBox.setBackground(new Color(60, 60, 80));
            searchTypeComboBox.setForeground(Color.WHITE);

            JLabel searchLabel = new JLabel("搜索内容:");
            searchLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            searchLabel.setForeground(Color.WHITE);

            JTextField searchField = new JTextField(25);
            searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

            JButton searchButton = createStyledButton("搜索影片", new Color(70, 130, 180),
                    e -> searchMovies(searchField.getText().trim(),
                            (String) searchTypeComboBox.getSelectedItem()));

            panel.add(typeLabel);
            panel.add(searchTypeComboBox);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(searchLabel);
            panel.add(searchField);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(searchButton);

            return panel;
        }

        private JPanel createResultsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_BG_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            JLabel resultTitle = new JLabel("搜索结果");
            resultTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
            resultTitle.setForeground(TABLE_HEADER_FG_COLOR);
            resultTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

            JPanel cardsPanel = new JPanel();
            cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
            cardsPanel.setBackground(new Color(45, 45, 60));

            // Add example cards
            cardsPanel.add(createMovieCard("流浪地球", "郭帆", "吴京、屈楚萧、李光洁", 125, 8.5,
                    "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园..."));
            cardsPanel.add(Box.createVerticalStrut(15));
            cardsPanel.add(createMovieCard("长津湖", "陈凯歌、徐克、林超贤", "吴京、易烊千玺、段奕宏", 176, 9.0,
                    "电影以抗美援朝战争第二次战役中的长津湖战役为背景..."));

            JScrollPane scrollPane = new JScrollPane(cardsPanel);
            scrollPane.getViewport().setBackground(new Color(45, 45, 60));
            scrollPane.setBorder(null);

            panel.add(resultTitle, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createMovieCard(String title, String director, String actors,
                                       int duration, double rating, String plot) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(TABLE_BG_COLOR);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(70, 70, 90)),
                    new EmptyBorder(10, 15, 15, 15)
            ));

            // Top section
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(TABLE_BG_COLOR);
            topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
            titleLabel.setForeground(TABLE_HEADER_FG_COLOR);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            infoPanel.setBackground(TABLE_BG_COLOR);

            infoPanel.add(createInfoLabel("导演: " + director));
            infoPanel.add(createInfoLabel("主演: " + actors));
            infoPanel.add(createInfoLabel("时长: " + duration + "分钟"));
            infoPanel.add(createRatingLabel("评分: " + rating));

            topPanel.add(titleLabel, BorderLayout.NORTH);
            topPanel.add(infoPanel, BorderLayout.CENTER);

            // Plot section
            JPanel plotPanel = new JPanel(new BorderLayout());
            plotPanel.setBackground(TABLE_BG_COLOR);
            plotPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            plotPanel.add(createPlotTitleLabel(), BorderLayout.NORTH);
            plotPanel.add(createPlotTextArea(plot), BorderLayout.CENTER);

            // Combine components
            card.add(topPanel, BorderLayout.NORTH);
            card.add(plotPanel, BorderLayout.CENTER);

            return card;
        }

        private JLabel createInfoLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            label.setForeground(new Color(180, 180, 200));
            return label;
        }

        private JLabel createRatingLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("微软雅黑", Font.BOLD, 13));
            label.setForeground(TABLE_HEADER_FG_COLOR);
            return label;
        }

        private JLabel createPlotTitleLabel() {
            JLabel label = new JLabel("剧情简介:");
            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            label.setForeground(Color.WHITE);
            return label;
        }

        private JScrollPane createPlotTextArea(String text) {
            JTextArea plotText = new JTextArea(text);
            plotText.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            plotText.setLineWrap(true);
            plotText.setWrapStyleWord(true);
            plotText.setEditable(false);
            plotText.setBackground(TABLE_BG_COLOR);
            plotText.setForeground(new Color(200, 200, 220));
            plotText.setBorder(new EmptyBorder(5, 5, 5, 5));

            return new JScrollPane(plotText);
        }

        private JPanel createFooterPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel.setBackground(DARK_BG_COLOR);
            panel.setBorder(new EmptyBorder(15, 0, 0, 0));

            JButton backButton = createStyledButton("返回主菜单", new Color(70, 130, 180),
                    e -> {
                        system.showManagerPanel(currentUser);
                        system.logOperation(currentUser, "操作", "返回主菜单");
                    });

            panel.add(backButton);
            return panel;
        }

        private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
            JButton button = new JButton(text);
            button.setFont(new Font("微软雅黑", Font.BOLD, 14));
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            button.addActionListener(listener);

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    button.setBackground(bgColor.brighter());
                }

                public void mouseExited(MouseEvent evt) {
                    button.setBackground(bgColor);
                }
            });

            return button;
        }

        private void refreshMovieTable() {
            // TODO: Load movies from database
            if (movieList.isEmpty()) {
                Label descriptionArea = new Label();
                movieList.add(new Movie("流浪地球", "郭帆", "吴京", "科幻冒险", 125, 8.5, descriptionArea.getText(), false));
                movieList.add(new Movie("长津湖", "陈凯歌", "吴京", "战争历史", 176, 9.0, descriptionArea.getText(), false));
            }

            tableModel.setRowCount(0);
            for (Movie movie : movieList) {
                tableModel.addRow(new Object[]{
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getActors(),
                        movie.getDuration(),
                        movie.getRating(),
                        movie.getDescription()
                });
            }
        }

        private void searchMovies(String searchText, String searchType) {
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入搜索内容！", "搜索提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // TODO: Implement search logic
            List<Movie> results = new ArrayList<>();
            for (Movie movie : movieList) {
                switch (searchType) {
                    case "片名":
                        if (movie.getTitle().contains(searchText)) results.add(movie);
                        break;
                    case "导演":
                        if (movie.getDirector().contains(searchText)) results.add(movie);
                        break;
                    case "主演":
                        if (movie.getActors().contains(searchText)) results.add(movie);
                        break;
                    case "组合查询":
                        if (movie.getTitle().contains(searchText) ||
                                movie.getDirector().contains(searchText) ||
                                movie.getActors().contains(searchText)) {
                            results.add(movie);
                        }
                        break;
                }
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "未找到匹配的影片", "搜索结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "找到 " + results.size() + " 部影片:\n" +
                                results.stream().map(Movie::getTitle).reduce((a, b) -> a + "\n" + b).orElse(""),
                        "搜索结果", JOptionPane.INFORMATION_MESSAGE);
            }

            system.logOperation(currentUser, "影片管理", "搜索影片: " + searchType + " - " + searchText);
        }

        private void editSelectedMovie() {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要编辑的影片！", "编辑提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // TODO: Implement edit movie dialog
            Movie selectedMovie = movieList.get(selectedRow);
            system.logOperation(currentUser, "影片管理", "编辑影片: " + selectedMovie.getTitle());
        }

        private void deleteSelectedMovie() {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的影片！", "删除提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Movie selectedMovie = movieList.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除影片 '" + selectedMovie.getTitle() + "' 吗?\n删除后无法恢复!",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (hasSchedules(selectedMovie)) {
                    JOptionPane.showMessageDialog(this,
                            "无法删除: 影片 '" + selectedMovie.getTitle() + "' 已有排片信息!",
                            "删除失败",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                movieList.remove(selectedRow);
                refreshMovieTable();

                JOptionPane.showMessageDialog(this,
                        "影片 '" + selectedMovie.getTitle() + "' 已成功删除!",
                        "删除成功",
                        JOptionPane.INFORMATION_MESSAGE);

                system.logOperation(currentUser, "影片管理", "删除影片: " + selectedMovie.getTitle());
            }
        }

        private boolean hasSchedules(Movie movie) {
            // TODO: Check if movie has schedules in database
            return movie.getTitle().contains("流浪地球") || movie.getTitle().contains("长津湖");
        }

        public void refreshMovieData() {
            refreshMovieTable();
        }
    }
}