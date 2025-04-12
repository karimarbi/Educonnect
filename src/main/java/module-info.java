module com.example.educonnect.educonnectjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.educonnect.educonnectjavafx to javafx.fxml;
    exports com.example.educonnect.educonnectjavafx;
}