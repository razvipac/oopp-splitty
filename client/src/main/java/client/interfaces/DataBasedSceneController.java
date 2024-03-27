package client.interfaces;

import javafx.scene.Scene;

public interface DataBasedSceneController<T> {
    // Populates the scene with data of given type
    Scene initialize(T data);

}
