package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.Attributes;
import model.person.player.CareerStats;
import model.person.player.Player;
import model.person.player.RatingCalculator;

import java.io.Serial;
import java.time.LocalDate;

public class FieldPlayer extends Player {
    @Serial
    private static final long serialVersionUID = 1L;

    public FieldPlayer(String name, LocalDate birthDate, String documentType, String documentNumber, Position position, FieldPlayerAttributes attributes, FieldPlayerCareerStats careerStats) {
        super(name, birthDate, documentType, documentNumber, position, RatingCalculator.calculateRating(attributes, careerStats, position), attributes, careerStats);
    }

    public FieldPlayerAttributes getATTRIBUTES() {
        return null;
    }

}
