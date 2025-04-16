package esprit.tn.main;

import esprit.tn.entities.Type;
import esprit.tn.services.ITypeService;
import esprit.tn.services.TypeService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * Classe principale pour l'application JavaFX
 */
public class MainFX extends Application {
    
    private VBox typeBox;
    private ObservableList<Type> typeList;
    private ITypeService typeService;
    
    /**
     * Méthode appelée au démarrage de l'application
     * @param primaryStage La fenêtre principale de l'application
     * @throws Exception En cas d'erreur lors du chargement du FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialiser le service de types
        typeService = new TypeService();
        typeList = FXCollections.observableArrayList();
        
        // Créer un conteneur HBox pour aligner les tableaux horizontalement
        HBox root = new HBox(20); // 20 pixels d'espacement entre les tableaux
        root.setPadding(new Insets(10));
        
        // Charger le tableau des tests
        FXMLLoader testLoader = new FXMLLoader(getClass().getResource("/ListeTests.fxml"));
        Parent testRoot = testLoader.load();
        
        // Récupérer le contrôleur et lui passer une référence à MainFX
        ListeTestsController testController = testLoader.getController();
        testController.setMainFX(this);
        
        // Créer le tableau des types
        typeBox = createTypeTable();
        
        // Ajouter les deux tableaux au conteneur HBox
        root.getChildren().addAll(testRoot, typeBox);
        
        // Créer la scène avec le conteneur HBox
        Scene scene = new Scene(root);
        
        // Configurer la fenêtre principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Tests et Types");
        primaryStage.setResizable(false);
        
        // Afficher la fenêtre
        primaryStage.show();
    }
    
    /**
     * Crée un tableau pour afficher les types
     * @return Un VBox contenant le tableau des types
     */
    private VBox createTypeTable() {
        VBox typeBox = new VBox(10);
        typeBox.setPadding(new Insets(10));
        
        // Créer le tableau des types
        TableView<Type> typeTable = new TableView<>();
        
        // Créer les colonnes
        TableColumn<Type, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);
        
        TableColumn<Type, String> modeColumn = new TableColumn<>("Mode");
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        modeColumn.setPrefWidth(200);
        
        // Ajouter les colonnes au tableau
        typeTable.getColumns().addAll(idColumn, modeColumn);
        
        // Charger les données
        try {
            List<Type> types = typeService.getAll();
            typeList.addAll(types);
            typeTable.setItems(typeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Créer les boutons
        HBox buttonBox = new HBox(10);
        Button refreshButton = new Button("Rafraîchir");
        Button addButton = new Button("Ajouter");
        Button deleteButton = new Button("Supprimer");
        
        refreshButton.setOnAction(e -> refreshTypeTable());
        
        addButton.setOnAction(e -> openAddTypeDialog());
        
        deleteButton.setOnAction(e -> {
            Type selectedType = typeTable.getSelectionModel().getSelectedItem();
            if (selectedType != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Êtes-vous sûr de vouloir supprimer ce type ?");
                
                if (alert.showAndWait().get() == ButtonType.OK) {
                    try {
                        typeService.delete(selectedType.getId());
                        typeList.remove(selectedType);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        buttonBox.getChildren().addAll(refreshButton, addButton, deleteButton);
        
        // Ajouter le tableau et les boutons au VBox
        typeBox.getChildren().addAll(
            new Label("Types"),
            typeTable,
            buttonBox
        );
        
        return typeBox;
    }
    
    /**
     * Rafraîchit le tableau des types
     */
    public void refreshTypeTable() {
        try {
            typeList.clear();
            typeList.addAll(typeService.getAll());
        } catch (Exception e) {
            e.printStackTrace();
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
            
            // Rafraîchir le tableau après l'ajout
            refreshTypeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée de l'application
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}
