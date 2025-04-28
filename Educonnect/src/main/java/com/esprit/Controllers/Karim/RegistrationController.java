package com.esprit.Controllers.Karim;

import com.esprit.Entities.User;
import com.esprit.Entities.UserSession;
import com.esprit.Models.Event;
import com.esprit.Services.EventService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    @FXML private Label eventTitleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Label participantsLabel;
    private final EventService eventService = new EventService();

    private Event event;

    @FXML
    public void initialize() {
        checkAndPopulateUserData();
    }

    public void setEvent(Event event) {
        this.event = event;
        eventTitleLabel.setText("Register for: " + event.getTitle());
        participantsLabel.setText(String.format("Available spots: %d/%d",
                event.getAvailableSpots(), event.getMaxParticipants()));
        checkAndPopulateUserData();
    }

    @FXML
    private void handleSubmit() {
        if (validateForm()) {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "No user logged in");
                return;
            }

            if (eventService.registerUserForEvent(event, currentUser)) {
                redirectToPayment(); // Changed from closing window to redirecting to payment
            } else {
                showAlert("Error", "Registration failed - event may be full or you have a time conflict");
            }
        }
    }

    private void checkAndPopulateUserData() {
        if (UserSession.getInstance() != null && UserSession.getInstance().isLoggedIn()) {
            populateUserData();
        }
    }

    private void populateUserData() {
        UserSession session = UserSession.getInstance();
        if (session != null && session.getCurrentUser() != null) {
            Platform.runLater(() -> {
                firstNameField.setText(session.getPrenom());
                firstNameField.setEditable(false);

                lastNameField.setText(session.getUserName());
                lastNameField.setEditable(false);

                addressField.setText(session.getAddress());
                phoneField.setText(session.getPhone());
            });
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateForm() {
        if (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                addressField.getText().isEmpty() ||
                phoneField.getText().isEmpty()) {

            showAlert("Erreur", "Tous les champs sont obligatoires");
            return false;
        }

        if (!phoneField.getText().matches("\\d{8}")) {
            showAlert("Erreur", "Numéro de téléphone invalide (8 chiffres requis)");
            return false;
        }

        return true;
    }

    private void redirectToPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Events/PaymentForm.fxml"));
            VBox paymentForm = loader.load();

            PaymentController controller = loader.getController();
            controller.initializePayment(
                    event,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    addressField.getText(),
                    phoneField.getText()
            );

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Paiement pour " + event.getTitle());
            stage.setScene(new Scene(paymentForm));
            stage.showAndWait();

            // Close the registration window after payment is done
            closeWindow();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de paiement");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}