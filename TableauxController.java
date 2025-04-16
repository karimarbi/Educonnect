package esprit.tn.controllers;

import esprit.tn.entities.Test;
import esprit.tn.entities.Type;
import esprit.tn.services.TestService;
import esprit.tn.services.TypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TableauxController {
    @FXML
    private TableView<Test> testTable;
    @FXML
    private TableColumn<Test, Integer> idColumn;
    @FXML
    private TableColumn<Test, String> heureDuTestColumn;
    @FXML
    private TableColumn<Test, String> nomMatiereColumn;
    @FXML
    private TableColumn<Test, String> typeColumn;
    
    @FXML
    private TableView<Type> typeTable;
    @FXML
    private TableColumn<Type, Integer> typeIdColumn;
    @FXML
    private TableColumn<Type, String> modeColumn;

    private TestService testService;
    private TypeService typeService;
    private ObservableList<Test> testList;
    private ObservableList<Type> typeList;

    @FXML
    public void initialize() {
        testService = new TestService();
        typeService = new TypeService();
        testList = FXCollections.observableArrayList();
        typeList = FXCollections.observableArrayList();

        // Configuration des colonnes de la table des tests
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        heureDuTestColumn.setCellValueFactory(new PropertyValueFactory<>("heureDuTest"));
        nomMatiereColumn.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        typeColumn.setCellValueFactory(cellData -> {
            Type type = cellData.getValue().getType();
            return type != null ? new SimpleStringProperty(type.getMode()) : new SimpleStringProperty("");
        });
        
        // Configuration des colonnes de la table des types
        typeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        
        // Chargement initial des données
        refreshTables();
    }

    @FXML
    private void handleAjouterType() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/AjouterType.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Type");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            AjouterTypeController controller = loader.getController();
            
            stage.showAndWait();
            
            if (controller.isTypeAdded()) {
                refreshTables();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterTest() {
        try {
            // D'abord, ouvrir la fenêtre d'ajout de type
            FXMLLoader typeLoader = new FXMLLoader(getClass().getResource("/esprit/tn/views/AjouterType.fxml"));
            Parent typeRoot = typeLoader.load();
            Stage typeStage = new Stage();
            typeStage.setTitle("Ajouter un Type");
            typeStage.setScene(new Scene(typeRoot));
            typeStage.initModality(Modality.APPLICATION_MODAL);
            
            AjouterTypeController typeController = typeLoader.getController();
            typeStage.showAndWait();
            
            // Rafraîchir les tables après l'ajout du type
            refreshTables();
            
            // Ensuite, ouvrir la fenêtre d'ajout de test
            FXMLLoader testLoader = new FXMLLoader(getClass().getResource("/esprit/tn/views/AjouterTestAvecType.fxml"));
            Parent testRoot = testLoader.load();
            Stage testStage = new Stage();
            testStage.setTitle("Ajouter un Test");
            testStage.setScene(new Scene(testRoot));
            testStage.initModality(Modality.APPLICATION_MODAL);
            
            testStage.showAndWait();
            refreshTables();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        refreshTables();
    }

    private void refreshTables() {
        testList.clear();
        typeList.clear();
        testList.addAll(testService.getAll());
        typeList.addAll(typeService.getAll());
        
        testTable.setItems(testList);
        typeTable.setItems(typeList);
    }

    @FXML
    private void handleDeleteTest() {
        Test selectedTest = testTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            testService.delete(selectedTest.getId());
            refreshTables();
        } else {
            showAlert("Attention", "Veuillez sélectionner un test à supprimer.");
        }
    }

    @FXML
    private void handleDeleteType() {
        Type selectedType = typeTable.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            typeService.delete(selectedType.getId());
            refreshTables();
        } else {
            showAlert("Attention", "Veuillez sélectionner un type à supprimer.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 