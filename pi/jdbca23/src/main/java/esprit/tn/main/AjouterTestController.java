package esprit.tn.main;

import esprit.tn.entities.Test;
import esprit.tn.services.ITestService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterTestController {
    @FXML
    private TextField idField;
    @FXML
    private TextField heureDuTestField;
    @FXML
    private TextField nomMatiereField;
    @FXML
    private TextField nomFormateurField;
    @FXML
    private TextField nomSalleField;
    @FXML
    private TextField coefficientField;

    private ITestService testService;
    private ListeTestsController listeController;

    public void setTestService(ITestService testService) {
        this.testService = testService;
    }

    public void setListeController(ListeTestsController listeController) {
        this.listeController = listeController;
    }

    @FXML
    private void handleAjouterTest() {
        String idText = idField.getText().trim();
        String heureDuTest = heureDuTestField.getText().trim();
        String nomMatiere = nomMatiereField.getText().trim();
        String nomFormateur = nomFormateurField.getText().trim();
        String nomSalle = nomSalleField.getText().trim();
        String coefficient = coefficientField.getText().trim();

        if (heureDuTest.isEmpty() || nomMatiere.isEmpty() || nomFormateur.isEmpty() || 
            nomSalle.isEmpty() || coefficient.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            Test test = new Test();
            if (!idText.isEmpty()) {
                test.setId(Integer.parseInt(idText));
            }
            test.setHeureDuTest(Integer.parseInt(heureDuTest));
            test.setNomMatiere(nomMatiere);
            test.setNomFormateur(nomFormateur);
            test.setNomSalle(nomSalle);
            test.setCoefficient(Double.parseDouble(coefficient));

            testService.add(test);

            if (listeController != null) {
                listeController.loadData();
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showError("Format invalide pour les champs numériques. Veuillez vérifier l'heure du test et le coefficient.");
        } catch (Exception e) {
            showError("Erreur lors de l'ajout du test: " + e.getMessage());
        }
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

    private void closeWindow() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}