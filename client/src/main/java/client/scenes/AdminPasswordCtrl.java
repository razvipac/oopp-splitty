package client.scenes;

import client.interfaces.VoidSceneController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AdminPasswordCtrl implements VoidSceneController {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField passwordField;

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
