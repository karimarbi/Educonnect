package esprit.tn.controllers;

import esprit.tn.entities.Test;

import esprit.tn.services.TestService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ModifierTestController {
    @FXML
    private TextField heureDuTestField;
    @FXML
    private TextField nomMatiereField;
    @FXML

    @FXML
    private TextField nomFormateurField;
    @FXML
    private DatePicker jourTestPicker;
    @FXML
    private TextField nomSalleField;
    @FXML
    private TextField coefficientField;

    private Test test;
    private TestService testService;


    public ModifierTestController() {
        testService = new TestService();

    }

    @FXML
    public void initialize() {
        // Configuration du DatePicker
        jourTestPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        loadTypes();
    }

    private void loadTypes() {
        try {
            List<Type> types = typeService.getAll();
            typeComboBox.getItems().clear();
            typeComboBox.getItems().addAll(types);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des types : " + e.getMessage());
        }
    }

    public void setTest(Test test) {
        this.test = test;
        heureDuTestField.setText(String.valueOf(test.getHeureDuTest()));
        nomMatiereField.setText(test.getNomMatiere());
        try {
            Type type = typeService.getById(test.getType_id());
            typeComboBox.setValue(type);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du type: " + e.getMessage());
        }
        nomFormateurField.setText(test.getNomFormateur());
        jourTestPicker.setValue(test.getJourTest());
        nomSalleField.setText(test.getNomSalle());
        coefficientField.setText(String.valueOf(test.getCoefficient()));
    }

    @FXML
    private void handleEnregistrer() {
        if (validateFields()) {
            try {
                updateTestFromFields();
                testService.update(test);
                showSuccess("Test modifié avec succès");
                closeWindow();
            } catch (SQLException e) {
                showError("Erreur lors de la modification : " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showError("Erreur de validation : " + e.getMessage());
            } catch (Exception e) {
                showError("Une erreur inattendue est survenue : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void updateTestFromFields() {
        try {
            int heure = Integer.parseInt(heureDuTestField.getText().trim());
            if (heure < 8 || heure > 17) {
                throw new IllegalArgumentException("L'heure doit être comprise entre 8 et 17");
            }
            test.setHeureDuTest(heure);

            String nomMatiere = nomMatiereField.getText().trim();
            if (nomMatiere.isEmpty()) {
                throw new IllegalArgumentException("Le nom de la matière est requis");
            }
            test.setNomMatiere(nomMatiere);

            Type type = typeComboBox.getValue();
            if (type == null) {
                throw new IllegalArgumentException("Le type est requis");
            }
            test.setType_id(type.getId());

            String formateur = nomFormateurField.getText().trim();
            if (formateur.isEmpty()) {
                throw new IllegalArgumentException("Le nom du formateur est requis");
            }
            test.setNomFormateur(formateur);

            LocalDate jourTest = jourTestPicker.getValue();
            if (jourTest == null) {
                throw new IllegalArgumentException("La date du test est requise");
            }
            if (jourTest.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La date du test ne peut pas être dans le passé");
            }
            test.setJourTest(jourTest);

            String nomSalle = nomSalleField.getText().trim();
            if (nomSalle.isEmpty()) {
                throw new IllegalArgumentException("Le nom de la salle est requis");
            }
            test.setNomSalle(nomSalle);

            double coefficient = Double.parseDouble(coefficientField.getText().trim());
            if (coefficient <= 0) {
                throw new IllegalArgumentException("Le coefficient doit être positif");
            }
            test.setCoefficient(coefficient);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de nombre invalide");
        }
    }

    private boolean validateFields() {
        if (heureDuTestField.getText().trim().isEmpty() || 
            nomMatiereField.getText().trim().isEmpty() ||
            typeComboBox.getValue() == null ||
            nomFormateurField.getText().trim().isEmpty() ||
            jourTestPicker.getValue() == null ||
            nomSalleField.getText().trim().isEmpty() ||
            coefficientField.getText().trim().isEmpty()) {
            showError("Tous les champs sont requis");
            return false;
        }
        return true;
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
        Stage stage = (Stage) heureDuTestField.getScene().getWindow();
        stage.close();
    }
}
