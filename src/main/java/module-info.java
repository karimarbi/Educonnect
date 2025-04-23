module com.esprit.event_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    // Optional dependencies
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.google.zxing;
    requires java.desktop;
    requires javafx.swing;

    // Main package exports and opens
    opens com.esprit.event_java to javafx.fxml;
    exports com.esprit.event_java;

    // Controller package access
    opens com.esprit.event_java.Controllers to javafx.fxml;
    exports com.esprit.event_java.Controllers;

    // Utils package
    exports com.esprit.event_java.utils;
    opens com.esprit.event_java.utils to javafx.fxml;

    // Model package - CRITICAL FIX
    // Added opens to javafx.base for PropertyValueFactory access
    opens com.esprit.event_java.Models to javafx.fxml, javafx.base;
    exports com.esprit.event_java.Models;
}