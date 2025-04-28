package com.esprit.Controllers.Karim;

import com.esprit.Entities.User;
import com.esprit.Entities.UserSession;
import com.esprit.Models.Category;
import com.esprit.Models.Event;
import com.esprit.Services.CategoryService;
import com.esprit.Services.EventService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FrontController {
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane eventsContainer;
    @FXML private FlowPane categoriesContainer;

    private final EventService eventService = new EventService();
    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        loadCategories();
        loadAllEvents();
        setupContainerStyle();
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();

        Button allButton = createCategoryButton("All", null);
        categoriesContainer.getChildren().add(allButton);

        for (Category category : categories) {
            Button categoryButton = createCategoryButton(category.getName(), category);
            categoriesContainer.getChildren().add(categoryButton);
        }
    }

    private Button createCategoryButton(String text, Category category) {
        Button button = new Button(text);
        button.getStyleClass().add("category-button");
        button.setOnAction(e -> filterEventsByCategory(category));
        return button;
    }

    private void filterEventsByCategory(Category category) {
        eventsContainer.getChildren().clear();
        List<Event> filteredEvents = category == null ?
                eventService.getAllEvents() :
                eventService.filterEventsByCategory(category);

        for (Event event : filteredEvents) {
            eventsContainer.getChildren().add(createEventCard(event));
        }
    }

    private void loadAllEvents() {
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            eventsContainer.getChildren().add(createEventCard(event));
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");

        // Image and content setup...
        ImageView imageView = createEventImageView(event);

        VBox content = new VBox();
        content.getStyleClass().add("event-content");
        content.setSpacing(10);
        content.setPadding(new Insets(15));

        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");

        // Add participants info
        Label participantsLabel = new Label(
                String.format("Participants: %d/%d",
                        event.getCurrentParticipants(),
                        event.getMaxParticipants())
        );
        participantsLabel.getStyleClass().add("event-meta");

        Button registerButton = new Button("Register Now");
        registerButton.getStyleClass().add("register-button");

        // Check registration status
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (eventService.isUserRegistered(event.getId(), currentUser.getId())) {
                registerButton.setText("Registered");
                registerButton.setDisable(true);
            } else if (eventService.hasTimeConflict(currentUser.getId(), event)) {
                registerButton.setText("Time Conflict");
                registerButton.setDisable(true);
            } else if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
                registerButton.setText("Full");
                registerButton.setDisable(true);
            }
        }

        registerButton.setOnAction(e -> showRegistrationForm(event));

        // Add elements to card
        content.getChildren().addAll(
                titleLabel,
                createDateTimeLabel(event),
                createLocationLabel(event),
                participantsLabel,
                registerButton
        );

        card.getChildren().addAll(imageView, content);
        return card;
    }

    private Label createDateTimeLabel(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");
        String formattedDateTime = event.getStartDatetime().format(formatter);
        Label dateLabel = new Label(formattedDateTime);
        dateLabel.getStyleClass().add("event-datetime");
        return dateLabel;
    }

    private Label createLocationLabel(Event event) {
        Label locationLabel = new Label(event.getLocation());
        locationLabel.getStyleClass().add("event-location");
        return locationLabel;
    }

    private ImageView createEventImageView(Event event) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        try {
            if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                String imagePath = "file:uploads/events/" + event.getImagePath();
                Image image = new Image(imagePath);
                if (!image.isError()) {
                    imageView.setImage(image);
                    return imageView;
                }

                InputStream imageStream = getClass().getResourceAsStream("/uploads/events/" + event.getImagePath());
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                    return imageView;
                }
            }

            InputStream placeholderStream = getClass().getResourceAsStream("/images/blank.png");
            if (placeholderStream != null) {
                imageView.setImage(new Image(placeholderStream));
            } else {
                imageView.setImage(createBlankImage());
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(createBlankImage());
        }

        return imageView;
    }

    private Image createBlankImage() {
        return new Image(getClass().getResourceAsStream("/images/blank.png"));
    }

    private void showRegistrationForm(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Events/RegistrationForm.fxml"));
            VBox form = loader.load();

            RegistrationController controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Inscription à " + event.getTitle());
            stage.setScene(new Scene(form));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'inscription");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupContainerStyle() {
        eventsContainer.setPadding(new Insets(20));
        eventsContainer.setHgap(20);
        eventsContainer.setVgap(20);
        eventsContainer.setAlignment(Pos.CENTER);

        categoriesContainer.setPadding(new Insets(10));
        categoriesContainer.setHgap(10);
        categoriesContainer.setVgap(10);
        categoriesContainer.setAlignment(Pos.CENTER);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}