package model.person.player;

import java.io.Serial;
import java.io.Serializable;

public class TournamentStats implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int matchesPlayed;
    private int minutesPlayed;
    private int goals;
    private int penaltyGoals;
    private int standaloneYellowCards;
    private int doubleYellowExpulsions;
    private int directRedCards;
    private boolean suspended;

    public TournamentStats() {
        this.matchesPlayed = 0;
        this.minutesPlayed = 0;
        this.goals = 0;
        this.penaltyGoals = 0;
        this.standaloneYellowCards = 0;
        this.doubleYellowExpulsions = 0;
        this.directRedCards = 0;
        this.suspended = false;
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
        suspended = true;
    }
    public void registerDirectRed() {
        directRedCards++;
        suspended = true;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

    public int getGoals() {
        return goals;
    }

    public int getPenaltyGoals() {
        return penaltyGoals;
    }

    public int getStandaloneYellowCards() {
        return standaloneYellowCards;
    }

    public int getDoubleYellowExpulsions() {
        return doubleYellowExpulsions;
    }

    public int getDirectRedCards() {
        return directRedCards;
    }

    public boolean isSuspended() {
        return suspended;
    }

}
