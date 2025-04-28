package com.esprit.Controllers.Aziz;

import com.esprit.Models.Reclamation;
import com.esprit.Models.Reponse;
import com.esprit.Services.ReclamationService;
import com.esprit.Services.ReponseService;
import com.esprit.utils.NotificationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReclamationController {
    @FXML private ListView<Reclamation> reclamationsList;
    @FXML private TextArea messageField;
    @FXML private ListView<Reponse> reponsesList;
    @FXML private TextArea reponseField;
    @FXML private TextField searchField;

    private final ReclamationService reclamationService = new ReclamationService();
    private final ReponseService reponseService = new ReponseService();
    private final ObservableList<Reclamation> observableReclamations = FXCollections.observableArrayList();
    private final ObservableList<Reponse> observableReponses = FXCollections.observableArrayList();
    private List<Reclamation> allReclamations;

    @FXML
    public void initialize() {
        // Configure list views
        reclamationsList.setItems(observableReclamations);
        reponsesList.setItems(observableReponses);

        // Set cell factories
        reclamationsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMessage() + " (" + item.getDateCreation() + ")");
                }
            }
        });

        reponsesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getContenu() + " (" + item.getDateCreation() + ")");
                }
            }
        });

        // Load data
        refreshReclamations();

        // Selection listener
        reclamationsList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        messageField.setText(newVal.getMessage());
                        showReponses(newVal);
                    }
                });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            observableReclamations.setAll(allReclamations);
        } else {
            List<Reclamation> filtered = allReclamations.stream()
                    .filter(r -> r.getMessage().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            observableReclamations.setAll(filtered);
        }
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        observableReclamations.setAll(allReclamations);
    }

    @FXML
    private void handleAddReclamation() {
        String message = messageField.getText().trim();

        if (!message.isEmpty()) {
            Reclamation newReclamation = new Reclamation(message, LocalDateTime.now());
            reclamationService.addReclamation(newReclamation);

            NotificationHelper.showNotification("Réclamation Ajoutée", "Votre réclamation a été ajoutée avec succès!");

            refreshReclamations();
            messageField.clear();
        } else {
            showAlert("Erreur", "Veuillez entrer un message pour la réclamation");
        }
    }

    @FXML
    private void handleUpdateReclamation() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        String message = messageField.getText().trim();

        if (selected != null && !message.isEmpty()) {
            selected.setMessage(message);
            reclamationService.updateReclamation(selected);

            NotificationHelper.showNotification("Réclamation Modifiée", "La réclamation a été modifiée avec succès!");

            refreshReclamations();
        } else {
            showAlert("Error", "Please select a reclamation and enter a new message");
        }
    }

    @FXML
    private void handleDeleteReclamation() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reclamationService.deleteReclamation(selected.getId());

            NotificationHelper.showNotification("Réclamation Supprimée", "La réclamation a été supprimée avec succès!");

            messageField.clear();
            refreshReclamations();
            observableReponses.clear();
        }
    }

    @FXML
    private void handleAddReponse() {
        Reclamation selected = reclamationsList.getSelectionModel().getSelectedItem();
        String contenu = reponseField.getText().trim();

        if (selected != null && !contenu.isEmpty()) {
            Reponse newReponse = new Reponse(contenu, LocalDateTime.now(), selected);
            reponseService.addReponse(newReponse);

            NotificationHelper.showNotification("Réponse Ajoutée", "Votre réponse a été ajoutée avec succès!");

            reponseField.clear();
            showReponses(selected);
        } else {
            showAlert("Error", "Please select a reclamation and enter a response");
        }
    }

    public void refreshReclamations() {
        allReclamations = reclamationService.getAllReclamations();
        observableReclamations.setAll(allReclamations);
    }

    private void showReponses(Reclamation reclamation) {
        if (reclamation != null) {
            observableReponses.setAll(reponseService.getReponsesByReclamation(reclamation.getId()));
        } else {
            observableReponses.clear();
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