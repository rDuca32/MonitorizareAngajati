package com.example.monitorizareangajati;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ManagerController {

    @FXML
    private ListView<String> employeeListView;

    @FXML
    private TextArea taskDescriptionArea;

    @FXML
    private Button assignTaskButton;

    @FXML
    private Text messageText;

    private ObservableList<String> employeeList = FXCollections.observableArrayList();

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
    public void initialize() {
        employeeListView.setItems(employeeList);
        employeeList.addAll("Employee 1 - 09:00", "Employee 2 - 09:15");
    }

    @FXML
    protected void onAssignTaskButtonClick() {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        String taskDescription = taskDescriptionArea.getText();

        if (selectedEmployee == null || taskDescription.isEmpty()) {
            showError("Input Error", "Select an employee and enter task description.");
            return;
        }

        int employeeId = extractEmployeeId(selectedEmployee);

        if (employeeId != -1) {
            showInfo("Task Assigned", "Task '" + taskDescription + "' assigned to " + selectedEmployee);
            taskDescriptionArea.clear();
        } else {
            showError("Selection Error", "Invalid employee selection.");
        }
    }

    private int extractEmployeeId(String selectedEmployee) {
        try {
            String[] parts = selectedEmployee.split(" - ");
            return Integer.parseInt(parts[0].replace("Employee ", ""));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }
}