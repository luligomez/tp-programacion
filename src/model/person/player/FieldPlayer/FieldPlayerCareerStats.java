package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.CareerStats;

import java.io.Serial;

public class FieldPlayerCareerStats extends CareerStats {
    @Serial
    private static final long serialVersionUID = 2L;
    private final int GOALS;
    private final int PENALTIES;
    private final int PENALTIES_SCORED;
    private final int ASSISTS;

    public FieldPlayerCareerStats(int matchesPlayed, int expulsions, int goals, int penalties, int penaltiesScored, int assists) {
        super(matchesPlayed, expulsions);
        this.GOALS = goals;
        this.PENALTIES = penalties;
        this.PENALTIES_SCORED = penaltiesScored;
        this.ASSISTS = assists;
    }

    public int getGoals() {
        return GOALS;
    }

    public int getPenalties() {
        return PENALTIES;
    }

    public int getPenaltiesScored() {
        return PENALTIES_SCORED;
    }

    public int getAssists() {
        return ASSISTS;
    }

    @Override
    public double calculateScore(Position pos) {
        int matchesPlayed = getMatchesPlayed();
        if (matchesPlayed == 0) return 60.0;

        double goalsPerMatch = (double) this.GOALS / matchesPlayed;
        double assistsPerMatch = (double) this.ASSISTS / matchesPlayed;

        double penaltyEfficiency = 0.0;
        if (this.PENALTIES > 0) {
            penaltyEfficiency = (double) this.PENALTIES_SCORED / this.PENALTIES;
        }

        double basePerformance;

        switch (pos) {
            case DEFENDER:
                basePerformance = 70.0 + (goalsPerMatch * 40.0) + (assistsPerMatch * 30.0);
                break;

            case MIDFIELDER:
                basePerformance = 60.0 + (assistsPerMatch * 80.0) + (goalsPerMatch * 50.0) + (penaltyEfficiency * 5.0);
                break;

            case FORWARD:
                basePerformance = 45.0 + (goalsPerMatch * 100.0) + (assistsPerMatch * 30.0) + (penaltyEfficiency * 5.0);
                break;
            default:
                throw new IllegalArgumentException("Invalid position for FieldPlayer: " + pos);
        }

        double finalScore = basePerformance - calculateDisciplinePenalty();

        // Limit between 10 - 99
        return Math.max(10.0, Math.min(99.0, finalScore));
    }
}
