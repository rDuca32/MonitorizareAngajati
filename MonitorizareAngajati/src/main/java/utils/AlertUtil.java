package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtil {

    private AlertUtil() {}

    public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String message) {
        showAlert(AlertType.ERROR, "Error", message);
    }

    public static void showInfoAlert(String message) {
        showAlert(AlertType.INFORMATION, "Info", message);
    }

    public static void showWarningAlert(String message) {
        showAlert(AlertType.WARNING, "Warning", message);
    }
}
