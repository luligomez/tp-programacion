package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.Player;

import java.io.Serial;
import java.time.LocalDate;
import static model.person.player.RatingCalculator.calculateRating;

public class Goalkeeper extends Player {
    @Serial
    private static final long serialVersionUID = 1L;
    private final GoalkeeperAttributes ATTRIBUTES;
    private final GoalkeeperCareerStats CAREERSTATS;

    public Goalkeeper(String name, LocalDate birthDate, String documentType, String documentNumber, Position position, GoalkeeperAttributes attributes, GoalkeeperCareerStats careerStats) {
        super(name, birthDate, documentType, documentNumber, position, calculateRating(attributes, careerStats, position)); //CALCULAR RATING
        this.ATTRIBUTES = attributes;
        this.CAREERSTATS = careerStats;
    }


}