package server.service.exceptions;

public class NotFoundInDatabaseException extends Exception{
    public NotFoundInDatabaseException(String message) {
        super(message);
    }
}
