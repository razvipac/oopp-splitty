package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class AdminPasswordCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private PasswordField passwordField;

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
     * Clears passwordField.
     */
    public void refresh() {
        passwordField.clear();
    }

}
