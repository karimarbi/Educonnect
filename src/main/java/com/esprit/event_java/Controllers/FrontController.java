package com.esprit.event_java.Controllers;

import com.esprit.event_java.Models.Event;
import com.esprit.event_java.Services.EventService;
import com.esprit.event_java.Services.QRGenerator;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.google.zxing.WriterException;

public class FrontController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane coursesContainer;

    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        loadEvents();
        setupContainerStyle();
    }

    private void loadEvents() {
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            coursesContainer.getChildren().add(createEventCard(event));
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        // Image - Fixed image loading
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        try {
            if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                InputStream imageStream = getClass().getResourceAsStream("/images/events/" + event.getImagePath());
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                } else {
                    imageView.setImage(new Image("file:uploads/events/" + event.getImagePath()));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("Couldn't load placeholder image either");
            }
        }

        // Title
        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(270);

        // Start DateTime
        Label startLabel = new Label("Starts: " + event.getStartDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        startLabel.getStyleClass().add("event-datetime");

        // Location
        Label locationLabel = new Label("Location: " + event.getLocation());
        locationLabel.getStyleClass().add("event-location");

        // Category
        Label categoryLabel = new Label();
        if (event.getCategory() != null) {
            categoryLabel.setText("Category: " + event.getCategory().getName());
        }
        categoryLabel.getStyleClass().add("event-category");

        // Description
        Text descriptionText = new Text(event.getDescription());
        descriptionText.setWrappingWidth(270);
        descriptionText.getStyleClass().add("event-description");

        // Details Button with QR Code functionality
        Button detailsButton = new Button("View QR Code");
        detailsButton.getStyleClass().add("qr-button");
        detailsButton.setOnAction(e -> showQRCode(event));

        card.getChildren().addAll(imageView, titleLabel, startLabel, locationLabel, categoryLabel, descriptionText, detailsButton);
        return card;
    }

    private void showQRCode(Event event) {
        try {
            // Create QR code
            QRGenerator qrGenerator = new QRGenerator(event, 300, 300, BufferedImage.TYPE_INT_ARGB);
            BufferedImage qrImage = qrGenerator.qrImage();
            Image fxImage = SwingFXUtils.toFXImage(qrImage, null);

            // Create QR code display window
            Stage qrStage = new Stage();
            qrStage.initModality(Modality.APPLICATION_MODAL);
            qrStage.setTitle("Event QR Code - " + event.getTitle());

            ImageView qrImageView = new ImageView(fxImage);
            qrImageView.setFitWidth(350);
            qrImageView.setFitHeight(350);
            qrImageView.setPreserveRatio(true);

            VBox qrBox = new VBox(20);
            qrBox.setAlignment(Pos.CENTER);
            qrBox.setPadding(new Insets(20));
            qrBox.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Scan to get event details");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            qrBox.getChildren().addAll(titleLabel, qrImageView);

            Scene scene = new Scene(qrBox, 400, 450);
            qrStage.setScene(scene);
            qrStage.show();
        } catch (WriterException e) {
            e.printStackTrace();
            // Show error message
            System.err.println("Failed to generate QR code: " + e.getMessage());
        }
    }

    private void setupContainerStyle() {
        coursesContainer.setPadding(new Insets(20));
        coursesContainer.setHgap(20);
        coursesContainer.setVgap(20);
        coursesContainer.setAlignment(Pos.CENTER);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}