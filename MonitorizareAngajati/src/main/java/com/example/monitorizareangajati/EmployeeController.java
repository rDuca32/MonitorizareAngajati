package com.example.monitorizareangajati;

import domain.Task;
import domain.Time;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.time.LocalTime;

public class EmployeeController {

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onMarkPresenceButtonClick() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        Time arrivalTime = new Time(hour, minute);

        showInfo("Presence Marked", "Presence marked at " + arrivalTime.toString());
    }

    @FXML
    protected void onLogoutButtonClick() {
        showInfo("Logout", "Logging out.");
        System.exit(0);
    }

    public void addTask(Task task) {
        tasksArea.appendText("Task ID: " + task.getTaskId() + ", Description: " + task.getDescription() + ", Status: " + task.getStatus() + "\n");
    }
}