package exceptions;

public class SubtaskNotFoundException extends RuntimeException {
    public SubtaskNotFoundException(String message) {
        super(message);
    }
}
