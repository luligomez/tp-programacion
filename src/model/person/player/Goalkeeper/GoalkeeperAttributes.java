package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.Attributes;

import java.io.Serial;

public class GoalkeeperAttributes extends Attributes {
    @Serial
    private static final long serialVersionUID = 2L;
    private final int REFLEXES;
    private final int AERIALGAME;
    private final int POSITIONING;
    private final int RUSHINGOUT;
    private final int HANDLING;
    private final int FOOTWORK;

    public GoalkeeperAttributes(int reflexes, int aerialgame, int positioning, int rushingout, int handling, int footwork) {
        this.REFLEXES = reflexes;
        this.AERIALGAME = aerialgame;
        this.POSITIONING = positioning;
        this.RUSHINGOUT = rushingout;
        this.HANDLING = handling;
        this.FOOTWORK = footwork;
    }

    public int getReflexes() {
        return REFLEXES;
    }

    public int getAerialGame() {
        return AERIALGAME;
    }

    public int getPositioning() {
        return POSITIONING;
    }

    public int getRushingout() {
        return RUSHINGOUT;
    }

    public int getHandling() {
        return HANDLING;
    }

    public int getFootwork() {
        return FOOTWORK;
    }

    @Override
    public double calculateScore(Position position) {
        if(position.equals(Position.GOALKEEPER)){
            return (int) Math.round(
                    REFLEXES * 0.25 + POSITIONING * 0.20 + HANDLING * 0.20 +
                            AERIALGAME * 0.15 + FOOTWORK * 0.10 + RUSHINGOUT * 0.10
            );
        }
        throw new IllegalArgumentException("Invalid position for Goalkeeper: " + position);
    }

    @Override
    public double calculatePenaltyConversionChance() {
        return 0.35 + (FOOTWORK / 250.0);
    }

    @Override
    public double calculateGoalScoringWeight(Position position) {
        return 0.0001;
    }
}
