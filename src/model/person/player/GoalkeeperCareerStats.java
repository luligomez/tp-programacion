package model.person.player;

public class GoalkeeperCareerStats extends CareerStats{
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
}
