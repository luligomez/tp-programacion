package model.exception;

public class InvalidMatchResourceException extends IllegalStateException {

    public InvalidMatchResourceException(String message) {
        super(message);
    }

    public InvalidMatchResourceException() {
        super("Invalid match resource.");
    }
}