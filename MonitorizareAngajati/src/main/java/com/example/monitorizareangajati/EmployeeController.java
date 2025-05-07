package com.example.monitorizareangajati;

import domain.Task;
import domain.TaskConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import repository.SQLUserRepository;
import repository.TextFileRepository;
import utils.AlertUtil;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EmployeeController {
    private static final String TASKS_FILE_PATH = "MonitorizareAngajati/tasks.txt";
    private static final String PRESENCE_FILE_NAME = "MonitorizareAngajati/prezenta.txt";
    private static final String NOTIF_FILE_PATH = "MonitorizareAngajati/notificari.txt";

    @FXML private Text messageText;
    @FXML private String employeeName;
    @FXML private Button markPresenceButton;
    @FXML private TextField arrivalHour;
    @FXML private Button logoutButton;
    @FXML private ListView<String> tasksListView;

    private ObservableList<String> tasksList = FXCollections.observableArrayList();
    private List<String> seenTasks = new ArrayList<>();

    private SQLUserRepository sqlUserRepository;
    private TextFileRepository tasksTextFileRepository;

    @FXML
    public void initialize() {
        tasksListView.setItems(tasksList);
    }

    public void initializeAfterRepo() {
        loadEmployeeTasks();
        startTasksPolling();
    }

    public void setRepositories(SQLUserRepository sqlUserRepository, TextFileRepository tasksTextFileRepository) {
        this.sqlUserRepository = sqlUserRepository;
        this.tasksTextFileRepository = tasksTextFileRepository;
    }

    public void setEmployeeName(String name) {
        this.employeeName = name;
    }

    private void loadEmployeeTasks() {
        try {
            tasksList.clear();
            Integer employeeId = sqlUserRepository.getEmployeeIdByName(employeeName);

            if (employeeId == null) {
                AlertUtil.showErrorAlert("Employee not found" + employeeName);
                return;
            }

            List<Task> allTasks = (List<Task>) tasksTextFileRepository.getAll();
            boolean hasTasks = false;

            for (Task task : allTasks) {
                if (task.getEmployeeId() == employeeId) {
                    String taskDisplay = String.format("ID: %d | %s | Status: %s",
                            task.getId(), task.getDescription(), task.getStatus());
                    tasksList.add(taskDisplay);
                    hasTasks = true;
                }
            }

            if (!hasTasks) {
                tasksList.add("No tasks assigned yet");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Failed to load tasks" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startTasksPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForNewTasks();
            }
        }, 0, 5000);
    }

    private void checkForNewTasks() {
        File file = new File(TASKS_FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!seenTasks.contains(line)) {
                    seenTasks.add(line);
                    String finalLine = line;
                    Platform.runLater(() -> showNewTasks(finalLine));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNewTasks(String taskLine) {
        try {
            TaskConverter converter = new TaskConverter();
            Task newTask = converter.fromString(taskLine);
            Integer employeeId = sqlUserRepository.getEmployeeIdByName(employeeName);

            if (employeeId != null && newTask.getEmployeeId() == employeeId) {
                String taskDisplay = String.format("ID: %d | %s | Status: %s", newTask.getId(), newTask.getDescription(), newTask.getStatus());

                if (!tasksList.contains(taskDisplay)) {
                    tasksList.add(taskDisplay);

                    if (tasksList.size() > 1 && tasksList.get(0).equals("No tasks assigned yet")) {
                        tasksList.remove(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing new task: " + e.getMessage());
        }
    }

    @FXML
    protected void onMarkPresenceButtonClick() {
        String arrivalHourText = arrivalHour.getText();

        if (arrivalHourText.isEmpty()) {
            AlertUtil.showErrorAlert("Arrival hour cannot be empty");
            return;
        }

        try {
            LocalTime.parse(arrivalHourText);
            writePresenceToFile(employeeName, arrivalHourText);
            AlertUtil.showInfoAlert("Successfully marked presence");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Failed to mark presence" + e.getMessage());
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        writeLogoutToFile(employeeName);

        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            HelloApplication.openUserView(stage);
        } catch (Exception e) {
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    private void writePresenceToFile(String employeeName, String arrivalTime) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(PRESENCE_FILE_NAME, true))) {
            out.println(employeeName + " - " + arrivalTime);
        }
    }

    private void writeLogoutToFile(String employeeName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(NOTIF_FILE_PATH, true))) {
            out.println("LOGOUT:" + employeeName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}