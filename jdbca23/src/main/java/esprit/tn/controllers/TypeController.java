package esprit.tn.controllers;

import esprit.tn.entities.Type;
import esprit.tn.services.TypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TypeController {
    @FXML
    private TableView<Type> typeTable;
    @FXML
    private TableColumn<Type, Integer> idColumn;
    @FXML
    private TableColumn<Type, String> modeColumn;
    @FXML
    private TextField modeField;

    private TypeService typeService = new TypeService();
    private ObservableList<Type> typeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        String mode = modeField.getText();

        if (mode.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir le champ mode");
            return;
        }

        Type type = new Type();
        type.setMode(mode);

        typeService.add(type);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Type selectedType = typeTable.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showAlert("Erreur", "Veuillez sélectionner un type à modifier");
            return;
        }

        String mode = modeField.getText();

        if (mode.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir le champ mode");
            return;
        }

        selectedType.setMode(mode);

        typeService.update(selectedType);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleDelete() {
        Type selectedType = typeTable.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showAlert("Erreur", "Veuillez sélectionner un type à supprimer");
            return;
        }

        typeService.delete(selectedType.getId());
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    private void refreshTable() {
        typeList.clear();
        typeList.addAll(typeService.getAll());
        typeTable.setItems(typeList);
    }

    private void clearFields() {
        modeField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 