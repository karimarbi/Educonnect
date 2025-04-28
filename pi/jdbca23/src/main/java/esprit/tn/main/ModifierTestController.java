package esprit.tn.main;

import esprit.tn.entities.Test;
import esprit.tn.services.ITestService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ModifierTestController {
    @FXML
    private ComboBox<Integer> heureComboBox;
    @FXML
    private TextField matiereTextField;
    @FXML
    private TextField formateurTextField;
    @FXML
    private TextField salleTextField;
    @FXML
    private TextField coefficientTextField;

    private Test test;
    private ITestService testService;
    private ListeTestsController listeController;

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les heures possibles (8-17)
        for (int i = 8; i <= 17; i++) {
            heureComboBox.getItems().add(i);
        }
    }

    public void setTest(Test test) {
        this.test = test;
        heureComboBox.setValue(test.getHeureDuTest());
        matiereTextField.setText(test.getNomMatiere());
        formateurTextField.setText(test.getNomFormateur());
        salleTextField.setText(test.getNomSalle());
        coefficientTextField.setText(String.valueOf(test.getCoefficient()));
    }

    public void setTestService(ITestService testService) {
        this.testService = testService;
    }

    public void setListeController(ListeTestsController listeController) {
        this.listeController = listeController;
    }

    @FXML
    private void handleModifier() {
        if (!validateFields()) {
            return;
        }

        try {
            updateTestFromFields();
            testService.update(test);
            
            if (listeController != null) {
                listeController.loadData();
            }
            showSuccess("Test modifié avec succès");
            closeWindow();
        } catch (NumberFormatException e) {
            showError("Format invalide pour les champs numériques. Veuillez vérifier l'heure du test et le coefficient.");
        } catch (SQLException e) {
            showError("Erreur lors de la modification du test : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (heureComboBox.getValue() == null || 
            matiereTextField.getText().trim().isEmpty() || 
            formateurTextField.getText().trim().isEmpty() || 
            salleTextField.getText().trim().isEmpty() || 
            coefficientTextField.getText().trim().isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires");
            return false;
        }
        return true;
    }

    private void updateTestFromFields() {
        int heure = heureComboBox.getValue();
        if (heure < 8 || heure > 17) {
            throw new IllegalArgumentException("L'heure du test doit être entre 8 et 17");
        }
        test.setHeureDuTest(heure);

        test.setNomMatiere(matiereTextField.getText().trim());
        test.setNomFormateur(formateurTextField.getText().trim());
        test.setNomSalle(salleTextField.getText().trim());

        double coefficient = Double.parseDouble(coefficientTextField.getText().trim());
        if (coefficient <= 0) {
            throw new IllegalArgumentException("Le coefficient doit être positif");
        }
        test.setCoefficient(coefficient);
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) matiereTextField.getScene().getWindow();
        stage.close();
    }
}