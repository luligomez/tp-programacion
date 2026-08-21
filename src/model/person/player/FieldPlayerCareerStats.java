package model.person.player;

public class FieldPlayerCareerStats extends CareerStats{
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
}
