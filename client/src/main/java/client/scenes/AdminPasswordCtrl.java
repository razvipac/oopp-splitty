package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.core.Response;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class AdminPasswordCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

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

}
