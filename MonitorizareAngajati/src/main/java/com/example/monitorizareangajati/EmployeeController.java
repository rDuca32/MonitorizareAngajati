package com.example.monitorizareangajati;

import domain.Task;
import domain.TaskStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    @FXML private ListView<String> tasksListView;

    private final ObservableList<String> tasksList = FXCollections.observableArrayList();
    private final Set<Integer> knownTaskIds = new HashSet<>();
    private Integer employeeId;

    private SQLUserRepository sqlUserRepository;
    private SQLTaskRepository sqlTaskRepository;

    private Timer tasksPollingTimer;

    @FXML
    public void initialize() {
        tasksListView.setItems(tasksList);
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
            tasksList.clear();
            knownTaskIds.clear();

            List<Task> employeeTasks = getEmployeeTasks();

            if (employeeTasks.isEmpty()) {
                tasksList.add(NO_TASKS_MESSAGE);
                return;
            }

            employeeTasks.forEach(this::addTaskToList);
        } catch (Exception e) {
            showError("Failed to load tasks: " + e.getMessage());
        }
    }

    private List<Task> getEmployeeTasks() {
        List<Task> allTasks = (List<Task>) sqlTaskRepository.getAll();
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

    private void stopTasksPolling() {
        if (tasksPollingTimer != null) {
            tasksPollingTimer.cancel();
            tasksPollingTimer = null;
        }
    }

    private void checkForNewTasks() {
        try {
            sqlTaskRepository.reload();

            List<Task> currentTasks = getEmployeeTasks();

            Platform.runLater(() -> {
                boolean hasNewTasks = false;

                for (Task task : currentTasks) {
                    if (!knownTaskIds.contains(task.getId())) {
                        addTaskToList(task);
                        hasNewTasks = true;
                    }
                }

                if (hasNewTasks && !tasksList.isEmpty() && NO_TASKS_MESSAGE.equals(tasksList.get(0))) {
                    tasksList.remove(0);
                }
            });
        } catch (Exception e) {
            System.err.println("Error checking for new tasks: " + e.getMessage());
        }
    }


    private void addTaskToList(Task task) {
        if (task == null) return;

        String taskDisplay = formatTaskDisplay(task);

        if (!tasksList.contains(taskDisplay)) {
            tasksList.add(taskDisplay);
            knownTaskIds.add(task.getId());
        }
    }

    private String formatTaskDisplay(Task task) {
        return String.format("ID: %d | %s | Status: %s", task.getId(), Optional.ofNullable(task.getDescription()).orElse("No description"), Optional.ofNullable(task.getStatus()).orElse(TaskStatus.PENDING));
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

    public void cleanup() {
        stopTasksPolling();
    }
}