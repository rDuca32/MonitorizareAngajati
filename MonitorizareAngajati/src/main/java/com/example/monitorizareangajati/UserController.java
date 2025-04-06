package com.example.monitorizareangajati;

import domain.User;
import domain.UserType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.SQLUserRepository;
import service.LoginService;
import service.LoginException;

public class UserController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private MenuButton userMenu;
    @FXML private Button loginButton;

    private UserType selectedRole;
    private LoginService loginService;

    public UserController() {

    }

    public void setRepositories(SQLUserRepository sqlUserRepository) {
        this.loginService = new LoginService(sqlUserRepository);
    }

    @FXML
    public void initialize() {
        userMenu.getItems().clear();

        MenuItem managerItem = new MenuItem("Manager");
        MenuItem employeeItem = new MenuItem("Employee");

        managerItem.setOnAction(e -> {
            selectedRole = UserType.MANAGER;
            userMenu.setText("Manager");
        });

        employeeItem.setOnAction(e -> {
            selectedRole = UserType.EMPLOYEE;
            userMenu.setText("Employee");
        });

        userMenu.getItems().addAll(managerItem, employeeItem);

        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputs(username, password)) {
            return;
        }

        try {
            User user = loginService.login(username, password);

            if ((selectedRole == UserType.MANAGER && !user.isManager()) ||
                    (selectedRole == UserType.EMPLOYEE && !user.isEmployee())) {
                throw new LoginException("Please select the correct role.");
            }
            redirectBasedOnRole(user);

        } catch (LoginException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Username and password cannot be empty");
            return false;
        }

        if (selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Unselected role", "Select a role");
            return false;
        }

        return true;
    }

    private void redirectBasedOnRole(User user) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        try {
            if (user.isManager()) {
                HelloApplication.openManagerView(stage, user.getUsername());
            } else {
                HelloApplication.openEmployeeView(stage, user.getUsername());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open view: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}