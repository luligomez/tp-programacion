package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.CareerStats;

import java.io.Serial;

public class FieldPlayerCareerStats extends CareerStats {
    @Serial
    private static final long serialVersionUID = 1L;
    private int goals;
    private int penalties;
    private int penaltiesScored;
    private int assists;

    public FieldPlayerCareerStats(int matchesPlayed, int expulsions, int goals, int penalties, int penaltiesScored, int assists) {
        super(matchesPlayed, expulsions);
        this.goals = goals;
        this.penalties = penalties;
        this.penaltiesScored = penaltiesScored;
        this.assists = assists;
    }

    public int getGoals() {
        return goals;
    }

    public int getPenalties() {
        return penalties;
    }

    public int getPenaltiesScored() {
        return penaltiesScored;
    }

    public int getAssists() {
        return assists;
    }

    @Override
    public double calculateScore(Position pos) {
        int matchesPlayed = getMATCHESPLAYED();
        if (matchesPlayed == 0) return 60.0;

        double goalsPerMatch = (double) this.goals / matchesPlayed;
        double assistsPerMatch = (double) this.assists / matchesPlayed;

        double penaltyEfficiency = 0.0;
        if (this.penalties > 0) {
            penaltyEfficiency = (double) this.penaltiesScored / this.penalties;
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
