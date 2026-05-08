package panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import model.*;
import service.*;
import exception.DatabaseException;
import exception.ValidationException;

public class UserManagementPanel extends JPanel {
    private final UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JLabel statusLabel;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_WHITE = Color.WHITE;
    private final Color BG_GRAY = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(220, 220, 220);

    public UserManagementPanel() {
        this.userService = new UserService();
        initComponents();
        loadUsers();
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

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BG_WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        statusLabel = new JLabel("✅ Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(Color.GRAY);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setBackground(BG_WHITE);

        filterPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterUsers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterUsers(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterUsers(); }
        });
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Role:"));
        roleFilter = new JComboBox<>(new String[]{"All", "ADMIN", "MANAGER", "FRONT_DESK", "CUSTOMER"});
        roleFilter.addActionListener(e -> filterUsers());
        filterPanel.add(roleFilter);

        JButton refreshBtn = createButton("🔄 Refresh", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadUsers());
        filterPanel.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Created"};
        tableModel = new DefaultTableModel(columns, 0);

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(40);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(52, 73, 94));
        userTable.getTableHeader().setForeground(Color.WHITE);

        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        userTable.getColumnModel().getColumn(5).setCellRenderer(new RoleCellRenderer());

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addBtn = createButton("➕ Add User", SUCCESS_COLOR);
        addBtn.addActionListener(e -> showAddUserDialog());

        JButton editBtn = createButton("✏️ Edit User", WARNING_COLOR);
        editBtn.addActionListener(e -> showEditUserDialog());

        JButton deleteBtn = createButton("🗑️ Delete User", DANGER_COLOR);
        deleteBtn.addActionListener(e -> deleteUser());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = userTable.getSelectedRow() >= 0;
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

    private void loadUsers() {
        try {
            tableModel.setRowCount(0);
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail() != null ? user.getEmail() : "—",
                        user.getPhone() != null ? user.getPhone() : "—",
                        user.getRole(),
                        user.getCreatedAt() != null ? user.getCreatedAt().toString().substring(0, 10) : "N/A"
                });
            }
            statusLabel.setText("✅ Loaded " + users.size() + " users");
        } catch (DatabaseException e) {
            statusLabel.setText("❌ Error loading users");
            loadDemoUsers();
        }
    }

    private void loadDemoUsers() {
        Object[][] demoUsers = {
                {1, "admin", "Admin User", "admin@cinema.com", "1234567890", "ADMIN", "2024-01-15"},
                {2, "manager1", "John Manager", "manager@cinema.com", "1234567891", "MANAGER", "2024-01-15"},
                {3, "staff1", "Jane Staff", "staff@cinema.com", "1234567892", "FRONT_DESK", "2024-01-15"},
                {4, "customer1", "John Doe", "john@email.com", "1234567893", "CUSTOMER", "2024-01-15"}
        };
        for (Object[] user : demoUsers) {
            tableModel.addRow(user);
        }
        statusLabel.setText("📊 Showing " + tableModel.getRowCount() + " users (Demo Mode)");
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = (String) roleFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> searchFilter = null;
        RowFilter<DefaultTableModel, Object> roleRowFilter = null;

        if (!searchText.isEmpty()) {
            searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 2);
        }
        if (selectedRole != null && !selectedRole.equals("All")) {
            roleRowFilter = RowFilter.regexFilter(selectedRole, 5);
        }

        if (searchFilter != null && roleRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(searchFilter, roleRowFilter)));
        } else if (searchFilter != null) {
            sorter.setRowFilter(searchFilter);
        } else if (roleRowFilter != null) {
            sorter.setRowFilter(roleRowFilter);
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void showAddUserDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "FRONT_DESK", "CUSTOMER"});

        Object[] fields = {
                "Username:", username,
                "Password:", password,
                "Full Name:", name,
                "Email:", email,
                "Phone:", phone,
                "Role:", role
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                User user = new User();
                user.setUsername(username.getText());
                user.setPassword(new String(password.getPassword()));
                user.setName(name.getText());
                user.setEmail(email.getText());
                user.setPhone(phone.getText());
                user.setRole((String) role.getSelectedItem());

                if (userService.createUser(user)) {
                    JOptionPane.showMessageDialog(this, "✅ User added!");
                    loadUsers();
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, "Validation Error: " + ex.getMessage());
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showEditUserDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) return;

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            User user = userService.getUserById(id);

            JTextField name = new JTextField(user.getName());
            JTextField email = new JTextField(user.getEmail());
            JTextField phone = new JTextField(user.getPhone());
            JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "FRONT_DESK", "CUSTOMER"});
            role.setSelectedItem(user.getRole());

            Object[] fields = {
                    "Full Name:", name,
                    "Email:", email,
                    "Phone:", phone,
                    "Role:", role
            };

            int result = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                user.setName(name.getText());
                user.setEmail(email.getText());
                user.setPhone(phone.getText());
                user.setRole((String) role.getSelectedItem());

                if (userService.updateUser(user)) {
                    JOptionPane.showMessageDialog(this, "✅ User updated!");
                    loadUsers();
                }
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, "Validation Error: " + ex.getMessage());
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) return;

        String username = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + username + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                if (userService.deleteUser(id)) {
                    JOptionPane.showMessageDialog(this, "✅ User deleted!");
                    loadUsers();
                }
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    class RoleCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String role = value != null ? value.toString() : "";

            if (!isSelected) {
                switch (role) {
                    case "ADMIN":
                        setForeground(DANGER_COLOR);
                        setFont(new Font("Segoe UI", Font.BOLD, 11));
                        break;
                    case "MANAGER":
                        setForeground(PRIMARY_COLOR);
                        break;
                    case "FRONT_DESK":
                        setForeground(SUCCESS_COLOR);
                        break;
                    case "CUSTOMER":
                        setForeground(new Color(155, 89, 182));
                        break;
                    default:
                        setForeground(Color.GRAY);
                }
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }

    public void refresh() {
        loadUsers();
    }
}