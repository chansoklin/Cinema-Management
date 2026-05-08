package ui;

import javax.swing.*;
import java.awt.*;
import model.User;
import panels.*;
import util.Constants;

public class MainFrame extends JFrame implements LoginPanel.LoginListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;
    private AdminPanel adminPanel;
    private ManagerPanel managerPanel;
    private FrontDeskPanel frontDeskPanel;
    private CustomerPortalPanel customerPanel;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Cinema Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add login panel
        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "login");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    @Override
    public void onLoginSuccess(User user) {
        this.currentUser = user;

        // Create role-specific panel
        String panelName = "main";
        JPanel rolePanel = null;

        switch (user.getRole()) {
            case Constants.ROLE_ADMIN:
                if (adminPanel == null) {
                    adminPanel = new AdminPanel(user);
                } else {
                    adminPanel.refreshData();
                }
                rolePanel = adminPanel;
                break;
            case Constants.ROLE_MANAGER:
                if (managerPanel == null) {
                    managerPanel = new ManagerPanel(user);
                } else {
                    managerPanel.showManagerPanel(user);
                }
                rolePanel = managerPanel;
                break;
            case Constants.ROLE_FRONT_DESK:
                if (frontDeskPanel == null) {
                    frontDeskPanel = new FrontDeskPanel(user);
                }
                rolePanel = frontDeskPanel;
                break;
            case Constants.ROLE_CUSTOMER:  // ADD THIS CASE
                if (customerPanel == null) {
                    customerPanel = new CustomerPortalPanel(user);
                }
                rolePanel = customerPanel;
                break;
        }

        if (rolePanel != null) {
            // Check if panel already exists
            if (mainPanel.getComponentCount() > 1) {
                mainPanel.remove(1);
            }
            mainPanel.add(rolePanel, panelName);
            cardLayout.show(mainPanel, panelName);
        }
    }

    public void showManagerPanel(User user) {
        if (managerPanel == null) {
            managerPanel = new ManagerPanel(user);
        } else {
            managerPanel.showManagerPanel(user);
        }

        if (mainPanel.getComponentCount() > 1) {
            mainPanel.remove(1);
        }
        mainPanel.add(managerPanel, "manager");
        cardLayout.show(mainPanel, "manager");
    }

    public void showAdminPanel(User user) {
        if (adminPanel == null) {
            adminPanel = new AdminPanel(user);
        } else {
            adminPanel.refreshData();
        }

        if (mainPanel.getComponentCount() > 1) {
            mainPanel.remove(1);
        }
        mainPanel.add(adminPanel, "admin");
        cardLayout.show(mainPanel, "admin");
    }

    public void showFrontDeskPanel(User user) {
        if (frontDeskPanel == null) {
            frontDeskPanel = new FrontDeskPanel(user);
        }

        if (mainPanel.getComponentCount() > 1) {
            mainPanel.remove(1);
        }
        mainPanel.add(frontDeskPanel, "frontdesk");
        cardLayout.show(mainPanel, "frontdesk");
    }
    public void showCustomerPanel(User user) {
        if (customerPanel == null) {
            customerPanel = new CustomerPortalPanel(user);
        }

        if (mainPanel.getComponentCount() > 1) {
            mainPanel.remove(1);
        }
        mainPanel.add(customerPanel, "customer");
        cardLayout.show(mainPanel, "customer");
    }
}