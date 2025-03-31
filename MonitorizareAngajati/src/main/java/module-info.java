module com.example.monitorizareangajati {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.monitorizareangajati to javafx.fxml;
    exports com.example.monitorizareangajati;
}