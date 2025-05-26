package exceptions;

public class TaskIntersectException extends RuntimeException {
    public TaskIntersectException(String message, Throwable cause) {
        super(message, cause);
    }
}
