package server.service.exceptions;

public class NotFoundInDatabaseException extends Exception{
    /**
     * Constructs a NotFoundInDatabaseException with the specified detail message.
     *
     * @param message The detail message.
     */
    public NotFoundInDatabaseException(String message) {
        super(message);
    }
}
