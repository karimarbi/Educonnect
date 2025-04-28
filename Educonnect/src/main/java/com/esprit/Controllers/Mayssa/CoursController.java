package com.esprit.Controllers.Mayssa;

import com.esprit.Models.*;
import com.esprit.Services.*;
import com.esprit.exceptions.ValidationException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class CoursController {
    // Form controls
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea contenuArea;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private TextField imagePathField;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button exportPdfButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<Categorie> filterCategoryCombo;

    // Table components
    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, String> titreColumn;
    @FXML private TableColumn<Cours, String> descriptionColumn;
    @FXML private TableColumn<Cours, String> contenuColumn;
    @FXML private TableColumn<Cours, String> categorieColumn;
    @FXML private TableColumn<Cours, String> imageColumn;

    private final CoursService coursService = new CoursService();
    private final CategorieService categorieService = new CategorieService();
    private FileChooser fileChooser;
    private File selectedImageFile;
    private Cours selectedCours;
    private ObservableList<Cours> coursList = FXCollections.observableArrayList();
    private ObservableList<Categorie> categories = FXCollections.observableArrayList();
    private FilteredList<Cours> filteredData;

    @FXML
    public void initialize() {
        initializeForm();
        initializeTableColumns();
        setupSearchAndFilter();
        refreshTable();
        setupTableSelectionListener();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void initializeForm() {
        categories.setAll(categorieService.getAllCategories());
        categorieCombo.setItems(categories);
        filterCategoryCombo.setItems(FXCollections.observableArrayList(categories));
        filterCategoryCombo.getItems().add(0, new Categorie(0, "All Categories")); // Add "All" option
        filterCategoryCombo.getSelectionModel().selectFirst();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        imagePreview.setImage(null);
    }

    private void setupSearchAndFilter() {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data)
        filteredData = new FilteredList<>(coursList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        filterCategoryCombo.valueProperty().addListener((observable, oldValue, newValue) -> updateFilters());

        // 3. Wrap the FilteredList in a SortedList
        SortedList<Cours> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(coursTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table
        coursTable.setItems(sortedData);
    }

    private void updateFilters() {
        String searchText = searchField.getText().toLowerCase();
        Categorie selectedCategory = filterCategoryCombo.getValue();

        filteredData.setPredicate(cours -> {
            // Check search text
            boolean matchesSearch = searchText.isEmpty() ||
                    cours.getTitre().toLowerCase().contains(searchText) ||
                    cours.getDescription().toLowerCase().contains(searchText) ||
                    cours.getContenu().toLowerCase().contains(searchText);

            // Check category filter
            boolean matchesCategory = selectedCategory == null ||
                    selectedCategory.getId() == 0 || // "All Categories" selected
                    (cours.getCategorie() != null &&
                            cours.getCategorie().getId() == selectedCategory.getId());

            return matchesSearch && matchesCategory;
        });
    }

    private void initializeTableColumns() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        categorieColumn.setCellValueFactory(cellData -> {
            Categorie categorie = cellData.getValue().getCategorie();
            return new SimpleStringProperty(categorie != null ? categorie.getNomCategorie() : "");
        });
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        // Set up text wrapping for description and content columns
        descriptionColumn.setCellFactory(tc -> new TextWrappingTableCell());
        contenuColumn.setCellFactory(tc -> new TextWrappingTableCell());

        // Set up image display in table
        imageColumn.setCellFactory(tc -> new TableCell<Cours, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        File imageFile = new File(CoursService.UPLOAD_DIR + imagePath);
                        if (imageFile.exists()) {
                            imageView.setImage(new Image(imageFile.toURI().toString()));
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    // Custom TableCell for text wrapping
    private static class TextWrappingTableCell extends TableCell<Cours, String> {
        private final Text text;
        private final HBox container;

        public TextWrappingTableCell() {
            text = new Text();
            container = new HBox(text);
            container.setStyle("-fx-padding: 5px;");
            text.wrappingWidthProperty().bind(widthProperty().subtract(15));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setPrefHeight(Control.USE_COMPUTED_SIZE);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setTooltip(null);
            } else {
                text.setText(item);
                setGraphic(container);
                setTooltip(new Tooltip(item));
            }
        }
    }

    private void setupTableSelectionListener() {
        coursTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedCours = newSelection;
                    if (newSelection != null) {
                        setFormData(newSelection);
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearForm();
                    }
                }
        );
    }

    @FXML
    private void handleSave() {
        try {
            Cours cours = createCoursFromForm();

            // Debug selected category
            System.out.println("Attempting to save with category: " +
                    cours.getCategorie().getId() + " - " + cours.getCategorie().getNomCategorie());

            coursService.addCours(cours);
            showAlert("Success", "Course created successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save course: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    private Cours createCoursFromForm() throws ValidationException, IOException {
        validateFields();

        Cours cours = new Cours();
        cours.setTitre(titreField.getText().trim());
        cours.setDescription(descriptionArea.getText().trim());
        cours.setContenu(contenuArea.getText().trim());

        // Get the selected category from ComboBox
        Categorie selectedCategory = categorieCombo.getValue();
        if (selectedCategory == null || selectedCategory.getId() == 0) {
            throw new ValidationException("Please select a valid category");
        }
        cours.setCategorie(selectedCategory);

        if (selectedImageFile != null) {
            cours.setImageFile(selectedImageFile);
        }

        return cours;
    }

    @FXML
    private void handleUpdate() {
        if (selectedCours == null) {
            showAlert("Error", "No course selected to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            Cours updatedCours = createCoursFromForm();
            updatedCours.setId(selectedCours.getId());
            coursService.updateCours(updatedCours);
            showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
            refreshTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update course: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCours == null) {
            showAlert("Error", "No course selected to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Course");
        confirmation.setContentText("Are you sure you want to delete this course?");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    coursService.deleteCours(selectedCours.getId());
                    showAlert("Success", "Course deleted successfully", Alert.AlertType.INFORMATION);
                    refreshTable();
                    clearForm();
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete course: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleUpload() {
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                selectedImageFile = file;
                imagePathField.setText(file.getName());
                imagePreview.setImage(new Image(file.toURI().toString()));
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleExportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());

        if (file != null) {
            try {
                exportTableToPdf(file);
                showAlert("Success", "PDF exported successfully", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void exportTableToPdf(File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Courses List", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add generation date
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph date = new Paragraph("Generated on: " + new Date().toString(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);

        // Create table with same columns as TableView
        PdfPTable pdfTable = new PdfPTable(coursTable.getColumns().size());
        pdfTable.setWidthPercentage(100);

        // Add table headers
        for (TableColumn<Cours, ?> column : coursTable.getColumns()) {
            PdfPCell cell = new PdfPCell(new Phrase(column.getText()));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfTable.addCell(cell);
        }

        // Add table rows
        for (Cours cours : coursTable.getItems()) {
            pdfTable.addCell(cours.getTitre() != null ? cours.getTitre() : "");

            PdfPCell descCell = new PdfPCell(new Phrase(cours.getDescription() != null ? cours.getDescription() : ""));
            descCell.setFixedHeight(50f);
            descCell.setNoWrap(false);
            pdfTable.addCell(descCell);

            PdfPCell contentCell = new PdfPCell(new Phrase(cours.getContenu() != null ? cours.getContenu() : ""));
            contentCell.setFixedHeight(50f);
            contentCell.setNoWrap(false);
            pdfTable.addCell(contentCell);

            pdfTable.addCell(cours.getCategorie() != null ? cours.getCategorie().getNomCategorie() : "");
            pdfTable.addCell(cours.getImagePath() != null ? cours.getImagePath() : "");
        }

        document.add(pdfTable);
        document.close();
    }



    private void validateFields() throws ValidationException {
        if (titreField.getText().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (titreField.getText().trim().length() < 3 || titreField.getText().trim().length() > 50) {
            throw new ValidationException("Title must be between 3-50 characters");
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            throw new ValidationException("Description is required");
        }
        if (descriptionArea.getText().trim().length() < 10 || descriptionArea.getText().trim().length() > 255) {
            throw new ValidationException("Description must be between 10-255 characters");
        }
        if (contenuArea.getText().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (contenuArea.getText().trim().length() < 20) {
            throw new ValidationException("Content must be at least 20 characters");
        }
        if (categorieCombo.getValue() == null) {
            throw new ValidationException("Category is required");
        }
    }

    public void refreshTable() {
        coursList.setAll(coursService.getAllCours());
        updateFilters(); // Refresh filters after data load
    }

    private void setFormData(Cours cours) {
        titreField.setText(cours.getTitre());
        descriptionArea.setText(cours.getDescription());
        contenuArea.setText(cours.getContenu());
        categorieCombo.setValue(cours.getCategorie());
        imagePathField.setText(cours.getImagePath());

        if (cours.getImagePath() != null && !cours.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(CoursService.UPLOAD_DIR + cours.getImagePath());
                if (imageFile.exists()) {
                    selectedImageFile = imageFile;
                    imagePreview.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imagePreview.setImage(null);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                imagePreview.setImage(null);
            }
        } else {
            imagePreview.setImage(null);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionArea.clear();
        contenuArea.clear();
        categorieCombo.getSelectionModel().clearSelection();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
        coursTable.getSelectionModel().clearSelection();
        selectedCours = null;
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}