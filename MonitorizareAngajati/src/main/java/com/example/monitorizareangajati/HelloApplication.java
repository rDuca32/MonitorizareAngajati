package com.example.monitorizareangajati;

import domain.Task;
import domain.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.MemoryRepository;
import repository.RepositoryException;
import repository.SQLUserRepository;
import service.LoginService;
import service.NotificationService;

import java.io.IOException;

public class HelloApplication extends Application {

    public static MemoryRepository<User> repoUser;
    public static MemoryRepository<Task> repoTasks;

    private static NotificationService notificationService;
    private static LoginService loginService;

    @Override
    public void start(Stage stage) throws IOException {
        notificationService = NotificationService.getInstance();
        loginService = new LoginService(new SQLUserRepository());
        openUserView(stage);
    }

    public static void openUserView(Stage stage) throws IOException {
        try {
            SQLUserRepository sqlUserRepository = new SQLUserRepository();

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            UserController userController = fxmlLoader.getController();
            userController.initialize();
            userController.setRepositories(sqlUserRepository);

            stage.setTitle("Employee Monitoring System - Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load user view: " + e.getMessage());
            throw e;
        }
    }

    public static void openEmployeeView(Stage stage, String employeeName) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("employee-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);

        EmployeeController controller = loader.getController();
        controller.setEmployeeName(employeeName);

        stage.setTitle("Employee - " + employeeName);
        stage.setScene(scene);
        stage.show();
    }

    public static void openManagerView(Stage stage, String managerName) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("manager-view.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);

        ManagerController controller = loader.getController();
        stage.setOnHidden(e -> controller.cleanup());

        stage.setTitle("Manager Panel - " + managerName);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}