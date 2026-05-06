package panels;

import main.CinemaManagementSystem;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTextArea logArea;
    private List<User> users;
    private List<String> logs;
    private User currentUser;
    private CinemaManagementSystem system;
    private JLabel titleLabel;

    public AdminPanel(CinemaManagementSystem system, List<User> users, List<String> logs, Object o) {
        this.system = system;
        this.users = users;
        this.logs = logs;
        this.currentUser = currentUser;

        setBackground(new Color(245, 248, 250));
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        titleLabel = new JLabel("管理员面板 - 登录用户: " +
                (currentUser != null ? currentUser.getUsername() : "未登录"));
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("系统管理与用户维护");
        subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 215, 230));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Main content with card-style navigation
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 25, 25));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        contentPanel.setBackground(new Color(245, 248, 250));

        String[] menuItems = {"密码管理", "用户管理", "操作日志"};
        String[] icons = {"🔑", "👥", "📝"};

        for (int i = 0; i < menuItems.length; i++) {
            JPanel cardPanel = createCardPanel(menuItems[i], icons[i]);
            contentPanel.add(cardPanel);
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createCardPanel(String title, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240)),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 250, 252));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 220, 240)),
                        BorderFactory.createEmptyBorder(25, 15, 25, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 235, 240)),
                        BorderFactory.createEmptyBorder(25, 15, 25, 15)
                ));
            }
        });

        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(60, 75, 100));

        card.add(iconLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        // Add click functionality
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleNavigation(title);
            }
        });

        return card;
    }

    private void handleNavigation(String action) {
        switch (action) {
            case "密码管理":
                showPasswordManagementPanel();
                break;
            case "用户管理":
                showUserManagementPanel();
                break;
            case "操作日志":
                showLogPanel();
                break;
            default:
                JOptionPane.showMessageDialog(system, "未知操作: " + action, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPasswordManagementPanel() {
        JDialog dialog = new JDialog(system, "密码管理", true);
        dialog.setSize(800, 700);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(system);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 修改自身密码部分
        JPanel selfPasswordPanel = createSectionPanel("修改自身密码");
        selfPasswordPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        selfPasswordPanel.add(createFormLabel("当前密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField oldPasswordField = createFormField(20);
        selfPasswordPanel.add(oldPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        selfPasswordPanel.add(createFormLabel("新密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField newPasswordField = createFormField(20);
        selfPasswordPanel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        selfPasswordPanel.add(createFormLabel("确认新密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = createFormField(20);
        selfPasswordPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton changePasswordButton = createPrimaryButton("修改密码");
        changePasswordButton.addActionListener(e -> {
            changePassword(oldPasswordField, newPasswordField, confirmPasswordField);
        });
        selfPasswordPanel.add(changePasswordButton, gbc);

        // 重置其他用户密码部分
        JPanel resetPasswordPanel = createSectionPanel("重置其他用户密码");
        resetPasswordPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 2;
        gbc.gridy = 2;
        resetPasswordPanel.add(createFormLabel("选择用户:"), gbc);

        gbc.gridx = 1;
        DefaultComboBoxModel<User> userModel = new DefaultComboBoxModel<>();
        for (User user : users) {
            // 只显示非管理员用户（经理和前台）
            if (!"管理员".equals(user.getRole()) && !user.getUsername().equals("admin")) {
                userModel.addElement(user);
            }
        }
        JComboBox<User> userComboBox = new JComboBox<>(userModel);
        userComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userComboBox.setPreferredSize(new Dimension(200, 30));
        resetPasswordPanel.add(userComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        resetPasswordPanel.add(createFormLabel("重置密码:"), gbc);

        gbc.gridx = 1;
        JPanel resetInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resetInfoPanel.setBackground(Color.WHITE);
        JLabel passwordLabel = new JLabel("123456");
        passwordLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(200, 0, 0)); // 红色强调
        JLabel infoLabel = new JLabel("(用户首次登录需修改)");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoLabel.setForeground(Color.BLACK);
        resetInfoPanel.add(passwordLabel);
        resetInfoPanel.add(infoLabel);
        resetPasswordPanel.add(resetInfoPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton resetPasswordButton = createPrimaryButton("重置密码");
        resetPasswordButton.addActionListener(e -> {
            resetUserPassword(userComboBox);
        });
        resetPasswordPanel.add(resetPasswordButton, gbc);

        // Add to main panel
        panel.add(selfPasswordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(resetPasswordPanel);

        // Back button
        JButton backButton = new JButton("返回主界面");
        backButton.addActionListener(e -> dialog.dispose());
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 248, 250));
        buttonPanel.add(backButton);
        panel.add(buttonPanel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showUserManagementPanel() {
        JDialog dialog = new JDialog(system, "用户管理", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(system);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 248, 250));

        // User table with modern styling
        String[] columnNames = {"用户ID", "用户名", "角色", "注册时间", "手机号", "需要重置密码"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        userTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setGridColor(new Color(230, 235, 240));
        userTable.setShowGrid(true);
        userTable.setForeground(Color.BLACK); // 确保文字为黑色

        // 设置表头
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 248, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton addButton = createTableButton("添加用户", new Color(70, 130, 180));
        addButton.addActionListener(e -> {
            showAddUserDialog();
            loadUserData();
        });

        JButton editButton = createTableButton("编辑用户", new Color(60, 179, 113));
        editButton.addActionListener(e -> {
            editSelectedUser();
            loadUserData();
        });

        JButton deleteButton = createTableButton("删除用户", new Color(205, 92, 92));
        deleteButton.addActionListener(e -> {
            deleteSelectedUser();
            loadUserData();
        });

        JButton searchButton = createTableButton("查询用户", new Color(106, 90, 205));
        searchButton.addActionListener(e -> showSearchDialog());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Back button
        JButton backButton = new JButton("返回主界面");
        backButton.addActionListener(e -> dialog.dispose());
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel backPanel = new JPanel();
        backPanel.setBackground(new Color(245, 248, 250));
        backPanel.add(backButton);
        bottomPanel.add(backPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(panel, BorderLayout.CENTER);
        loadUserData();
        dialog.setVisible(true);
    }

    private void showLogPanel() {
        JDialog dialog = new JDialog(system, "操作日志", true);
        dialog.setSize(700, 500);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(system);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 248, 250));

        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setForeground(Color.BLACK); // 确保文字为黑色
        logArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = createPrimaryButton("刷新日志");
        refreshButton.addActionListener(e -> loadLogData());
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("返回主界面");
        backButton.addActionListener(e -> dialog.dispose());
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 248, 250));
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel, BorderLayout.CENTER);
        loadLogData();
        dialog.setVisible(true);
    }

    // Helper methods for UI components
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 235, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(60, 75, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel);

        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.BOLD, 14));
        label.setForeground(Color.BLACK); // 确保文字为黑色
        return label;
    }

    private JPasswordField createFormField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setForeground(Color.BLACK); // 确保文字为黑色
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JButton createTableButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return button;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (titleLabel != null) {
            titleLabel.setText("管理员面板 - 登录用户: " +
                    (currentUser != null ? currentUser.getUsername() : "未登录"));
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(system, "添加新用户", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(system);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(createFormLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setForeground(Color.BLACK);
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(createFormLabel("角色:"), gbc);

        gbc.gridx = 1;
        String[] roles = {"管理员", "经理", "前台"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        roleCombo.setForeground(Color.BLACK);
        roleCombo.setPreferredSize(new Dimension(200, 30));
        dialog.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(createFormLabel("手机号:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneField.setForeground(Color.BLACK);
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("初始密码: 123456 (用户首次登录需修改密码)");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoLabel.setForeground(Color.BLACK);
        dialog.add(infoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = createPrimaryButton("添加用户");
        addButton.addActionListener(e -> addNewUser(dialog, usernameField, roleCombo, phoneField));
        dialog.add(addButton, gbc);

        dialog.setVisible(true);
    }

    /**
     * 添加新用户
     */
    private void addNewUser(JDialog dialog, JTextField usernameField, JComboBox<String> roleCombo, JTextField phoneField) {
        String username = usernameField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String phone = phoneField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "手机号不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查用户名唯一性
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 检查手机号唯一性
        for (User user : users) {
            if (user.getPhone().equals(phone)) {
                JOptionPane.showMessageDialog(dialog, "手机号已被使用！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 创建新用户并设置唯一ID
        User newUser = new User(
                "U" + (users.size() + 1000), // 生成唯一用户ID
                username,
                "123456", // 默认密码
                role,
                phone
        );
        newUser.setPasswordResetRequired(true);
        users.add(newUser);

        // 记录日志
        system.addLog("添加用户", "添加" + role + "用户: " + username);

        JOptionPane.showMessageDialog(dialog, "用户添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    }

    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userId = (String) userTableModel.getValueAt(selectedRow, 0);
        User selectedUser = users.stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "未找到用户信息！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedUser.getUsername().equals("admin")) {
            JOptionPane.showMessageDialog(this, "不能编辑admin用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 管理员只能编辑经理和前台
        if (selectedUser.getRole().equals("管理员")) {
            JOptionPane.showMessageDialog(this,
                    "只能编辑经理和前台用户！",
                    "操作限制",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(system, "编辑用户信息", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(system);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(createFormLabel("用户ID:"), gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField(selectedUser.getUserId(), 20);
        idField.setEditable(false);
        idField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        idField.setForeground(Color.BLACK);
        dialog.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(createFormLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(selectedUser.getUsername(), 20);
        usernameField.setEditable(false);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setForeground(Color.BLACK);
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(createFormLabel("角色:"), gbc);

        gbc.gridx = 1;
        String[] roles = {"经理", "前台"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setSelectedItem(selectedUser.getRole());
        roleCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        roleCombo.setForeground(Color.BLACK);
        roleCombo.setPreferredSize(new Dimension(200, 30));
        dialog.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(createFormLabel("手机号:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(selectedUser.getPhone(), 20);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneField.setForeground(Color.BLACK);
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = createPrimaryButton("保存修改");
        saveButton.addActionListener(e -> saveUserChanges(dialog, selectedUser, roleCombo, phoneField));
        dialog.add(saveButton, gbc);

        dialog.setVisible(true);
    }

    /**
     * 保存用户修改
     */
    private void saveUserChanges(JDialog dialog, User selectedUser, JComboBox<String> roleCombo, JTextField phoneField) {
        String newRole = (String) roleCombo.getSelectedItem();
        String newPhone = phoneField.getText().trim();

        if (newPhone.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "手机号不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查手机号唯一性（排除当前用户）
        for (User user : users) {
            if (!user.getUserId().equals(selectedUser.getUserId()) &&
                    user.getPhone().equals(newPhone)) {
                JOptionPane.showMessageDialog(dialog, "手机号已被其他用户使用！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 更新用户信息
        String oldRole = selectedUser.getRole();
        selectedUser.setRole(newRole);
        selectedUser.setPhone(newPhone);

        // 记录日志
        system.addLog("修改用户", "修改用户" + selectedUser.getUsername() +
                " (角色: " + oldRole + " → " + newRole + ")");

        JOptionPane.showMessageDialog(dialog, "用户信息修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    }

    /**
     * 删除选中用户
     */
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userId = (String) userTableModel.getValueAt(selectedRow, 0);
        User selectedUser = null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                selectedUser = user;
                break;
            }
        }

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "未找到用户信息！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 防止删除admin账户
        if (selectedUser.getUsername().equals("admin")) {
            JOptionPane.showMessageDialog(this, "不能删除admin用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除用户: " + selectedUser.getUsername() + " (" + selectedUser.getRole() + ") 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            users.remove(selectedUser);
            // 记录日志
            system.addLog("删除用户", "删除" + selectedUser.getRole() + "用户: " + selectedUser.getUsername());

            loadUserData();
            JOptionPane.showMessageDialog(this, "用户删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showSearchDialog() {
        JDialog dialog = new JDialog(system, "查询用户", true);
        dialog.setSize(450, 200);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(system);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(createFormLabel("查询条件:"), gbc);

        gbc.gridx = 1;
        String[] searchOptions = {"用户ID", "用户名", "手机号"};
        JComboBox<String> searchCombo = new JComboBox<>(searchOptions);
        searchCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchCombo.setForeground(Color.BLACK);
        searchCombo.setPreferredSize(new Dimension(150, 30));
        dialog.add(searchCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(createFormLabel("查询值:"), gbc);

        gbc.gridx = 1;
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setForeground(Color.BLACK);
        dialog.add(searchField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton searchButton = createPrimaryButton("查询");
        searchButton.addActionListener(e -> searchUsers(dialog, searchCombo, searchField));
        dialog.add(searchButton, gbc);

        dialog.setVisible(true);
    }

    /**
     * 搜索用户
     */
    private void searchUsers(JDialog dialog, JComboBox<String> searchCombo, JTextField searchField) {
        String searchType = (String) searchCombo.getSelectedItem();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            loadUserData();
            dialog.dispose();
            return;
        }

        userTableModel.setRowCount(0);
        for (User user : users) {
            boolean match = false;
            if ("用户ID".equals(searchType)) {
                match = user.getUserId().contains(searchValue);
            } else if ("用户名".equals(searchType)) {
                match = user.getUsername().contains(searchValue);
            } else if ("手机号".equals(searchType)) {
                match = user.getPhone().contains(searchValue);
            }

            if (match) {
                userTableModel.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getFormattedRegisterTime(),
                        user.getPhone(),
                        user.isPasswordResetRequired() ? "是" : "否"
                });
            }
        }

        dialog.dispose();
    }

    /**
     * 加载用户数据到表格
     */
    private void loadUserData() {
        userTableModel.setRowCount(0);
        for (User user : users) {
            // 显示所有用户（包括管理员）
            userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getFormattedRegisterTime(),
                    user.getPhone(),
                    user.isPasswordResetRequired() ? "是" : "否"
            });
        }
    }

    /**
     * 加载日志数据
     */
    private void loadLogData() {
        logArea.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logArea.append(sdf.format(new Date()) + " - 日志刷新时间\n\n");

        // 逆序加载日志（最新的在最前面）
        for (int i = logs.size() - 1; i >= 0; i--) {
            logArea.append(logs.get(i) + "\n");
        }
    }

    /**
     * 修改密码
     */
    private void changePassword(JPasswordField oldPasswordField,
                                JPasswordField newPasswordField,
                                JPasswordField confirmPasswordField) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "未登录用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (!oldPass.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "旧密码不正确！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "两次输入的新密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setPassword(newPass);
        // 记录日志
        system.addLog("修改密码", currentUser.getUsername() + "修改了自己的密码");

        // 清空字段
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");

        JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 重置用户密码
     */
    private void resetUserPassword(JComboBox<User> userComboBox) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "未登录用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User selectedUser = (User) userComboBox.getSelectedItem();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请选择要重置密码的用户！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 防止重置admin密码
        if (selectedUser.getUsername().equals("admin")) {
            JOptionPane.showMessageDialog(this, "不能重置admin用户的密码！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedUser.setPassword("123456");
        selectedUser.setPasswordResetRequired(true);
        // 记录日志
        system.addLog("重置密码", "重置" + selectedUser.getRole() + "用户" +
                selectedUser.getUsername() + "的密码");

        JOptionPane.showMessageDialog(this,
                "已成功重置 " + selectedUser.getUsername() + " 的密码为 123456\n用户下次登录必须修改密码",
                "成功",
                JOptionPane.INFORMATION_MESSAGE);
    }
}