package esprit.tn.controllers;

import esprit.tn.entities.Test;
import esprit.tn.entities.Type;
import esprit.tn.services.TestService;
import esprit.tn.services.TypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AjouterTestAvecTypeController {
    @FXML
    private ComboBox<String> modeComboBox;
    @FXML
    private TextField heureDuTestField;
    @FXML
    private ComboBox<String> matiereComboBox;
    @FXML
    private TableView<Type> Types;
    @FXML
    private TableColumn<Type, Integer> idCol;
    @FXML
    private TableColumn<Type, String> modeCol;
    @FXML
    private VBox typeSection;
    @FXML
    private VBox testSection;

    private TypeService typeService = new TypeService();
    private TestService testService = new TestService();
    private ObservableList<Type> typeList = FXCollections.observableArrayList();
    private Type selectedType;

    @FXML
    public void initialize() {
        // Initialiser le ComboBox des modes
        modeComboBox.getItems().addAll("présentielle", "en ligne");
        
        // Initialiser le ComboBox des matières
        matiereComboBox.getItems().addAll("math", "physique", "science", "anglais", "francais");
        
        // Initialiser les colonnes du tableau
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        modeCol.setCellValueFactory(new PropertyValueFactory<>("mode"));
        
        // Charger les types existants
        refreshTypeTable();
        
        // Écouter la sélection dans le tableau
        Types.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedType = newSelection;
        });

        // Ajouter un écouteur pour valider l'heure en temps réel
        heureDuTestField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                heureDuTestField.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (!newValue.isEmpty()) {
                int heure = Integer.parseInt(newValue);
                if (heure < 8 || heure > 17) {
                    heureDuTestField.setStyle("-fx-border-color: red;");
                } else {
                    heureDuTestField.setStyle("");
                }
            }
        });
        
        // Désactiver la section test par défaut
        testSection.setVisible(false);
    }

    private void refreshTypeTable() {
        typeList.clear();
        typeList.addAll(typeService.getAll());
        Types.setItems(typeList);
    }

    @FXML
    private void handleAjouterType() {
        String mode = modeComboBox.getValue();
        if (mode == null || mode.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un mode (présentielle ou en ligne)");
            return;
        }

        try {
            // Créer et sauvegarder le type
            Type type = new Type();
            type.setMode(mode);
            typeService.add(type);
            
            // Rafraîchir et sélectionner dans le tableau
            refreshTypeTable();
            Types.getSelectionModel().select(type);
            selectedType = type;
            
            // Passer à l'étape d'ajout du test
            typeSection.setVisible(false);
            testSection.setVisible(true);
            
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterTest() {
        String heureStr = heureDuTestField.getText();
        String matiere = matiereComboBox.getValue();

        // Vérifier si un type est sélectionné
        if (selectedType == null) {
            showAlert("Erreur", "Aucun type n'a été sélectionné");
            return;
        }

        // Vérifier si tous les champs sont remplis
        if (heureStr.isEmpty() || matiere == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs du test");
            return;
        }

        // Valider l'heure
        try {
            int heure = Integer.parseInt(heureStr);
            if (heure < 8 || heure > 17) {
                showAlert("Erreur", "L'heure doit être comprise entre 8 et 17");
                return;
            }

            // Créer et sauvegarder le test
            Test test = new Test();
            test.setHeureDuTest(heureStr);
            test.setNomMatiere(matiere);
            test.setType(selectedType);
            testService.add(test);

            showAlert("Succès", "Test ajouté avec succès");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'heure doit être un nombre entier");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) modeComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}