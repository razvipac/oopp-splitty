package client.interfaces;

public interface StaticPopupController extends StaticSceneController {

    /**
     * Shows the popup window
     */
    void displayAlertBox();

    /**
     * Hides the popup window
     */
    void closeAlertBox();

    /**
     * Returns whether the window is open
     * @return true - open, false otherwise
     */
    boolean isOpen();

}
