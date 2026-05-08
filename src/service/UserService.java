package service;

import model.User;
import dao.UserDAO;
import exception.DatabaseException;
import exception.ValidationException;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            throw new DatabaseException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new DatabaseException("Password cannot be empty");
        }

        System.out.println("Attempting login for user: " + username);
        User user = userDAO.validateLogin(username, password);

        if (user != null) {
            System.out.println("Login successful for: " + username + " (Role: " + user.getRole() + ")");
        } else {
            System.out.println("Login failed for: " + username);
        }

        return user;
    }

    public User getUserById(int id) throws DatabaseException {
        return userDAO.getUserById(id);
    }

    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.getAllUsers();
    }

    public boolean createUser(User user) throws DatabaseException, ValidationException {
        validateUser(user);
        return userDAO.createUser(user);
    }

    public boolean updateUser(User user) throws DatabaseException, ValidationException {
        validateUser(user);
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int id) throws DatabaseException {
        return userDAO.deleteUser(id);
    }
    // Add this method to UserService.java
    public List<User> getUsersByRole(String role) throws DatabaseException {
        List<User> allUsers = userDAO.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getRole().equals(role)) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }
    private void validateUser(User user) throws ValidationException {
        ValidationUtil.validateNotEmpty(user.getUsername(), "Username");
        ValidationUtil.validateNotEmpty(user.getPassword(), "Password");
        ValidationUtil.validateNotEmpty(user.getName(), "Name");
    }
}