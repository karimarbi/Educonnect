package esprit.tn.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private void handleAjouterType() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/AjouterType.fxml"));
            Parent root = loader.load();
            AjouterTypeController controller = loader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Type");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Si un type a été ajouté, ouvrir la fenêtre des tableaux
            if (controller != null && controller.isTypeAdded()) {
                handleOuvrirTableaux();
            }
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue d'ajout de type.");
        }
    }

    @FXML
    private void handleOuvrirTableaux() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/Tableaux.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Tests et Types");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue des tableaux.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 