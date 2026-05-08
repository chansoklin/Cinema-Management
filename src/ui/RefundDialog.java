package ui;

import service.CinemaService;
import javax.swing.*;
import java.awt.*;

public class RefundDialog extends JDialog {
    private final CinemaService service;

    public RefundDialog(Frame parent, CinemaService service) {
        super(parent, "Refund Ticket", true);
        this.service = service;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
    }
}