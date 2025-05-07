package com.example.monitorizareangajati;

import domain.EntityConverter;
import domain.Task;
import domain.TaskConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.SQLUserRepository;
import repository.TextFileRepository;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final String TASKS_FILE_PATH = "MonitorizareAngajati/tasks.txt";

    static SQLUserRepository sqlUserRepository = new SQLUserRepository();
    static EntityConverter<Task> converter = new TaskConverter();
    static TextFileRepository tasksRepo = new TextFileRepository<>(TASKS_FILE_PATH, converter);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        openUserView(stage);
    }

    public static void openUserView(Stage stage) throws IOException {
        try {
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
        controller.initialize();
        controller.setRepositories(sqlUserRepository, tasksRepo);
        controller.initializeAfterRepo();

        stage.setTitle("Employee - " + employeeName);
        stage.setScene(scene);
        stage.show();
    }

    public static void openManagerView(Stage stage, String managerName) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("manager-view.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);

        ManagerController controller = loader.getController();
        controller.setManagerName(managerName);
        controller.initialize();
        controller.setRepositories(sqlUserRepository, tasksRepo);

        stage.setTitle("Manager Panel - " + managerName);
        stage.setScene(scene);
        stage.show();
    }
}