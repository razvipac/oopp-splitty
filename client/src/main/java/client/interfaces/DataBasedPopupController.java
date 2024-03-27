package client.interfaces;

public interface DataBasedPopupController<T> extends DataBasedSceneController<T> {
    // Shows the popup window
    void displayAlertBox();

    // Hides the popup window
    void closeAlertBox();

    // Returns whether the window is open
    boolean isOpen();
}
