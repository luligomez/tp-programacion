package model.exception;

public class InvalidTournamentException extends IllegalStateException {

    public InvalidTournamentException(String message) {
        super(message);
    }

    public InvalidTournamentException() {
        super("Invalid Tournament.");
    }
}