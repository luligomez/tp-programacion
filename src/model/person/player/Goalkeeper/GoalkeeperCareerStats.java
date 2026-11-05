package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.CareerStats;

import java.io.Serial;

public class GoalkeeperCareerStats extends CareerStats {
    @Serial
    private static final long serialVersionUID = 2L;
    private final int GOALS_RECEIVED;
    private final int PENALTIES_RECEIVED;
    private final int PENALTIES_SAVED;

    public GoalkeeperCareerStats(int careerMatchesPlayed, int careerExpulsions, int goalsReceived, int penaltiesReceived, int penaltiesSaved) {
        super(careerMatchesPlayed, careerExpulsions);
        this.GOALS_RECEIVED = goalsReceived;
        this.PENALTIES_RECEIVED = penaltiesReceived;
        this.PENALTIES_SAVED = penaltiesSaved;
    }

    public int getGoalsReceived() {
        return GOALS_RECEIVED;
    }

    public int getPenaltiesReceived() {
        return PENALTIES_RECEIVED;
    }

    public int getPenaltiesSaved() {
        return PENALTIES_SAVED;
    }

    @Override
    public double calculateScore(Position pos) {
        if (pos.equals(Position.GOALKEEPER)) {
            int matchesPlayed = getMatchesPlayed();
            if (matchesPlayed == 0)
                return 60.0;

            // 1. average goals per match
            double goalsPerMatch = (double) this.GOALS_RECEIVED / matchesPlayed;
            double goalsScore = 100.0 - (goalsPerMatch * 20.0);

            // 2. penalty bonus
            double penaltyBonus = 0.0;
            if (this.PENALTIES_RECEIVED > 0) {
                double saveRate = (double) this.PENALTIES_SAVED / this.PENALTIES_RECEIVED;
                penaltyBonus = saveRate * 15.0;
            }

            double totalScore = (goalsScore * 0.85) + (penaltyBonus) - calculateDisciplinePenalty();
            // Limit between 10 - 99
            return Math.max(10.0, Math.min(99.0, totalScore));
        }
            throw new IllegalArgumentException("Invalid position for Goalkeeper: " + pos);
    }
}
