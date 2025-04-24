package com.esprit.event_java.Controllers;

import com.esprit.event_java.Models.Event;
import com.esprit.event_java.Models.Category;
import com.esprit.event_java.Services.EventService;
import com.esprit.event_java.Services.CategoryService;
import com.esprit.event_java.exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventController {
    // Form fields
    @FXML private TextField titleField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField durationField;
    @FXML private TextField maxParticipantsField;
    @FXML private TextField imagePathField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Button uploadImageButton;
    @FXML private ImageView imagePreview;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilterCombo;

    // Action buttons
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    // Table view
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, LocalDateTime> dateColumn;
    @FXML private TableColumn<Event, LocalDateTime> timeColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, Category> categoryColumn;

    private final EventService eventService = new EventService();
    private final CategoryService categoryService = new CategoryService();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private FileChooser fileChooser;
    private File selectedImageFile;
    private Event selectedEvent;

    @FXML
    public void initialize() {
        initializeForm();
        initializeCategoryFilter();
        initializeTableColumns();
        refreshEventTable();
        setupTableSelectionListener();

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void initializeForm() {
        categoryCombo.setItems(categoryService.getAllCategories());
        categoryCombo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        startTimeField.setText(LocalTime.now().format(timeFormatter));
        endTimeField.setText(LocalTime.now().plusHours(1).format(timeFormatter));

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
    }

    private void initializeCategoryFilter() {
        ObservableList<Category> allCategories = FXCollections.observableArrayList();
        allCategories.add(null);
        allCategories.addAll(categoryService.getAllCategories());

        categoryFilterCombo.setItems(allCategories);
        categoryFilterCombo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? "All Categories" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        categoryFilterCombo.getSelectionModel().selectFirst();
    }

    private void initializeTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDatetime"));
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toLocalDate().toString());
            }
        });

        timeColumn.setCellValueFactory(new PropertyValueFactory<>("startDatetime"));
        timeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toLocalTime().toString());
            }
        });

        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    private void setupTableSelectionListener() {
        eventTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedEvent = newValue;
                    if (newValue != null) {
                        setEventData(newValue);
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearForm();
                        saveButton.setDisable(false);
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                }
        );
    }

    @FXML
    private void handleSaveEvent() {
        try {
            Event event = createEventFromForm();
            eventService.addEvent(event);
            showAlert("Success", "Event created successfully", Alert.AlertType.INFORMATION);
            refreshEventTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to create event: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateEvent() {
        if (selectedEvent == null) {
            showAlert("Error", "No event selected to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            Event updatedEvent = createEventFromForm();
            updatedEvent.setId(selectedEvent.getId());
            eventService.updateEvent(updatedEvent);
            showAlert("Success", "Event updated successfully", Alert.AlertType.INFORMATION);
            refreshEventTable();
            clearForm();
        } catch (ValidationException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update event: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteEvent() {
        if (selectedEvent == null) {
            showAlert("Error", "No event selected to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Event");
        confirmation.setHeaderText("Confirm Deletion");
        confirmation.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                eventService.deleteEvent(selectedEvent.getId());
                showAlert("Success", "Event deleted successfully", Alert.AlertType.INFORMATION);
                refreshEventTable();
                clearForm();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete event: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    @FXML
    private void handleImageUpload() {
        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imagePathField.setText(file.getName());
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleRefresh() {
        refreshEventTable();
    }

    private Event createEventFromForm() throws ValidationException {
        validateFields();

        LocalDateTime startDateTime = LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.parse(startTimeField.getText(), timeFormatter)
        );

        LocalDateTime endDateTime = LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.parse(endTimeField.getText(), timeFormatter)
        );

        Event event = new Event();
        event.setTitle(titleField.getText().trim());
        event.setStartDatetime(startDateTime);
        event.setEndDatetime(endDateTime);
        event.setLocation(locationField.getText().trim());
        event.setDescription(descriptionArea.getText().trim());
        event.setDuration(Integer.parseInt(durationField.getText()));
        event.setMaxParticipants(Integer.parseInt(maxParticipantsField.getText()));
        event.setImageFile(selectedImageFile);
        event.setImagePath(imagePathField.getText().trim());
        event.setCategory(categoryCombo.getValue());

        return event;
    }

    private void validateFields() throws ValidationException {
        if (titleField.getText().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (locationField.getText().isEmpty()) {
            throw new ValidationException("Location is required");
        }
        if (durationField.getText().isEmpty()) {
            throw new ValidationException("Duration is required");
        }
        if (maxParticipantsField.getText().isEmpty()) {
            throw new ValidationException("Max participants is required");
        }
        if (categoryCombo.getValue() == null) {
            throw new ValidationException("Category is required");
        }
    }

    private void refreshEventTable() {
        String searchTerm = searchField.getText();
        Category selectedCategory = categoryFilterCombo.getValue();

        if ((searchTerm == null || searchTerm.isEmpty()) && selectedCategory == null) {
            eventTable.setItems(eventService.getAllEvents());
        } else {
            eventTable.setItems(eventService.searchAndFilterEvents(searchTerm, selectedCategory));
        }
    }

    private void setEventData(Event event) {
        if (event != null) {
            titleField.setText(event.getTitle());
            startDatePicker.setValue(event.getStartDatetime().toLocalDate());
            startTimeField.setText(event.getStartDatetime().toLocalTime().format(timeFormatter));
            endDatePicker.setValue(event.getEndDatetime().toLocalDate());
            endTimeField.setText(event.getEndDatetime().toLocalTime().format(timeFormatter));
            locationField.setText(event.getLocation());
            descriptionArea.setText(event.getDescription());
            durationField.setText(String.valueOf(event.getDuration()));
            maxParticipantsField.setText(String.valueOf(event.getMaxParticipants()));
            imagePathField.setText(event.getImagePath());
            categoryCombo.setValue(event.getCategory());

            if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                try {
                    File imageFile = new File("uploads/events/" + event.getImagePath());
                    if (imageFile.exists()) {
                        selectedImageFile = imageFile;
                        imagePreview.setImage(new Image(imageFile.toURI().toString()));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
        }
    }

    private void clearForm() {
        titleField.clear();
        startDatePicker.setValue(LocalDate.now());
        startTimeField.setText(LocalTime.now().format(timeFormatter));
        endDatePicker.setValue(LocalDate.now());
        endTimeField.setText(LocalTime.now().plusHours(1).format(timeFormatter));
        locationField.clear();
        descriptionArea.clear();
        durationField.clear();
        maxParticipantsField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
        categoryCombo.getSelectionModel().clearSelection();
        eventTable.getSelectionModel().clearSelection();
        selectedEvent = null;

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

    @FXML
    private void handleSearch() {
        refreshEventTable();
    }

    @FXML
    private void handleCategoryFilter() {
        refreshEventTable();
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        categoryFilterCombo.getSelectionModel().selectFirst();
        refreshEventTable();
    }

    public Map<Category, Long> getEventCountByCategory() {
        return eventService.getEventCountByCategory();
    }
}