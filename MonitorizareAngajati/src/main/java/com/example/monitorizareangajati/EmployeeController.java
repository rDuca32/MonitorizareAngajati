    package com.example.monitorizareangajati;

    import domain.EntityConverter;
    import domain.Task;
    import domain.TaskConverter;
    import javafx.application.Platform;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import repository.SQLTaskRepository;
    import repository.SQLUserRepository;
    import repository.TextFileRepository;

    import java.io.*;
    import java.time.LocalTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Timer;
    import java.util.TimerTask;

    public class EmployeeController {

        @FXML
        private Text messageText;

        @FXML
        private String employeeName;

        @FXML
        private Button markPresenceButton;

        @FXML
        private TextField arrivalHour;
        @FXML
        private Button logoutButton;

        @FXML
        private ListView<String> tasksListView;

        @FXML
        private ObservableList<String> tasksList = FXCollections.observableArrayList();

        private SQLUserRepository sqlUserRepository;

        private TextFileRepository tasksTextFileRepository;

        public void setRepositories(SQLUserRepository sqlUserRepository, TextFileRepository tasksTextFileRepository) {
            this.sqlUserRepository = sqlUserRepository;
            this.tasksTextFileRepository = tasksTextFileRepository;
        }

        @FXML
        public void initialize() {
            tasksListView.setItems(tasksList);
        }

        public void initializeAfterRepo() {
            loadEmployeeTasks();
            startTasksPolling();
        }

        private void loadEmployeeTasks() {
            try {
                tasksList.clear();

                Integer employeeId = sqlUserRepository.getEmployeeIdByName(employeeName);
                if (employeeId == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Employee ID not found");
                    return;
                }

                List<Task> allTasks = (List<Task>) tasksTextFileRepository.getAll();

                for (Task task : allTasks) {
                    if (task.getEmployeeId() == employeeId) {
                        String taskDisplay = String.format("ID: %d | %s | Status: %s", task.getId(), task.getDescription(), task.getStatus());
                        tasksList.add(taskDisplay);
                    }
                }
                if (tasksList.isEmpty()) {
                    tasksList.add("No tasks assigned yet");
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tasks: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private static final String TASKS_FILE_PATH = "MonitorizareAngajati/tasks.txt";
        private List<String> seenTasks = new ArrayList<>();

        private void startTasksPolling() {
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    File file = new File(TASKS_FILE_PATH);

                    if (!file.exists()) return;

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while((line = reader.readLine()) != null){
                            if (!seenTasks.contains(line)) {
                                seenTasks.add(line);
                                String finalLine = line;
                                Platform.runLater(() -> showNewTasks(finalLine));
                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }, 0, 5000);
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

        private static final String PRESENCE_FILE_NAME = "MonitorizareAngajati/prezenta.txt";

        @FXML
        protected void onMarkPresenceButtonClick() {
            String arrivalHourText = arrivalHour.getText();

            if (arrivalHourText.isEmpty()){
                showAlert(Alert.AlertType.ERROR, "Attention", "Arrival hour is empty");
                return;
            }

            try {
                LocalTime.parse(arrivalHourText);

                try (PrintWriter out = new PrintWriter(new FileWriter(PRESENCE_FILE_NAME, true))){
                    out.println(employeeName + " - " + arrivalHourText);
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Arrival hour market successfully");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Arrival hour market failed. Use (HH:MM)");
            }
        }

        private static final String NOTIF_FILE_PATH = "MonitorizareAngajati/notificari.txt";

        private void writeLogoutToFile(String employeeName) {
            try (PrintWriter out = new PrintWriter(new FileWriter(NOTIF_FILE_PATH, true))) {
                String logoutEntry = "LOGOUT:" + employeeName;
                out.println(logoutEntry);
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