package panels;

import main.CinemaManagementSystem;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LoginPanel extends JPanel {
    private final CinemaManagementSystem system;
    private final List<User> users;
    private final List<String> logs;
    private JLabel statusLabel;
    private User currentUser;

    public LoginPanel(CinemaManagementSystem system, List<User> users, List<String> logs) {
        this.system = system;
        this.users = users;
        this.logs = logs;
        this.currentUser = currentUser;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // 浅蓝色背景

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel titleLabel = new JLabel("万东电影院管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112)); // 深蓝色
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // 创建登录面板
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("系统登录"));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(400, 300));

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        add(loginPanel, gbc);

        // 重置约束
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        // 用户名标签
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(userLabel, gbc);

        // 用户名输入框
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JTextField userField = new JTextField(15);
        userField.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(userField, gbc);

        // 密码标签
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(passLabel, gbc);

        // 密码输入框
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(passField, gbc);

        // 角色标签
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(roleLabel, gbc);

        // 角色选择
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        String[] roles = {"管理员", "经理", "前台"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("宋体", Font.PLAIN, 16));
        loginPanel.add(roleCombo, gbc);

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("宋体", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180)); // 钢蓝色
        loginButton.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginPanel.add(loginButton, gbc);

        // 状态标签（用于显示错误信息）
        statusLabel = new JLabel();
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        loginPanel.add(statusLabel, gbc);

        // 登录按钮事件处理
        loginButton.addActionListener((ActionEvent e) -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("用户名和密码不能为空!");
                return;
            }

            // 查找匹配的用户
            User authenticatedUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username) &&
                        user.getPassword().equals(password) &&
                        user.getRole().equals(role)) {
                    authenticatedUser = user;
                    break;
                }
            }

            if (authenticatedUser == null) {
                statusLabel.setText("用户名、密码或角色不正确！");
                return;
            }

            // 检查是否需要重置密码
            if (authenticatedUser.isPasswordResetRequired()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "您的密码已被重置，需要修改密码才能继续操作。\n是否现在修改密码？",
                        "密码重置要求",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    showPasswordChangeDialog(authenticatedUser);
                    return;
                }
            }

            // 根据角色跳转到相应面板
            switch (role) {
                case "管理员":
                    system.showAdminPanel(authenticatedUser);
                    break;
                case "经理":
                    system.showManagerPanel(authenticatedUser);
                    break;
                case "前台":
                    system.showFrontDeskPanel(authenticatedUser);
                    break;
            }
        });

        // 添加取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("宋体", Font.BOLD, 14));
        cancelButton.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            statusLabel.setText("");
        });
        gbc.gridy = 5;
        loginPanel.add(cancelButton, gbc);
    }

    private void showPasswordChangeDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改密码", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("新密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("宋体", Font.PLAIN, 14));
        dialog.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("确认新密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("宋体", Font.PLAIN, 14));
        dialog.add(confirmPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton changeButton = new JButton("修改密码");
        changeButton.setFont(new Font("宋体", Font.BOLD, 14));
        changeButton.addActionListener(e -> {
            String newPass = new String(newPasswordField.getPassword());
            String confirmPass = new String(confirmPasswordField.getPassword());

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的新密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            user.setPassword(newPass);
            user.setPasswordResetRequired(false);

            String log = "用户 " + user.getUsername() + " 在登录时修改了密码";
            logs.add(log);

            JOptionPane.showMessageDialog(dialog, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();

            // 根据角色跳转到相应面板
            switch (user.getRole()) {
                case "管理员":
                    system.showAdminPanel(user);
                    break;
                case "经理":
                    system.showManagerPanel(user);
                    break;
                case "前台":
                    system.showFrontDeskPanel(user);
                    break;
            }
        });
        dialog.add(changeButton, gbc);

        dialog.setVisible(true);
    }
}