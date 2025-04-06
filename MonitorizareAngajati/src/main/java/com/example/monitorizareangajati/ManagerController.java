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
import service.LoginService;
import service.NotificationService;

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
    private LoginService loginService;

    @FXML
    public void initialize() {
        // Inițializare listă angajați
        employeeList.addAll("Employee 1 - 09:00", "Employee 2 - 09:15");
        employeeListView.setItems(employeeList);

        // Înregistrare controller în serviciul de notificări
        NotificationService.getInstance().registerManagerController(this);
    }

    // Metodă apelată de NotificationService când un angajat se deconectează
    public void showEmployeeLogout(String employeeName) {
        Platform.runLater(() -> {
            // Actualizează interfața
            messageText.setText(employeeName + " s-a deconectat la " + java.time.LocalTime.now());

            // Afișează alertă
            showAlert(Alert.AlertType.INFORMATION, "Deconectare",
                    employeeName + " a părăsit sistemul.");
        });
    }

    // Curăță resursele la închiderea ferestrei
    public void cleanup() {
        NotificationService.getInstance().unregisterManagerController(this);
    }

    @FXML
    protected void onAssignTaskButtonClick() {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        String taskDescription = taskDescriptionArea.getText();

        if (selectedEmployee == null || taskDescription.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Selectați un angajat și introduceți o descriere");
            return;
        }

        try {
            int employeeId = extractEmployeeId(selectedEmployee);
            showAlert(Alert.AlertType.INFORMATION, "Succes",
                    "Sarcina a fost atribuită angajatului " + employeeId);
            taskDescriptionArea.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "ID angajat invalid");
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
            showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la deconectare");
        }
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}