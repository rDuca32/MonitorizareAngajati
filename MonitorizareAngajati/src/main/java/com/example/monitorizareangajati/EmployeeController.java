package com.example.monitorizareangajati;

import domain.Task;
import domain.TaskStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import repository.RepositoryException;
import repository.SQLTaskRepository;
import repository.SQLUserRepository;
import utils.AlertUtil;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeController {
    private static final String PRESENCE_FILE_NAME = "MonitorizareAngajati/prezenta.txt";
    private static final String NOTIFICATION_FILE_PATH = "MonitorizareAngajati/notificari.txt";
    private static final String NO_TASKS_MESSAGE = "No tasks assigned yet";

    @FXML private Text messageText;
    @FXML private String employeeName;
    @FXML private Button markPresenceButton;
    @FXML private TextField arrivalHour;
    @FXML private Button logoutButton;
    @FXML private Button markDoneButton;
    @FXML private ListView<Task> tasksListView;

    private final ObservableList<Task> observableTasks = FXCollections.observableArrayList();
    private Integer employeeId;

    private SQLUserRepository sqlUserRepository;
    private SQLTaskRepository sqlTaskRepository;

    private Timer tasksPollingTimer;

    @FXML
    public void initialize() {
        tasksListView.setItems(observableTasks);

        observableTasks.addListener((ListChangeListener<Task>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    Platform.runLater(() -> {
                        if (observableTasks.isEmpty()) {
                            messageText.setText(NO_TASKS_MESSAGE);
                        } else {
                            messageText.setText("");
                        }
                    });
                }
            }
        });

        tasksListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(String.format("ID: %d | %s | Status: %s",
                            task.getId(),
                            Optional.ofNullable(task.getDescription()).orElse("No description"),
                            task.getStatus()));
                }
            }
        });
    }

    public void initializeAfterRepo() {
        try {
            this.employeeId = sqlUserRepository.getEmployeeIdByName(employeeName);
            if (employeeId == null) {
                showError("Employee not found: " + employeeName);
                return;
            }
            loadEmployeeTasks();
            startTasksPolling();
        } catch (Exception e) {
            showError("Error initializing controller: " + e.getMessage());
        }
    }

    public void setRepositories(SQLUserRepository sqlUserRepository, SQLTaskRepository sqlTaskRepository) {
        this.sqlUserRepository = sqlUserRepository;
        this.sqlTaskRepository = sqlTaskRepository;
    }

    public void setEmployeeName(String name) {
        this.employeeName = name;
    }

    private void loadEmployeeTasks() {
        try {
            List<Task> allTasks = sqlTaskRepository.loadData();
            List<Task> employeeTasks = allTasks.stream().filter(t -> employeeId.equals(t.getEmployeeId())).toList();

            Platform.runLater(() -> {
                observableTasks.clear();
                observableTasks.addAll(employeeTasks);
                if (observableTasks.isEmpty()) {
                    messageText.setText(NO_TASKS_MESSAGE);
                } else {
                    messageText.setText("");
                }
            });

        } catch (Exception e) {
            showError("Failed to load tasks: " + e.getMessage());
        }
    }

    private void checkForNewTasks() {
        try {
            List<Task> currentTasks = getEmployeeTasks();

            Platform.runLater(() -> {
                observableTasks.clear();
                observableTasks.addAll(currentTasks);
                if (observableTasks.isEmpty()) {
                    messageText.setText(NO_TASKS_MESSAGE);
                }
            });
        } catch (Exception e) {
            System.err.println("Error checking for new tasks: " + e.getMessage());
        }
    }

    private List<Task> getEmployeeTasks() {
        List<Task> allTasks = sqlTaskRepository.loadData();
        if (allTasks == null) {
            return Collections.emptyList();
        }

        return allTasks.stream().filter(Objects::nonNull).filter(task -> employeeId.equals(task.getEmployeeId())).collect(Collectors.toList());
    }

    private void startTasksPolling() {
        stopTasksPolling();
        tasksPollingTimer = new Timer(true);
        tasksPollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForNewTasks();
            }
        }, 0, 5000);
    }

    @FXML
    protected void onMarkPresenceButtonClick() {
        String arrivalHourText = arrivalHour.getText();

        if (arrivalHourText == null || arrivalHourText.trim().isEmpty()) {
            showError("Arrival hour cannot be empty");
            return;
        }

        try {
            LocalTime.parse(arrivalHourText.trim());
            writePresenceToFile(employeeName, arrivalHourText.trim());
            showInfo("Successfully marked presence");
        } catch (Exception e) {
            showError("Invalid time format or failed to mark presence: " + e.getMessage());
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        stopTasksPolling();
        writeLogoutToFile(employeeName);

        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            HelloApplication.openUserView(stage);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    protected void onMarkDoneButtonClick() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError("Select a task first");
            return;
        }

        try {
            if (selectedTask.getStatus() == TaskStatus.FINISHED) {
                showInfo("Task already finished");
                return;
            }

            selectedTask.setStatus(TaskStatus.FINISHED);
            sqlTaskRepository.update(selectedTask);
            loadEmployeeTasks();

        } catch (RepositoryException e) {
            showError("Failed to update task: " + e.getMessage());
        }
    }

    private void writePresenceToFile(String employeeName, String arrivalTime) throws IOException {
        if (employeeName == null || arrivalTime == null) return;

        try (PrintWriter out = new PrintWriter(new FileWriter(PRESENCE_FILE_NAME, true))) {
            out.println(employeeName + " - " + arrivalTime);
        }
    }

    private void writeLogoutToFile(String employeeName) {
        if (employeeName == null) return;

        try (PrintWriter out = new PrintWriter(new FileWriter(NOTIFICATION_FILE_PATH, true))) {
            out.println("LOGOUT:" + employeeName);
        } catch (IOException e) {
            System.err.println("Error writing logout notification: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> AlertUtil.showErrorAlert(message));
    }

    private void showInfo(String message) {
        Platform.runLater(() -> AlertUtil.showInfoAlert(message));
    }

    private void stopTasksPolling() {
        if (tasksPollingTimer != null) {
            tasksPollingTimer.cancel();
            tasksPollingTimer = null;
        }
    }
}
