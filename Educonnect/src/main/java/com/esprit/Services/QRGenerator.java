package com.esprit.Services;

import com.esprit.Models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.util.Builder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class QRGenerator {

    private final BitMatrix bitMatrix;

    private BufferedImage qrImage;

    public QRGenerator(Event event, int width, int height, int imageType) throws WriterException {
        String eventData = formatEventData(event);
        BitMatrixBuilder builder = new BitMatrixBuilder(eventData, BarcodeFormat.QR_CODE, width, height);
        this.bitMatrix = builder.build();
        this.qrImageCreate(width, height, imageType);
    }

    private String formatEventData(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format(
                "Event: %s\nDate: %s to %s\nLocation: %s\nDescription: %s\nMax Participants: %d\nCategory: %s",
                event.getTitle(),
                event.getStartDatetime().format(formatter),
                event.getEndDatetime().format(formatter),
                event.getLocation(),
                event.getDescription(),
                event.getMaxParticipants(),
                event.getCategory()
        );
    }

    private void qrImageCreate(int width, int height, int imageType) {

        // Convert BitMatrix to BufferedImage
        this.qrImage = new BufferedImage(width, height, imageType);

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height; y++) {

                qrImage.setRGB(x, y, this.bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);

            }

        }

    }

    public void save(String pathname, String formatName) throws IOException {
        ImageIO.write(qrImage, formatName, new File(pathname));
    }

    public BufferedImage qrImage() {
        return this.qrImage;
    }

    private static class BitMatrixBuilder implements Builder<BitMatrix> {

        private final BitMatrix bitMatrix;

        public BitMatrixBuilder(String data, BarcodeFormat format, int width, int height) throws WriterException {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            this.bitMatrix = qrCodeWriter.encode(data, format, width, height);
        }

        @Override
        public BitMatrix build() {
            return this.bitMatrix;
        }

    }

}
