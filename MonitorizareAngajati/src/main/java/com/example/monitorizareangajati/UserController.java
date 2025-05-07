package com.example.monitorizareangajati;

import domain.User;
import domain.UserType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.SQLUserRepository;
import service.LoginService;
import service.LoginException;
import utils.AlertUtil;

public class UserController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private MenuButton userMenu;
    @FXML private Button loginButton;

    private UserType selectedRole;
    private LoginService loginService;

    public UserController() {}

    @FXML
    public void initialize() {
        setupRoleMenu();
        setupLoginButton();
    }

    private void setupRoleMenu() {
        userMenu.getItems().clear();

        MenuItem managerItem = new MenuItem("Manager");
        MenuItem employeeItem = new MenuItem("Employee");

        managerItem.setOnAction(e -> selectRole(UserType.MANAGER, "Manager"));
        employeeItem.setOnAction(e -> selectRole(UserType.EMPLOYEE, "Employee"));

        userMenu.getItems().addAll(managerItem, employeeItem);
    }

    private void selectRole(UserType role, String displayText) {
        selectedRole = role;
        userMenu.setText(displayText);
    }

    private void setupLoginButton() {
        loginButton.setOnAction(e -> handleLogin());
    }

    public void setRepositories(SQLUserRepository sqlUserRepository) {
        this.loginService = new LoginService(sqlUserRepository);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputs(username, password)) {
            return;
        }

        try {
            User user = loginService.login(username, password);
            validateUserRole(user);
            redirectBasedOnRole(user);
        } catch (LoginException e) {
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showErrorAlert("Username and password cannot be empty.");
            return false;
        }

        if (selectedRole == null) {
            AlertUtil.showErrorAlert("Selected role cannot be empty.");
            return false;
        }

        return true;
    }

    private void validateUserRole(User user) throws LoginException {
        if ((selectedRole == UserType.MANAGER && !user.isManager()) ||
                (selectedRole == UserType.EMPLOYEE && !user.isEmployee())) {
            throw new LoginException("Please select the correct role.");
        }
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
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }
}