package client.interfaces;

public interface DualDataBasedSceneController<T, U> {

    /**
     * Populates the scene with data of given type
     * @param dataOne first data source to populate scene with
     * @param dataTwo second data source to populate scene with
     */
    void initialize(T dataOne, U dataTwo);

}
