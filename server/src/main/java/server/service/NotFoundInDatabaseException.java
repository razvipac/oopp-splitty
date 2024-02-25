package server.service;

public class NotFoundInDatabaseException extends Exception{
    public NotFoundInDatabaseException(String message) {
        super(message);
    }
}
