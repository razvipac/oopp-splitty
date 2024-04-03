package client.utils;

import javafx.scene.control.Alert;

public class ControllerUtils {
    /**
     * Creates an alert window
     * @param type The type of alert (e.g. CONFIRMATION or ERROR)
     * @param title The title of the window
     * @param header The header of the window
     * @param content The content of the window
     * @return Alert object
     */
    public Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
