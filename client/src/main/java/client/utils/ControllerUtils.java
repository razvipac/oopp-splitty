package client.utils;

import client.LanguageManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.util.Optional;

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

    /**
     * Creates a confirmation alert window.
     * @param title The title of the confirmation window.
     * @param content The content of the confirmation window.
     * @return true if the user confirms, false otherwise.
     */
    public boolean createConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(content);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Creates an alert for notifying the user about the unavailability of the server.
     * This method displays a warning dialog with customizable title and content,
     * providing options to reconnect or exit the application.
     *
     * @param title the title of the alert
     * @param content the content message of the alert
     * @param lm the language manager for retrieving localized strings
     * @return true if the user requests a reconnect, false otherwise
     */
    public boolean createServerUnavailableAlert(String title, String content, LanguageManager lm) {
        ButtonType reconnect = new ButtonType(lm.get("Reconnect"), ButtonBar.ButtonData.OK_DONE);
        ButtonType exit = new ButtonType(lm.get("Exit"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                content,
                reconnect,
                exit
        );

        alert.setTitle(title);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == reconnect;
    }

    /**
     * Saves a serializable object to a given path, creates the object if it does not exist
     * @param path path to save
     * @param object object to save
     * @return true iff successful
     */
    public boolean saveObject(String path, Object object) {
        try {
            File f = new File(path);
            if (!f.exists()){
                f.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    /**
     * Reads a object from path
     * @param path path to read from
     * @param <T> type of the read object
     * @return the read object instance
     */
    public <T> T readObject(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            return null;
        }
    }

    /**
     * Creates a binding for allowing keyboard only users to use a given combo box
     * @param comboBox comboBox to bind
     * @param <T> generic type, not relevant for the functionality
     */
    public <T> void bindComboBoxForKeyboardInput(ComboBox<T> comboBox) {
        comboBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                int size = comboBox.getItems().size();
                int index = comboBox.getSelectionModel().getSelectedIndex();
                comboBox.getSelectionModel().select((index + 1) % size);
            }
        });
    }
}
