package model.person.player;

import model.person.Person;
import model.person.Position;

import java.time.LocalDate;

public abstract class Player extends Person {
    private Position position;
    private int rating;
    private TournamentStats tournamentStats;

    public Player(String firstName, String lastName, LocalDate birthDate,
                  String documentType, String documentNumber,
                  Position position) {

        super(firstName, lastName, birthDate, documentType, documentNumber);

        this.position = position;
        this.rating = 0; //TODO CALCULAR RATING
        this.tournamentStats = new TournamentStats();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getRating() {
        return rating;
    }

}