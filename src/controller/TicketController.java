package controller;

import model.Ticket;
import service.TicketService;
import exception.DatabaseException;
import javax.swing.JOptionPane;

public class TicketController {

    private final TicketService service = new TicketService();

    public boolean sellTicket(Ticket t) {
        try {
            return service.sellTicket(t);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(null,
                    "Error selling ticket: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean refundTicket(int id) {
        try {
            return service.refundTicket(id);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(null,
                    "Error refunding ticket: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}