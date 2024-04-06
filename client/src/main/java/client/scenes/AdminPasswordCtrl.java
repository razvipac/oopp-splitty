package client.scenes;

import client.LanguageManager;
import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class AdminPasswordCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
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
     * Initialized the Scene.
     */
    public void initialize() {
        setLanguageForAllAdminPasswordCtrl();
        refresh();

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

    public void setLanguageForAllAdminPasswordCtrl(){
        LanguageManager lm = mainCtrl.getLanguageManager();
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
