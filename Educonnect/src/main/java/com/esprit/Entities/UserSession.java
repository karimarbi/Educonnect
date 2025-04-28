package com.esprit.Entities;

import com.esprit.Entities.User;
import com.esprit.Entities.UserRole;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession(User user) {
        this.currentUser = user;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static UserSession initializeUserSession(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        } else {
            instance.currentUser = user;
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : 0;
    }

    public String getUserName() {
        return currentUser != null ? currentUser.getNom() : "";
    }

    public String getPrenom() {
        return currentUser != null ? currentUser.getPrenom() : "";
    }

    public UserRole getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public String getAddress() {
        return currentUser != null ? currentUser.getAdresse() : "";
    }

    public String getPhone() {
        return currentUser != null ? String.valueOf(currentUser.getTelephone()) : "";
    }

    public void cleanUserSession() {
        currentUser = null;
        instance = null;
    }
}