package client.interfaces;

import javafx.fxml.Initializable;

public interface DataBasedSceneController<T> extends Initializable {
    /**
     * Refreshes the scene with fresh data
     * @param data additional data to refresh the scene
     */
    void refresh(T data);
}