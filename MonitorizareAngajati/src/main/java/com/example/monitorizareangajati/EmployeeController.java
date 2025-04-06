    package com.example.monitorizareangajati;

    import domain.Task;
    import domain.Time;
    import javafx.animation.PauseTransition;
    import javafx.fxml.FXML;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextArea;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import javafx.util.Duration;
    import service.NotificationService;

    import java.io.IOException;
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
        private Text messageText;

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
            LocalTime now = LocalTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();

            Time arrivalTime = new Time(hour, minute);

            showAlert(Alert.AlertType.INFORMATION, "Presence Marked", "Your presence has been marked at " + arrivalTime.toString());
        }

        @FXML
        protected void onLogoutButtonClick() {
            try {
                NotificationService.getInstance().notifyLogout(employeeName);
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                HelloApplication.openUserView(stage);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }