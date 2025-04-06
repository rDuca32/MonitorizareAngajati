    package com.example.monitorizareangajati;

    import domain.Time;
    import javafx.fxml.FXML;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextArea;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;

    import java.io.FileWriter;
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.time.LocalTime;

    public class EmployeeController {

        private String employeeName;

        @FXML
        private TextArea tasksArea;

        @FXML
        private Button markPresenceButton;

        @FXML
        private Button logoutButton;


        @FXML
        public void initialize() {
            tasksArea.setEditable(false);
        }

        public void setEmployeeName(String name) {
            this.employeeName = name;
        }

        private void showAlert(Alert.AlertType type, String title, String message) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @FXML
        protected void onMarkPresenceButtonClick() {
            Time arrivalTime = Time.fromLocalTime(LocalTime.now());
            showAlert(Alert.AlertType.INFORMATION, "Prezență marcată",
                    "Prezența ta a fost înregistrată la ora " + arrivalTime);
        }

        private static final String FILE_PATH = "MonitorizareAngajati/notificari.txt";

        private void writeLogoutToFile(String employeeName) {
            Time logoutTime = Time.fromLocalTime(LocalTime.now());
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
                out.println(employeeName + " disconnected at " + logoutTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @FXML
        protected void onLogoutButtonClick() {
            writeLogoutToFile(employeeName);

            try {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                HelloApplication.openUserView(stage);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }