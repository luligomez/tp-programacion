package model.person.player;

import model.person.Position;

import java.io.Serial;
import java.io.Serializable;

public abstract class CareerStats implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int MATCHESPLAYED;
    private final int EXPULSIONS;

    public CareerStats(int matchesPlayed, int expulsions) {
        this.MATCHESPLAYED = matchesPlayed;
        this.EXPULSIONS = expulsions;
    }

    public int getMATCHESPLAYED() {
        return MATCHESPLAYED;
    }

    public int getEXPULSIONS() {
        return EXPULSIONS;
    }

    public abstract double calculateScore(Position pos);

    protected double calculateDisciplinePenalty() {
        if (this.MATCHESPLAYED == 0) return 0.0;

        double expulsionRate = (double) this.EXPULSIONS / this.MATCHESPLAYED;
        double penalty = expulsionRate * 100.0;
        return Math.min(15.0, penalty); //max penalty: 15
    }



}
