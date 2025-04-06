module com.example.monitorizareangajati {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;


    opens com.example.monitorizareangajati to javafx.fxml;
    exports com.example.monitorizareangajati;
}