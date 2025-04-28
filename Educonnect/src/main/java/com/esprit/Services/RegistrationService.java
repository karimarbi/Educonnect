package com.esprit.Services;

import com.esprit.Models.Cours;
import com.esprit.Entities.User;
import com.esprit.Entities.UserSession;
import com.esprit.Connexion.DatabaseConnection;
import java.sql.*;

public class RegistrationService {
    private Connection connection;

    public RegistrationService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean registerForCourse(Cours cours) {
        User currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser == null) {
            System.err.println("No user logged in");
            return false;
        }

        if (cours == null) {
            System.err.println("No course selected");
            return false;
        }

        try {
            // Check if already registered
            if (isUserRegisteredForCourse(currentUser.getId(), cours.getId())) {
                System.out.println("User already registered for this course");
                return false;
            }

            // Check for same category courses
            if (hasCourseInSameCategory(currentUser.getId(), cours.getCategorie().getId())) {
                System.out.println("User already has a course in this category");
                return false;
            }

            // Register for the course
            return addUserToCourse(currentUser.getId(), cours.getId());

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            return false;
        }
    }

    public boolean isUserRegisteredForCourse(int userId, int courseId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_courses WHERE user_id = ? AND course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean hasCourseInSameCategory(int userId, int categoryId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_courses uc " +
                "JOIN cours c ON uc.course_id = c.id " +
                "WHERE uc.user_id = ? AND c.categorie_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean addUserToCourse(int userId, int courseId) throws SQLException {
        String query = "INSERT INTO user_courses (user_id, course_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            return stmt.executeUpdate() > 0;
        }
    }
}