package controller;

import model.User;
import service.UserService;
import exception.DatabaseException;

public class LoginController {
    private UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    public User login(String username, String password) throws DatabaseException {
        return userService.login(username, password);
    }

    public boolean logout() {
        // Implement logout logic
        return true;
    }
}