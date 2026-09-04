package model.zone;

import model.Team;

import java.io.Serial;
import java.io.Serializable;

public class TeamStanding implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int POINTS_WIN = 3;
    private static final int POINTS_LOSE = 0;
    private static final int POINTS_DRAW = 1;
    private Team team;
    private int points;
    private int matchesPlayed;
    private int matchesWon;
    private int matchesDrawn;
    private int matchesLost;
    private int goalsFor;
    private int goalsAgainst;

    public TeamStanding(Team team) {
        this.team = team;
        this.points = 0;
        this.matchesPlayed = 0;
        this.matchesWon = 0;
        this.matchesDrawn = 0;
        this.matchesLost = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }

    public Team getTeam() {
        return team;
    }

    public int getPoints() {
        return points;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public int getMatchesDrawn() {
        return matchesDrawn;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void registerWin(int goalsFor, int goalsAgainst) {
        this.points += POINTS_WIN;
        this.matchesPlayed++;
        this.matchesWon++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }

    public void registerDraw(int goalsFor, int goalsAgainst) {
        this.points += POINTS_DRAW;
        this.matchesPlayed++;
        this.matchesDrawn++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }

    public void registerLoss(int goalsFor, int goalsAgainst) {
        this.matchesPlayed++;
        this.matchesLost++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        this.points += POINTS_LOSE; // 0
    }
}