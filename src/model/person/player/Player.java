package model.person.player;

import model.person.Person;
import model.person.Position;

import java.io.Serial;
import java.time.LocalDate;

public abstract class Player extends Person {
    @Serial
    private static final long serialVersionUID = 1L;

    private Position position;
    private final int RATING;
    private TournamentStats tournamentStats;


    public Player(String name, LocalDate birthDate,
                  String documentType, String documentNumber,
                  Position position, int rating) {

        super(name, birthDate, documentType, documentNumber);

        this.position = position;
        this.RATING = rating;
        this.tournamentStats = new TournamentStats();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getRating() {
        return RATING;
    }

    public TournamentStats getTournamentStats() {
        return tournamentStats;
    }

}