package esprit.tn.main;

import esprit.tn.entities.Test;
import esprit.tn.entities.Type;
import esprit.tn.services.ITestService;
import esprit.tn.services.TestService;
import esprit.tn.controllers.AjouterTypeController;
import esprit.tn.controllers.AjouterTestAvecTypeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListeTestsController {
    @FXML
    private TableView<Test> testTable;
    @FXML
    private TableColumn<Test, Integer> idColumn;
    @FXML
    private TableColumn<Test, String> heureDuTestColumn;
    @FXML
    private TableColumn<Test, String> nomMatiereColumn;
    @FXML
    private TextField searchField;

    private ITestService testService = new TestService();
    private ObservableList<Test> testList = FXCollections.observableArrayList();
    private FilteredList<Test> filteredData;
    private Runnable onTestAdded;
    private MainFX mainFX;

    public void setMainFX(MainFX mainFX) {
        this.mainFX = mainFX;
    }

    public void setOnTestAdded(Runnable onTestAdded) {
        this.onTestAdded = onTestAdded;
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation de ListeTestsController");
        // Configuration des colonnes avec les noms exacts des getters
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        heureDuTestColumn.setCellValueFactory(new PropertyValueFactory<>("heureDuTest"));
        nomMatiereColumn.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));

        // Configuration de la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredData != null) {
                filteredData.setPredicate(test -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return test.getNomMatiere().toLowerCase().contains(lowerCaseFilter) ||
                           test.getHeureDuTest().toLowerCase().contains(lowerCaseFilter);
                });
            }
        });

        refreshTable();
    }

    public void refreshTable() {
        System.out.println("Début du rafraîchissement de la table");
        testList.clear();
        try {
            List<Test> tests = testService.getAll();
            System.out.println("Nombre de tests récupérés: " + tests.size());
            testList.addAll(tests);
            filteredData = new FilteredList<>(testList, p -> true);
            SortedList<Test> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(testTable.comparatorProperty());
            testTable.setItems(sortedData);
            System.out.println("Table rafraîchie avec succès");

            // Notifier que la table a été rafraîchie
            if (onTestAdded != null) {
                onTestAdded.run();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du rafraîchissement de la liste: " + e.getMessage());
        }
    }

    @FXML
    private void handleNouveauTest() {
        System.out.println("Ouverture de la fenêtre Nouveau Test");
        try {
            FXMLLoader loader = new FXMLLoader(ListeTestsController.class.getResource("/esprit/tn/views/AjouterTestAvecType.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Test");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            refreshTable();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la fenêtre: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre la fenêtre d'ajout de type
     */
    private void openAddTypeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/AjouterType.fxml"));
            Parent typeRoot = loader.load();
            Stage typeStage = new Stage();
            typeStage.setTitle("Ajouter un Type");
            typeStage.setScene(new Scene(typeRoot));
            typeStage.initModality(Modality.APPLICATION_MODAL);
            
            typeStage.showAndWait();
            
            // Rafraîchir la table après l'ajout
            refreshTable();
            
            // Rafraîchir le tableau des types
            if (mainFX != null) {
                mainFX.refreshTypeTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la fenêtre d'ajout de type: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showError("Veuillez sélectionner un test à modifier");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierTest.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Modifier Test");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            ModifierTestController controller = loader.getController();
            controller.setTest(selectedTest);
            controller.setTestService(testService);
            controller.setListeController(this);
            
            stage.showAndWait();
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la fenêtre: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showError("Veuillez sélectionner un test à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce test ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                testService.delete(selectedTest.getId());
                refreshTable();
            } catch (Exception e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
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