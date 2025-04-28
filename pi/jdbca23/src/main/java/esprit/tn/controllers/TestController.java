package esprit.tn.controllers;

import esprit.tn.models.Test;
import esprit.tn.models.Type;
import esprit.tn.services.ITestService;
import esprit.tn.services.ITypeService;
import esprit.tn.services.TestService;
import esprit.tn.services.TypeService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TestController implements Initializable {
    @FXML private TableView<Test> tableView;
    @FXML private TableColumn<Test, Integer> colId;
    @FXML private TableColumn<Test, Integer> colHeure;
    @FXML private TableColumn<Test, String> colMatiere;
    @FXML private TableColumn<Test, String> colType;
    
    @FXML private Spinner<Integer> spHeure;
    @FXML private TextField tfMatiere;
    @FXML private ComboBox<Type> cbType;
    
    private final ITestService testService;
    private final ITypeService typeService;
    private Test selectedTest;
    
    public TestController() {
        testService = new TestService();
        typeService = new TypeService();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupSpinner();
        loadTypes();
        loadTests();
    }
    
    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureDuTest"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        colType.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getType().getMode()
            )
        );
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTest = newSelection;
            if (selectedTest != null) {
                spHeure.getValueFactory().setValue(selectedTest.getHeureDuTest());
                tfMatiere.setText(selectedTest.getNomMatiere());
                cbType.setValue(selectedTest.getType());
            }
        });
    }
    
    private void setupSpinner() {
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 17, 8);
        spHeure.setValueFactory(valueFactory);
    }
    
    private void loadTypes() {
        try {
            cbType.setItems(FXCollections.observableArrayList(typeService.getAll()));
        } catch (SQLException e) {
            showError("Error loading types", e);
        }
    }
    
    private void loadTests() {
        try {
            tableView.setItems(FXCollections.observableArrayList(testService.getAll()));
        } catch (SQLException e) {
            showError("Error loading tests", e);
        }
    }
    
    @FXML
    private void handleAdd() {
        if (!validateInput()) return;
        
        try {
            Test test = new Test(
                spHeure.getValue(),
                tfMatiere.getText(),
                cbType.getValue()
            );
            testService.add(test);
            loadTests();
            clearFields();
        } catch (SQLException e) {
            showError("Error adding test", e);
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedTest == null || !validateInput()) return;
        
        try {
            selectedTest.setHeureDuTest(spHeure.getValue());
            selectedTest.setNomMatiere(tfMatiere.getText());
            selectedTest.setType(cbType.getValue());
            testService.update(selectedTest);
            loadTests();
            clearFields();
        } catch (SQLException e) {
            showError("Error updating test", e);
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedTest == null) return;
        
        try {
            testService.delete(selectedTest.getId());
            loadTests();
            clearFields();
        } catch (SQLException e) {
            showError("Error deleting test", e);
        }
    }
    
    private boolean validateInput() {
        if (tfMatiere.getText().trim().isEmpty()) {
            showError("Validation Error", "Please enter a subject name");
            return false;
        }
        if (cbType.getValue() == null) {
            showError("Validation Error", "Please select a type");
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        spHeure.getValueFactory().setValue(8);
        tfMatiere.clear();
        cbType.setValue(null);
        selectedTest = null;
        tableView.getSelectionModel().clearSelection();
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}