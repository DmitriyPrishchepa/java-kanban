package exceptions;

public class ManagerLoadFromFileException extends RuntimeException {

    public ManagerLoadFromFileException(final String message, Throwable cause) {
        super(message, cause);
    }
}
