package client.interfaces;

public interface VoidPopupController {

    /**
     * Returns whether the window is open
     * @return true iff open, false otherwise
     */
    boolean isOpen();

    /**
     * Opens the popup window
     */
    void openPopup();

    /**
     * Closes the popup window
     */
    void closePopup();

}
