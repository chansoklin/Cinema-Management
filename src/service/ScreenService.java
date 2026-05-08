package service;

import model.Screen;
import dao.ScreenDAO;
import exception.DatabaseException;
import exception.ValidationException;
import util.ValidationUtil;
import java.util.List;

public class ScreenService {
    private ScreenDAO screenDAO;

    public ScreenService() {
        this.screenDAO = new ScreenDAO();
    }

    public List<Screen> getAllScreens() throws DatabaseException {
        return screenDAO.getAllScreens();
    }

    public Screen getScreenById(int id) throws DatabaseException {
        return screenDAO.getScreenById(id);
    }

    public boolean createScreen(Screen screen) throws DatabaseException, ValidationException {
        validateScreen(screen);
        return screenDAO.createScreen(screen);
    }

    public boolean updateScreen(Screen screen) throws DatabaseException, ValidationException {
        validateScreen(screen);
        return screenDAO.updateScreen(screen);
    }

    public boolean deleteScreen(int id) throws DatabaseException {
        // Check if screen has any schedules
        // Add validation logic here
        return screenDAO.deleteScreen(id);
    }
    public Screen getScreenByName(String name) throws DatabaseException {
        List<Screen> screens = screenDAO.getAllScreens();
        for (Screen screen : screens) {
            if (screen.getName().equalsIgnoreCase(name)) {
                return screen;
            }
        }
        return null;
    }
    private void validateScreen(Screen screen) throws ValidationException {
        ValidationUtil.validateNotEmpty(screen.getName(), "Screen name");
        ValidationUtil.validatePositiveNumber(screen.getCapacity(), "Capacity");
    }
}