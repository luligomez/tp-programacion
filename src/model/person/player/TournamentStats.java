package model.person.player;

public class TournamentStats {
    private int matchesPlayed;
    private int minutesPlayed;
    private int goals;
    private int penaltyGoals;
    private int standaloneYellowCards;
    private int doubleYellowExpulsions;
    private int directRedCards;

    public TournamentStats() {
        this.matchesPlayed = 0;
        this.minutesPlayed = 0;
        this.goals = 0;
        this.penaltyGoals = 0;
        this.standaloneYellowCards = 0;
        this.doubleYellowExpulsions = 0;
        this.directRedCards = 0;
    }

    public void registerGoal(boolean penalty) {
        this.goals++;
        if(penalty)
            penaltyGoals++;
    }
    public void registerMatchPlayed(int minutes) {
        this.matchesPlayed++;
        this.minutesPlayed += minutes;
    }
    public void registerStandaloneYellow() {
        standaloneYellowCards++;
    }
    public void revertStandaloneYellow() {
        standaloneYellowCards--;
    }
    public void registerDoubleYellowExpulsion() {
        doubleYellowExpulsions++;
    }
    public void registerDirectRed() {
        directRedCards++;
    }

}
