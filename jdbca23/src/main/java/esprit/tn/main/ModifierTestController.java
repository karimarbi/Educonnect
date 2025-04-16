package esprit.tn.main;

import esprit.tn.entities.Test;
import esprit.tn.services.ITestService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ModifierTestController {
    @FXML
    private TextField heureDuTestField;
    @FXML
    private TextField nomMatiereField;

    private Test test;
    private ITestService testService;
    private ListeTestsController listeController;

    public void setTest(Test test) {
        this.test = test;
        heureDuTestField.setText(test.getHeureDuTest());
        nomMatiereField.setText(test.getNomMatiere());
    }

    public void setTestService(ITestService testService) {
        this.testService = testService;
    }

    public void setListeController(ListeTestsController listeController) {
        this.listeController = listeController;
    }

    @FXML
    private void handleModifier() {
        String heureDuTest = heureDuTestField.getText();
        String nomMatiere = nomMatiereField.getText();

        if (heureDuTest.isEmpty() || nomMatiere.isEmpty()) {
            showError("Tous les champs sont obligatoires");
            return;
        }

        test.setHeureDuTest(heureDuTest);
        test.setNomMatiere(nomMatiere);
        try {
            testService.update(test);
            // Fermer la fenêtre
            Stage stage = (Stage) heureDuTestField.getScene().getWindow();
            stage.close();

            // Rafraîchir la liste
            listeController.refreshTable();
        } catch (Exception e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 