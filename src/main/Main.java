package main;

import ui.MainFrame;
import util.DBConnection;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Test database connection first
        DBConnection.testConnection();

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start application
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}