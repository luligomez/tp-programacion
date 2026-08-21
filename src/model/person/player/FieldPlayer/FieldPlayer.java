package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.Player;
import model.person.player.RatingCalculator;

import java.time.LocalDate;

public class FieldPlayer extends Player {
    private final FieldPlayerAttributes ATTRIBUTES;
    private final FieldPlayerCareerStats CAREERSTATS;

    public FieldPlayer(String firstName, String lastName, LocalDate birthDate, String documentType, String documentNumber, Position position, FieldPlayerAttributes attributes, FieldPlayerCareerStats careerStats) {
        super(firstName, lastName, birthDate, documentType, documentNumber, position, RatingCalculator.calculateRating(attributes, careerStats, position));
        this.ATTRIBUTES = attributes;
        this.CAREERSTATS = careerStats;
    }


}
