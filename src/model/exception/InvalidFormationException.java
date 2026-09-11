package model.exception;

public class InvalidFormationException extends RuntimeException {
    public InvalidFormationException(String message) {
        super(message);
    }

    public InvalidFormationException() {
        super("Invalid Formation.");
    }

}
