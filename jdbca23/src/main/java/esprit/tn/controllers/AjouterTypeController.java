package esprit.tn.controllers;

import esprit.tn.entities.Type;
import esprit.tn.services.TypeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class AjouterTypeController {
    @FXML
    private ComboBox<String> modeComboBox;
    
    private TypeService typeService = new TypeService();
    private boolean typeAdded = false;
    private Type lastAddedType;

    @FXML
    public void initialize() {
        modeComboBox.getItems().addAll("présentielle", "en ligne");
    }

    @FXML
    private void handleAjouter() {
        String mode = modeComboBox.getValue();

        if (mode == null) {
            showAlert("Erreur", "Veuillez sélectionner un mode");
            return;
        }

        Type type = new Type();
        type.setMode(mode);

        typeService.add(type);
        typeAdded = true;
        lastAddedType = type;
        
        // Fermer la fenêtre après l'ajout du type
        closeWindow();
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) modeComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public boolean isTypeAdded() {
        return typeAdded;
    }
    
    public Type getLastAddedType() {
        return lastAddedType;
    }
}