package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.Player;
import model.person.player.RatingCalculator;

import java.io.Serial;
import java.time.LocalDate;

public class FieldPlayer extends Player {
    @Serial
    private static final long serialVersionUID = 1L;
    private final FieldPlayerAttributes ATTRIBUTES;
    private final FieldPlayerCareerStats CAREERSTATS;

    public FieldPlayer(String name, LocalDate birthDate, String documentType, String documentNumber, Position position, FieldPlayerAttributes attributes, FieldPlayerCareerStats careerStats) {
        super(name, birthDate, documentType, documentNumber, position, RatingCalculator.calculateRating(attributes, careerStats, position));
        this.ATTRIBUTES = attributes;
        this.CAREERSTATS = careerStats;
    }


}
