import main.CinemaManagementSystem;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CinemaManagementSystem system = new CinemaManagementSystem();
            system.setVisible(true);
        });
    }
}