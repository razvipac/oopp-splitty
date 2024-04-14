package client.scenes;

import client.LanguageManager;
import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPasswordCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    private LanguageManager lm;

    @FXML
    private Button submitButton;

    @FXML
    private Text enterPasswordText;

    @FXML
    private Text authenticationRequired;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Text errorText;

    /**
     * Constructor for AdminPasswordCtrl.
     *
     * @param mainCtrl    MainCtrl class
     * @param serverUtils Global ServerUtils singleton
     */
    @Inject
    public AdminPasswordCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Initializes the scene
     * @param location passed URL location
     * @param resources passed ResourceBundle
     */
    public void initialize(URL location, ResourceBundle resources) {
        setLanguageForAllAdminPasswordCtrl();
    }

    /**
     * Refreshes the page.
     * Clears passwordField and hides the error text.
     */
    public void refresh() {
        passwordField.clear();
        errorText.setVisible(false);
    }

    /**
     * Checks if the entered password is correct. Closes this popup and switches scene to Admin
     * if it is.
     */
    @FXML
    public void submitPassword() {
        // TODO: Error handling
        if(serverUtils.matchesPassword(passwordField.getText())) {
            mainCtrl.setPasswordIsCorrect(true);
            mainCtrl.showAdmin();
            mainCtrl.closeAdminPasswordPopup();
        }
        else {
            errorText.setVisible(true);
        }
    }

    @FXML
    private void goBack(){
        mainCtrl.closeAdminPasswordPopup();
    }

    @FXML
    private void onGlobalKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            goBack();
        }
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.ENTER) {
            submitPassword();
        }
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.X) {
            mainCtrl.closeAdminPasswordPopup();
        }
    }

    /**
     * Sets the language for all elements in the administrator password control panel.
     * This method retrieves translations for various UI elements
     * from the LanguageManager and updates the corresponding
     * text or prompt text accordingly.
     */
    public void setLanguageForAllAdminPasswordCtrl(){
        if(lm == null){
            return;
        }
        submitButton.setText(lm.get("Submit"));
        authenticationRequired.setText(lm.get("Authentication Required"));
        enterPasswordText.setText(lm.get("Please enter the password to access the administrator control panel"));
        errorText.setText(lm.get("Incorrect password"));
        passwordField.setPromptText(lm.get("Enter password..."));
    }

}
