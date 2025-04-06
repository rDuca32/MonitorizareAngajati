package service;

import com.example.monitorizareangajati.ManagerController;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private static NotificationService instance;
    private final List<ManagerController> managerControllers = new ArrayList<>();

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void registerManagerController(ManagerController controller) {
        managerControllers.add(controller);
    }

    public void unregisterManagerController(ManagerController controller) {
        managerControllers.remove(controller);
    }

    public void notifyLogout(String employeeName) {
        Platform.runLater(() -> {
            for (ManagerController controller : new ArrayList<>(managerControllers)) {
                controller.showEmployeeLogout(employeeName);
            }
        });
    }
}