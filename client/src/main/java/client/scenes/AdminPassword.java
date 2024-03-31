package client.scenes;

import client.interfaces.StaticPopupController;
import client.utils.ServerUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminPassword implements StaticPopupController {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private final Stage window;
    private Scene scene;
    private boolean isOpen;

    private boolean passwordIsCorrect;

    /**
     * Constructor for the AdminPassword that calls the method to create the scene
     * @param mainCtrl scene of the mainCtrl class
     * @param serverUtils global serverUtils singleton
     */
    public AdminPassword(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        window = new Stage();
        window.setTitle("Enter Admin Password");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(mainCtrl.getPrimaryStage());
        window.setOnCloseRequest(e -> closeAlertBox());
    }

    /**
     * Generate an ui from the given object instance
     * @return the newly generated scene
     */
    public Scene initialize() {
        // Password label
        Label passwordLabel = new Label("Please enter the admin password:");

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String entered = passwordField.getText();
            validatePassword(entered);
        });

        // Initialize layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(passwordLabel, passwordField, submitButton);

        // Initialize scene
        scene = new Scene(layout, 300, 120);
        return scene;
    }

    private void validatePassword(String entered) {
        char[] serverPassword = serverUtils.getPassword();
    }

    /**
     * Displays a modal alert box for adding a participant.
     */
    public void displayAlertBox() {
        isOpen = true;
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Closes the modal alert box and clears up the input fields.
     */
    public void closeAlertBox() {
        isOpen = false;
        window.close();
    }

    /**
     * Getter for isOpen, true - window is open - false otherwise
     * @return value for isOpen
     */
    public boolean isOpen() {
        return isOpen;
    }

}
