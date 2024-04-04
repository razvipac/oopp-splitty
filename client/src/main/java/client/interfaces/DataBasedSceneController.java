package client.interfaces;

public interface DataBasedSceneController<T> {

    /**
     * Populates the scene with data of given type
     * @param data data to populate the scene with
     */
    void initialize(T data);

}
