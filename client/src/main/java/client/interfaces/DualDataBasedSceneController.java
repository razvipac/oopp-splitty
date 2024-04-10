package client.interfaces;

import javafx.fxml.Initializable;

public interface DualDataBasedSceneController<T, U> extends Initializable {

    /**
     * Populates the scene with data of given type
     * @param dataOne first data source to populate scene with
     * @param dataTwo second data source to populate scene with
     */
    void refresh(T dataOne, U dataTwo);

}
