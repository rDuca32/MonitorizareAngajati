package com.example.monitorizareangajati;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ManagerController {

    @FXML
    private ListView<String> employeeListView;

    @FXML
    private TextArea taskDescriptionArea;

    @FXML
    private Button assignTaskButton;

    @FXML
    private Text messageText;

    @FXML
    private Button logoutButton;

    private ObservableList<String> employeeList = FXCollections.observableArrayList();

    public void showEmployeeLogout(String employeeLine) {
        Platform.runLater(() -> {
            messageText.setText(employeeLine);
            showAlert(Alert.AlertType.INFORMATION, "Logout", employeeLine);
        });
    }


    @FXML
    protected void onAssignTaskButtonClick() {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        String taskDescription = taskDescriptionArea.getText();

        if (selectedEmployee == null || taskDescription.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select an employee and enter a task description");
            return;
        }

        try {
            int employeeId = extractEmployeeId(selectedEmployee);
            showAlert(Alert.AlertType.INFORMATION, "Succes",
                    "Task assigned to " + selectedEmployee + ": " + taskDescription);
            taskDescriptionArea.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error while assigning task");
        }
    }

    private int extractEmployeeId(String employeeEntry) {
        String[] parts = employeeEntry.split(" - ");
        return Integer.parseInt(parts[0].replace("Employee ", ""));
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            HelloApplication.openUserView(stage);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error while logging out");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<String> seenNotifications = new ArrayList<>();
    private static final String FILE_PATH = "MonitorizareAngajati/notificari.txt";


    private void startNotificationPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                File file = new File(FILE_PATH);
                if (!file.exists()) return;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!seenNotifications.contains(line)) {
                            seenNotifications.add(line);
                            String finalLine = line;
                            Platform.runLater(() -> showEmployeeLogout(finalLine));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    @FXML
    public void initialize() {
        employeeList.addAll("Employee 1 - 09:00", "Employee 2 - 09:15");
        employeeListView.setItems(employeeList);

        clearNotificationFile();
        startNotificationPolling();
    }

    private void clearNotificationFile() {
        try (PrintWriter writer = new PrintWriter(FILE_PATH)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}