package server.service.exceptions;

public class ImproperDumpFormatException extends Exception{

    /**
     * Constructs an ImproperDumpFormatException with the specified detail message.
     *
     * @param message The detail message.
     */
    public ImproperDumpFormatException(String message) {
        super(message);
    }
}
