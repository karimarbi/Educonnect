package com.esprit.Controllers.Mayssa;

import com.esprit.Models.Cours;
import com.esprit.Entities.User;
import com.esprit.Services.RegistrationService;
import com.esprit.Entities.UserSession;
import com.esprit.utils.NotificationHelper;
import com.esprit.Services.WhatsAppService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CourseRegistrationController {

    @FXML private Label courseTitleLabel;
    @FXML private Label userInfoLabel;

    private Cours selectedCourse;
    private final RegistrationService registrationService = new RegistrationService();
    private final WhatsAppService whatsAppService = new WhatsAppService();

    public void setCourseData(Cours cours) {
        this.selectedCourse = cours;
        User currentUser = UserSession.getInstance().getCurrentUser();

        courseTitleLabel.setText("Course: " + cours.getTitre());
        userInfoLabel.setText("Registering: " + currentUser.getNom() + " " + currentUser.getPrenom() +
                " (" + currentUser.getEmail() + ")");
    }

    @FXML
    private void confirmRegistration() {
        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "No user logged in", Alert.AlertType.ERROR);
                return;
            }

            if (registrationService.registerForCourse(selectedCourse)) {
                showSuccessNotifications(currentUser);
                closeWindow();
            } else {
                showAlert("Error", "Registration failed - you may already be registered or have a course in the same category", Alert.AlertType.ERROR);
                NotificationHelper.showNotification(
                        "Registration Failed",
                        "Could not register for: " + selectedCourse.getTitre()
                );
            }
        } catch (Exception e) {
            handleRegistrationError(e);
        }
    }

    private void showSuccessNotifications(User user) {
        // Show desktop notification
        NotificationHelper.showNotification(
                "Registration Success",
                "You are now registered for: " + selectedCourse.getTitre()
        );

        // Show alert
        showAlert("Success", "Successfully registered for the course!", Alert.AlertType.INFORMATION);

        // Send WhatsApp confirmation
        try {
            String phoneNumber = String.valueOf(user.getTelephone());
            String whatsAppMessage = createWhatsAppMessage(user, selectedCourse);
            whatsAppService.sendWhatsAppMessage(phoneNumber, whatsAppMessage);
        } catch (Exception e) {
            System.err.println("WhatsApp notification failed: " + e.getMessage());
            showAlert("Warning", "Registration successful but WhatsApp confirmation failed: " + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private String createWhatsAppMessage(User user, Cours course) {
        return String.format(
                "Hello %s %s,\n\n" +
                        "📚 *Course Registration Confirmation*\n\n" +
                        "You have successfully registered for:\n" +
                        "• *Title*: %s\n" +
                        "• *Category*: %s\n" +
                        "• *Date*: %s\n\n" +
                        "Thank you for choosing EduConnect!\n\n" +
                        "Best regards,\n" +
                        "The EduConnect Team",
                user.getPrenom(),
                user.getNom(),
                course.getTitre(),
                course.getCategorie() != null ? course.getCategorie().getNomCategorie() : "General"
        );
    }

    private void handleRegistrationError(Exception e) {
        showAlert("Error", "An error occurred during registration: " + e.getMessage(), Alert.AlertType.ERROR);
        NotificationHelper.showNotification(
                "Registration Error",
                "Error registering for course"
        );
        e.printStackTrace();
    }

    @FXML
    private void cancelRegistration() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) courseTitleLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}