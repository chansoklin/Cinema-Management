package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private String userId;
    private String username;
    private String password;
    private String role;
    private String phone;
    private LocalDateTime registerTime;
    private boolean passwordResetRequired;

    public User(String s, String username, String password, String role, String phone) {
        this.userId = generateUserId();
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.registerTime = LocalDateTime.now();
        this.passwordResetRequired = false;
    }

    private String generateUserId() {
        return "UID" + System.currentTimeMillis();
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public String getFormattedRegisterTime() {
        return registerTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    // Alias methods for requirePasswordChange (from second class)
    public boolean isRequirePasswordChange() {
        return passwordResetRequired;
    }

    public void setRequirePasswordChange(boolean requirePasswordChange) {
        this.passwordResetRequired = requirePasswordChange;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }

    public void markPasswordResetRequired() {

    }
}