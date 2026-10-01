package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.Attributes;
import model.person.player.CareerStats;
import model.person.player.Player;

import java.io.Serial;
import java.time.LocalDate;
import static model.person.player.RatingCalculator.calculateRating;

public class Goalkeeper extends Player {
    @Serial
    private static final long serialVersionUID = 1L;

    public Goalkeeper(String name, LocalDate birthDate, String documentType, String documentNumber, Position position, GoalkeeperAttributes attributes, GoalkeeperCareerStats careerStats) {
        super(name, birthDate, documentType, documentNumber, position, calculateRating(attributes, careerStats, position),attributes, careerStats); //CALCULAR RATING

    }

}