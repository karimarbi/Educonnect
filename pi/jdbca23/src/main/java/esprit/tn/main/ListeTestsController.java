package esprit.tn.main;

import esprit.tn.entities.Test;
import esprit.tn.services.TestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ListeTestsController {
    @FXML private TableView<Test> testTable;
    @FXML private TableColumn<Test, Integer> idTest;
    @FXML private TableColumn<Test, Integer> heureDuTest;
    @FXML private TableColumn<Test, String> nomMatiere;
    @FXML private TableColumn<Test, String> nomFormateur;
    @FXML private TableColumn<Test, String> nomSalle;
    @FXML private TableColumn<Test, Double> coefficient;

    private TestService testService;
    private ObservableList<Test> testList;

    public ListeTestsController() {
        testService = new TestService();
        testList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idTest.setCellValueFactory(new PropertyValueFactory<>("id"));
        heureDuTest.setCellValueFactory(new PropertyValueFactory<>("heureDuTest"));
        nomMatiere.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        nomFormateur.setCellValueFactory(new PropertyValueFactory<>("nomFormateur"));
        nomSalle.setCellValueFactory(new PropertyValueFactory<>("nomSalle"));
        coefficient.setCellValueFactory(new PropertyValueFactory<>("coefficient"));

        // Format de l'heure
        heureDuTest.setCellFactory(column -> new TableCell<Test, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%02dh00", item));
                }
            }
        });

        // Double-clic pour modifier
        testTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleModifierTest();
            }
        });
    }

    @FXML
    private void handleAjouterTest() {
        openWindow("/esprit/tn/views/AjouterTest.fxml", "Ajouter un test");
    }

    @FXML
    private void handleModifierTest() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showError("Veuillez sélectionner un test à modifier");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/ModifierTest.fxml"));
            Parent root = loader.load();

            ModifierTestController controller = loader.getController();
            controller.setTest(selectedTest);
            controller.setTestService(testService);
            controller.setListeController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier un test");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerTest() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showError("Veuillez sélectionner un test à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce test ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                testService.delete(selectedTest.getId());
                showSuccess("Test supprimé avec succès");
                loadData();
            } catch (SQLException e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    public void loadData() {
        try {
            testList.clear();
            testList.addAll(testService.getAll());
            testTable.setItems(testList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData(); // Rafraîchir les données après la fermeture de la fenêtre
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la fenêtre : " + e.getMessage());
        }
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
}