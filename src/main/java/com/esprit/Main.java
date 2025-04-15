package com.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showHomePage(); // Start with the home page
        primaryStage.setTitle("EduConnect");
        primaryStage.show();
    }

    public static void showHomePage() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Views/homepage.fxml"));
        primaryStage.setScene(new Scene(root));
    }

    public static void showAdminDashboard() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/Views/AdminDashboard.fxml"));
        primaryStage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}