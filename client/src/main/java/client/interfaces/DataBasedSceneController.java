package client.interfaces;

import javafx.scene.Scene;

public interface DataBasedSceneController<T> {
    /**
     * Populates the scene with data of given type
     * @param data data to populate the scene with
     * @return the newly populated scene
     */
    Scene initialize(T data);

}
