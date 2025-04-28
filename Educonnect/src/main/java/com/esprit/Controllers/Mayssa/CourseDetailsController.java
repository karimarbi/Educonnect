package com.esprit.Controllers.Mayssa;

import com.esprit.Models.Cours;
import com.esprit.Services.RegistrationService;
import com.esprit.Entities.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;

public class CourseDetailsController {

    @FXML
    private ImageView courseImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Text descriptionText;
    @FXML
    private Text contentText;
    @FXML
    private Button registerButton;
    @FXML
    private Button closeButton;

    private Cours currentCourse;
    private final RegistrationService registrationService = new RegistrationService();

    public void setCourseData(Cours cours) {
        this.currentCourse = cours;
        updateUI();
    }

    private void updateUI() {
        if (currentCourse == null) return;

        // Set course image
        try {
            if (currentCourse.getImagePath() != null && !currentCourse.getImagePath().isEmpty()) {
                File imageFile = new File("uploads/cours/" + currentCourse.getImagePath());
                if (imageFile.exists()) {
                    courseImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    courseImageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
                }
            } else {
                courseImageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            courseImageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
        }

        // Set course details
        titleLabel.setText(currentCourse.getTitre());

        if (currentCourse.getCategorie() != null) {
            categoryLabel.setText("Category: " + currentCourse.getCategorie().getNomCategorie());
        } else {
            categoryLabel.setText("Category: Not specified");
        }

        descriptionText.setText(currentCourse.getDescription());
        contentText.setText(currentCourse.getContenu());

        // Hide register button if user is not logged in or already registered
        if (UserSession.getInstance().getCurrentUser() == null) {
            registerButton.setVisible(false);
        } else if (UserSession.getInstance().getCurrentUser().isRegisteredForCourse(currentCourse)) {
            registerButton.setText("Already Registered");
            registerButton.setDisable(true);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            if (registrationService.registerForCourse(currentCourse)) {
                showAlert("Success", "Successfully registered for the course!", Alert.AlertType.INFORMATION);
                registerButton.setText("Registered");
                registerButton.setDisable(true);
            } else {
                showAlert("Warning", "Registration failed. You may already be registered for this course or another course in the same category.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred during registration: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        closeButton.getScene().getWindow().hide();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}