package model;

public class TeamStanding {

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

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesDrawn() {
        return matchesDrawn;
    }

    public void setMatchesDrawn(int matchesDrawn) {
        this.matchesDrawn = matchesDrawn;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void registerWin(int goalsFor, int goalsAgainst) {
        this.points += 3;
        this.matchesPlayed++;
        this.matchesWon++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }

    public void registerDraw(int goalsFor, int goalsAgainst) {
        this.points += 1;
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
    }
}