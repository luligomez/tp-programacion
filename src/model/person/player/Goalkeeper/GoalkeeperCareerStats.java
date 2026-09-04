package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.CareerStats;

import java.io.Serial;

public class GoalkeeperCareerStats extends CareerStats {
    @Serial
    private static final long serialVersionUID = 1L;
    private int goalsReceived;
    private int penaltiesReceived;
    private int penaltiesSaved;

    public GoalkeeperCareerStats(int carreerMatchesPlayed, int careerExpulsions, int goalsReceived, int penaltiesReceived, int penaltiesSaved) {
        super(carreerMatchesPlayed, careerExpulsions);
        this.goalsReceived = goalsReceived;
        this.penaltiesReceived = penaltiesReceived;
        this.penaltiesSaved = penaltiesSaved;
    }

    public int getGoalsReceived() {
        return goalsReceived;
    }

    public int getPenaltiesReceived() {
        return penaltiesReceived;
    }

    public int getPenaltiesSaved() {
        return penaltiesSaved;
    }

    @Override
    public double calculateScore(Position pos) {
        if (pos.equals(Position.GOALKEEPER)) {
            int matchesPlayed = getMATCHESPLAYED();
            if (matchesPlayed == 0)
                return 60.0;

            // 1. average goals per match
            double goalsPerMatch = (double) this.goalsReceived / matchesPlayed;
            double goalsScore = 100.0 - (goalsPerMatch * 20.0);

            // 2. penalty bonus
            double penaltyBonus = 0.0;
            if (this.penaltiesReceived > 0) {
                double saveRate = (double) this.penaltiesSaved / this.penaltiesReceived;
                penaltyBonus = saveRate * 15.0;
            }

            double totalScore = (goalsScore * 0.85) + (penaltyBonus) - calculateDisciplinePenalty();
            // Limit between 10 - 99
            return Math.max(10.0, Math.min(99.0, totalScore));
        }
            throw new IllegalArgumentException("Invalid position for Goalkeeper: " + pos);
    }
}
