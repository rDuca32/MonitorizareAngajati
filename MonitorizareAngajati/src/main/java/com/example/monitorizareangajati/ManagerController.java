package com.example.monitorizareangajati;

import domain.Task;
import domain.TaskStatus;
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
import repository.SQLTaskRepository;
import repository.SQLUserRepository;
import repository.TextFileRepository;

import java.io.*;
import java.util.*;

public class ManagerController {

    @FXML
    private ListView<String> employeeListView;

    @FXML
    private TextArea taskDescriptionArea;

    @FXML
    private Button assignTaskButton;

    @FXML
    private String managerName;

    @FXML
    private Text messageText;

    @FXML
    private Button logoutButton;

    private SQLUserRepository sqlUserRepository;
    private TextFileRepository tasksTextFileRepository;

    public void setRepositories(SQLUserRepository sqlUserRepository, TextFileRepository tasksTextFileRepository) {
        this.sqlUserRepository = sqlUserRepository;
        this.tasksTextFileRepository = tasksTextFileRepository;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    private ObservableList<String> employeeList = FXCollections.observableArrayList();

    public void showEmployeeLogout(String employeeLine) {
        Platform.runLater(() -> {
            try {
                String[] parts = employeeLine.split(":");
                if (parts.length < 2) return;
                String employeeName = parts[1].trim();

                employeeList.removeIf(entry -> entry.startsWith(employeeName + " - "));

                messageText.setText(employeeName + " has logged out");
                showAlert(Alert.AlertType.INFORMATION, "Logout", employeeName + " has logged out");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void showEmployeeLogin(String employeeLine) {
        Platform.runLater(() -> {
            employeeList.add(employeeLine);
            messageText.setText(employeeLine);
            showAlert(Alert.AlertType.INFORMATION, "Login", employeeLine);
        });
    }

    private int randomIdGenerator() {
        Random random = new Random();
        return 1000 + random.nextInt(9000);
    }

    @FXML
    protected void onAssignTaskButtonClick() {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        String taskDescription = taskDescriptionArea.getText();
        TaskStatus status = TaskStatus.PENDING;

        if (selectedEmployee == null || taskDescription.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select an employee and enter a task description");
            return;
        }

        try {
            int employeeId = findEmployeeIdByName(selectedEmployee);
            int taskId = randomIdGenerator();
            Task task = new Task(taskId, taskDescription, employeeId, status);
            tasksTextFileRepository.add(task);

            showAlert(Alert.AlertType.INFORMATION, "Succes", "Task assigned to " + selectedEmployee + ": " + taskDescription);
            taskDescriptionArea.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error while assigning task");
            e.printStackTrace();
        }
    }

    private int findEmployeeIdByName(String employeeEntry) throws Exception {
        String[] parts = employeeEntry.split(" - ");
        if (parts.length < 1) {
            throw new Exception("Invalid employee entry format: " + employeeEntry);
        }
        String username = parts[0].trim();

        Integer employeeId = sqlUserRepository.getEmployeeIdByName(username);
        if (employeeId == null) {
            throw new Exception("Employee '" + username + "' not found in database");
        }
        return employeeId;
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
    private static final String NOTIF_FILE_PATH = "MonitorizareAngajati/notificari.txt";

    private void startNotificationPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                File file = new File(NOTIF_FILE_PATH);
                if (!file.exists()) return;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!seenNotifications.contains(line)) {
                            seenNotifications.add(line);
                            String finalLine = line;
                            Platform.runLater(() -> {
                                if (finalLine.startsWith("LOGOUT:")){
                                    showEmployeeLogout(finalLine);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    private static final String PRESENCE_FILE_PATH = "MonitorizareAngajati/prezenta.txt";
    private List<String> seenPresence = new ArrayList<>();

    private void startPresencePolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                File file = new File(PRESENCE_FILE_PATH);
                if(!file.exists()) return;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while((line = reader.readLine()) != null){
                            if (!seenPresence.contains(line)) {
                                seenPresence.add(line);
                                String finalLine = line;
                                Platform.runLater(() -> showEmployeeLogin(finalLine));
                            }
                        }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    @FXML
    public void initialize() {
        employeeListView.setItems(employeeList);
        clearNotificationFile();
        clearPresenceFile();
        startNotificationPolling();
        startPresencePolling();
    }

    private void clearNotificationFile() {
        try (PrintWriter writer = new PrintWriter(NOTIF_FILE_PATH)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearPresenceFile() {
        try (PrintWriter writer = new PrintWriter(PRESENCE_FILE_PATH)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}