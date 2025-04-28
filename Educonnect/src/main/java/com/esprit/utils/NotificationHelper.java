package com.esprit.utils;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationHelper {

    public static void showNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            System.err.println("System tray not supported!");
            return;
        }

        try {
            // Create system tray instance
            SystemTray tray = SystemTray.getSystemTray();

            // Create image (use default if your icon isn't found)
            Image image = Toolkit.getDefaultToolkit().createImage(
                    NotificationHelper.class.getResource("/images/icon.png")
            );
            if (image == null) {
                // Fallback to default system icon
                image = Toolkit.getDefaultToolkit().createImage(new byte[0]);
            }

            // Create tray icon
            TrayIcon trayIcon = new TrayIcon(image, "EduConnect");
            trayIcon.setImageAutoSize(true);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added: " + e.getMessage());
                return;
            }

            // Display notification
            trayIcon.displayMessage(title, message, MessageType.INFO);

            // Remove icon after delay
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            tray.remove(trayIcon);
                        }
                    },
                    5000
            );

        } catch (Exception e) {
            System.err.println("Error showing notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}