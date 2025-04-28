package com.esprit.Controllers.Karim;

import com.esprit.Models.Event;
import com.esprit.Services.QRGenerator;
import com.google.zxing.WriterException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class QrCodeController {

    private static final int QR_CODE_SIZE = 300;
    private final StackPane parent = new StackPane();
    private Event event;

    public QrCodeController(Event event) {
        this.event = event;
    }

    public void buildUI() {
        QRGenerator qrGenerator;

        try {
            qrGenerator = new QRGenerator(
                    event,
                    QR_CODE_SIZE,
                    QR_CODE_SIZE,
                    BufferedImage.TYPE_INT_ARGB
            );
        } catch (WriterException e) {
            e.printStackTrace();
            return;
        }

        BufferedImage qrImage = qrGenerator.qrImage();

        // Save QR code as an image file (optional)
        try {
            String filename = "event_qrcode_" + event.getId() + ".png";
            qrGenerator.save(filename, "PNG");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image image = SwingFXUtils.toFXImage(qrImage, null);

        // Display QR code in JavaFX window
        ImageView imageView = new ImageView(image);
        parent.getChildren().add(imageView);
    }

    public void start(Stage stage) throws Exception {
        this.setupStage(stage);
    }

    private void setupStage(Stage stage) {
        Scene scene = new Scene(this.parent, 640.0, 480.0);
        stage.setTitle("Event QR Code - " + event.getTitle());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}