package ui;

import model.User;
import service.CinemaService;
import javax.swing.*;
import java.awt.*;

public class TicketDialog extends JDialog {
    private final CinemaService service;
    private final User user;

    public TicketDialog(Frame parent, CinemaService service, User user) {
        super(parent, "Book Ticket", true);
        this.service = service;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        // Add your UI components here
    }
}