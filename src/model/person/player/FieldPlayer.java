package model.person.player;

import model.person.Position;

import java.time.LocalDate;

public class FieldPlayer extends Player {
    //attributes
    private final FieldPlayerAttributes ATTRIBUTES;
    private final FieldPlayerCareerStats CAREERSTATS;

    public FieldPlayer(String firstName, String lastName, LocalDate birthDate, String documentType, String documentNumber, Position position, FieldPlayerAttributes ATTRIBUTES, FieldPlayerCareerStats CAREERSTATS) {
        super(firstName, lastName, birthDate, documentType, documentNumber, position); //CALCULAR RATING
        this.ATTRIBUTES = ATTRIBUTES;
        this.CAREERSTATS = CAREERSTATS;
    }
}
