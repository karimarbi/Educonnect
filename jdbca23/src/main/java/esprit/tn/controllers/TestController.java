package esprit.tn.controllers;

import esprit.tn.entities.Test;
import esprit.tn.services.TestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TestController {
    @FXML
    private TableView<Test> testTable;
    @FXML
    private TableColumn<Test, Integer> idColumn;
    @FXML
    private TableColumn<Test, String> heureDuTestColumn;
    @FXML
    private TableColumn<Test, String> nomMatiereColumn;
    @FXML
    private TextField heureDuTestField;
    @FXML
    private TextField nomMatiereField;

    private TestService testService = new TestService();
    private ObservableList<Test> testList = FXCollections.observableArrayList();
    private Runnable onTestAdded;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        heureDuTestColumn.setCellValueFactory(new PropertyValueFactory<>("heureDuTest"));
        nomMatiereColumn.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        
        refreshTable();
    }

    public void setOnTestAdded(Runnable onTestAdded) {
        this.onTestAdded = onTestAdded;
    }

    @FXML
    private void handleAdd() {
        String heureDuTest = heureDuTestField.getText();
        String nomMatiere = nomMatiereField.getText();

        if (heureDuTest.isEmpty() || nomMatiere.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        Test test = new Test();
        test.setHeureDuTest(heureDuTest);
        test.setNomMatiere(nomMatiere);

        testService.add(test);
        refreshTable();
        clearFields();

        // Notifier que un test a été ajouté
        if (onTestAdded != null) {
            onTestAdded.run();
        }
    }

    @FXML
    private void handleUpdate() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showAlert("Erreur", "Veuillez sélectionner un test à modifier");
            return;
        }

        String heureDuTest = heureDuTestField.getText();
        String nomMatiere = nomMatiereField.getText();

        if (heureDuTest.isEmpty() || nomMatiere.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        selectedTest.setHeureDuTest(heureDuTest);
        selectedTest.setNomMatiere(nomMatiere);

        testService.update(selectedTest);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleDelete() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            showAlert("Erreur", "Veuillez sélectionner un test à supprimer");
            return;
        }

        testService.delete(selectedTest.getId());
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    private void refreshTable() {
        testList.clear();
        testList.addAll(testService.getAll());
        testTable.setItems(testList);
    }

    private void clearFields() {
        heureDuTestField.clear();
        nomMatiereField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 