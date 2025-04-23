package com.esprit.event_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load main view
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/esprit/event_java/views/front_view.fxml")
        );
        Parent root = loader.load();

        // Create scene
        Scene scene = new Scene(root);

//        // Add CSS programmatically
//        try {
//            URL cssUrl = getClass().getResource("/com/esprit/event_java/views/styles/front.css");
//            if (cssUrl != null) {
//                scene.getStylesheets().add(cssUrl.toExternalForm());
//                System.out.println("CSS successfully loaded from: " + cssUrl);
//            } else {
//                System.err.println("CSS file not found at: /com/esprit/event_java/styles/front.css");
//                // Optional fallback styling
//                root.setStyle("-fx-font-family: 'Arial'; -fx-base: #34495e;");
//            }
//        } catch (Exception e) {
//            System.err.println("Error loading CSS: " + e.getMessage());
//        }

        stage.setScene(scene);
        stage.setTitle("Event Management System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
