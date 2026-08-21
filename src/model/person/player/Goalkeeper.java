package model.person.player;

import model.person.Position;

import java.time.LocalDate;

public class Goalkeeper extends Player {
    private final GoalkeeperAttributes ATTRIBUTES;
    private final GoalkeeperCareerStats CAREERSTATS;

    public Goalkeeper(String firstName, String lastName, LocalDate birthDate, String documentType, String documentNumber, Position position, GoalkeeperAttributes ATTRIBUTES, GoalkeeperCareerStats CAREERSTATS) {
        super(firstName, lastName, birthDate, documentType, documentNumber, position); //CALCULAR RATING
        this.ATTRIBUTES = ATTRIBUTES;
        this.CAREERSTATS = CAREERSTATS;
    }
}