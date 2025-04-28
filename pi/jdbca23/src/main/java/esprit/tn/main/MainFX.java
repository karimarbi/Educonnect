package esprit.tn.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/views/TestView.fxml"));
            Scene scene = new Scene(loader.load());
            
            primaryStage.setTitle("Gestion des Tests");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("javafx.runtime.path", "C:\\Users\\selmi\\Downloads\\openjfx-22.0.2_windows-x64_bin-sdk\\javafx-sdk-22.0.2");
        launch(args);
    }
}
