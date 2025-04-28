package com.esprit.Controllers.Mayssa;

import com.esprit.Models.Cours;
import com.esprit.Entities.User;
import com.esprit.Services.CoursService;
import com.esprit.Services.RegistrationService;
import com.esprit.Entities.UserSession;
import com.esprit.utils.NotificationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FrontController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane coursesContainer;
    @FXML private HBox paginationContainer;

    private final CoursService coursService = new CoursService();
    private final RegistrationService registrationService = new RegistrationService();
    private List<Cours> allCourses;
    private int currentPage = 0;
    private final int coursesPerPage = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadAllCourses();
        setupPagination();
    }

    private void setupUI() {
        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }

        if (coursesContainer != null) {
            coursesContainer.setPadding(new Insets(20));
            coursesContainer.setHgap(20);
            coursesContainer.setVgap(20);
            coursesContainer.setAlignment(Pos.CENTER);
        }
    }

    private void loadAllCourses() {
        allCourses = coursService.getAllCours();
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        coursesContainer.getChildren().clear();

        int fromIndex = currentPage * coursesPerPage;
        int toIndex = Math.min(fromIndex + coursesPerPage, allCourses.size());

        if (fromIndex >= allCourses.size()) {
            currentPage = 0;
            fromIndex = 0;
            toIndex = Math.min(coursesPerPage, allCourses.size());
        }

        List<Cours> pageCourses = allCourses.subList(fromIndex, toIndex);

        for (Cours cours : pageCourses) {
            VBox card = createCourseCard(cours);
            card.setUserData(cours); // Store course reference
            coursesContainer.getChildren().add(card);
        }
    }

    private void setupPagination() {
        paginationContainer.getChildren().clear();

        int totalPages = (int) Math.ceil((double) allCourses.size() / coursesPerPage);

        if (totalPages <= 1) {
            return; // No pagination needed if only one page
        }

        // Previous button
        Button prevButton = new Button("Previous");
        prevButton.getStyleClass().add("pagination-button");
        prevButton.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                displayCurrentPage();
                setupPagination();
            }
        });

        // Page indicator
        Label pageLabel = new Label(String.format("Page %d/%d", currentPage + 1, totalPages));
        pageLabel.getStyleClass().add("page-indicator");

        // Next button
        Button nextButton = new Button("Next");
        nextButton.getStyleClass().add("pagination-button");
        nextButton.setOnAction(e -> {
            if ((currentPage + 1) * coursesPerPage < allCourses.size()) {
                currentPage++;
                displayCurrentPage();
                setupPagination();
            }
        });

        paginationContainer.getChildren().addAll(prevButton, pageLabel, nextButton);
        paginationContainer.setAlignment(Pos.CENTER);
        paginationContainer.setSpacing(10);
    }

    private VBox createCourseCard(Cours cours) {
        VBox card = new VBox();
        card.getStyleClass().add("course-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        // Course image
        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("course-image");
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        try {
            if (cours.getImagePath() != null && !cours.getImagePath().isEmpty()) {
                File imageFile = new File(CoursService.UPLOAD_DIR + cours.getImagePath());
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/course-placeholder.png")));
        }

        // Course title
        Label titleLabel = new Label(cours.getTitre());
        titleLabel.getStyleClass().add("course-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // Course category
        Label categoryLabel = new Label();
        if (cours.getCategorie() != null) {
            categoryLabel.setText("Category: " + cours.getCategorie().getNomCategorie());
        }
        categoryLabel.getStyleClass().add("course-category");

        // Course description
        Text descriptionText = new Text(cours.getDescription());
        descriptionText.getStyleClass().add("course-description");
        descriptionText.setWrappingWidth(270);

        // Register button (only show if user is logged in)
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            Button registerButton = new Button();
            registerButton.getStyleClass().add("register-button");

            try {
                // Check registration status
                boolean isRegistered = registrationService.isUserRegisteredForCourse(
                        currentUser.getId(),
                        cours.getId()
                );

                if (isRegistered) {
                    registerButton.setText("Registered");
                    registerButton.setDisable(true);
                } else {
                    registerButton.setText("Register");
                    registerButton.setOnAction(event -> handleRegistration(cours, registerButton));
                }
            } catch (SQLException e) {
                System.err.println("Error checking registration status: " + e.getMessage());
                registerButton.setText("Register");
                registerButton.setOnAction(event -> handleRegistration(cours, registerButton));
            }

            card.getChildren().add(registerButton);
        }

        // Add click handler for details
        card.setOnMouseClicked(event -> openCourseDetails(cours));

        card.getChildren().addAll(imageView, titleLabel, categoryLabel, descriptionText);
        return card;
    }

    private void handleRegistration(Cours cours, Button registerButton) {
        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "Please login to register for courses", Alert.AlertType.ERROR);
                return;
            }

            // Show confirmation dialog
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Registration");
            confirmation.setHeaderText("Register for " + cours.getTitre());
            confirmation.setContentText(currentUser.getNom() + " " + currentUser.getPrenom() +
                    "\nEmail: " + currentUser.getEmail() +
                    "\n\nDo you want to register for this course?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (registrationService.registerForCourse(cours)) {
                    // Show notifications
                    NotificationHelper.showNotification(
                            "Course Registration",
                            "Successfully registered for " + cours.getTitre()
                    );
                    showAlert("Success", "Successfully registered for the course!", Alert.AlertType.INFORMATION);

                    // Update button state
                    registerButton.setText("Registered");
                    registerButton.setDisable(true);

                    // Refresh the course card
                    refreshCourseCard(cours);
                } else {
                    NotificationHelper.showNotification(
                            "Registration Failed",
                            "Could not register for " + cours.getTitre()
                    );
                    showAlert("Warning", "Registration failed. You may already be registered for this course or another course in the same category.", Alert.AlertType.WARNING);
                }
            }
        } catch (Exception e) {
            NotificationHelper.showNotification(
                    "Error",
                    "Registration error occurred"
            );
            showAlert("Error", "An error occurred during registration: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void refreshCourseCard(Cours cours) {
        // Find the card containing this course
        for (javafx.scene.Node node : coursesContainer.getChildren()) {
            if (node instanceof VBox && node.getUserData() == cours) {
                VBox newCard = createCourseCard(cours);
                newCard.setUserData(cours);
                int index = coursesContainer.getChildren().indexOf(node);
                coursesContainer.getChildren().set(index, newCard);
                break;
            }
        }
    }

    private void openCourseDetails(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Cours/CourseDetails.fxml"));
            Parent root = loader.load();

            CourseDetailsController controller = loader.getController();
            controller.setCourseData(cours);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(cours.getTitre());
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load course details", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void goToHome() {
        navigateTo("/Views/homepage.fxml", "EduConnect - Home");
    }

    @FXML
    private void goToEvents() {
        navigateTo("/Views/Events/front_view.fxml", "EduConnect - Events");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) scrollPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}