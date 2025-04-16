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

    private ITestService testService;
    private ListeTestsController listeController;

    public void setTestService(ITestService testService) {
        this.testService = testService;
        System.out.println("TestService défini dans AjouterTestController");
    }

    public void setListeController(ListeTestsController listeController) {
        this.listeController = listeController;
        System.out.println("ListeController défini dans AjouterTestController");
    }

    @FXML
    private void handleAjouter() {
        System.out.println("Début de handleAjouter");
        String idText = idField.getText();
        String heureDuTest = heureDuTestField.getText();
        String nomMatiere = nomMatiereField.getText();

        System.out.println("Valeurs saisies - ID: " + idText + ", Heure: " + heureDuTest + ", Matière: " + nomMatiere);

        if (heureDuTest.isEmpty() || nomMatiere.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            Test test = new Test();
            if (!idText.isEmpty()) {
                test.setId(Integer.parseInt(idText));
            }
            test.setHeureDuTest(heureDuTest);
            test.setNomMatiere(nomMatiere);

            System.out.println("Test créé: " + test);
            testService.add(test);
            System.out.println("Test ajouté à la base de données");

            if (listeController != null) {
                System.out.println("Rafraîchissement de la liste");
                listeController.refreshTable();
            } else {
                System.out.println("ListeController est null!");
            }
            closeWindow();
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format de nombre: " + e.getMessage());
            showError("L'ID doit être un nombre valide");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
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