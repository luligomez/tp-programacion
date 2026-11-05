package model.match.knockout;

import model.tournament.knockout.KnockoutPhase;
import service.FormationCreator;
import model.team.Team;
import model.match.Formation;
import model.match.Match;
import model.person.Referee;
import model.place.Stadium;
import service.MatchResourceService;

import java.io.Serializable;
import java.util.ArrayList;

public class KnockoutTie implements Serializable {
    private KnockoutPhase phase;
    private Team team1;
    private Team team2;
    private FirstLegMatch firstLeg;
    private SecondLegMatch secondLeg;

    public KnockoutTie(KnockoutPhase phase, Team team1, Team team2, ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        this.phase = phase;
        this.team1 = team1;
        this.team2 = team2;
        initTie(referees, stadiums);
    }

    private void initTie(ArrayList<Referee> referees, ArrayList<Stadium> stadiums){
        // The tie is in charge of creating the first and second legs with swapped home teams
        Referee ref1 = MatchResourceService.pickValidReferee(team1, team2, referees);
        Stadium st1 = MatchResourceService.pickRandomUnusedStadium(stadiums);
        Formation formation1 = FormationCreator.createAutomaticFormation(team1);
        Formation formation2 = FormationCreator.createAutomaticFormation(team2);
        this.firstLeg = new FirstLegMatch(team1, team2, ref1, formation1, formation2, st1);

        Referee ref2 = MatchResourceService.pickValidReferee(team2, team1, referees);
        Stadium st2 = MatchResourceService.pickRandomUnusedStadium(stadiums);
        this.secondLeg = new SecondLegMatch(team2, team1, ref2, st2);
    }

    public KnockoutPhase getPhase() { return phase; }
    public Team getTeam1() { return team1; }
    public Team getTeam2() { return team2; }
    public FirstLegMatch getFirstLeg() { return firstLeg; }
    public SecondLegMatch getSecondLeg() { return secondLeg; }


    public boolean isResolved() {
        return secondLeg != null && secondLeg.isPlayed() && getWinner()!=null;
    }

    public boolean isSecondLegPlayed() {
        return secondLeg != null && secondLeg.isPlayed();
    }

    public Team getWinner() {
        if (!isSecondLegPlayed()) {
            return null;
        }

        // 1. Points over both legs (win = 3, draw = 1, loss = 0)
        int points1 = getTeam1Points();
        int points2 = getTeam2Points();
        if (points1 != points2) {
            return points1 > points2 ? team1 : team2;
        }

        // 2. Goal difference, away goals count double
        int goalDifference = getTeam1WeightedGoalDifference();
        if (goalDifference != 0) {
            return goalDifference > 0 ? team1 : team2;
        }

        // 3. Penalties
        if (secondLeg.getPenalties() != null) {
            return secondLeg.getPenalties().getWinner();
        }

        return null;
    }

    public String getWinningCriteria() {
        if (!isResolved()) return "";

        if (getTeam1Points() != getTeam2Points()) return "Points";
        if (getTeam1WeightedGoalDifference() != 0) return "Goal difference (away goals count double)";
        if (secondLeg.getPenalties() != null) return "Penalties";

        return "";
    }

    public int getTeam1Points() {
        return pointsOf(getTeam1FirstLegGoals(), getTeam2FirstLegGoals())
                + pointsOf(getTeam1SecondLegGoals(), getTeam2SecondLegGoals());
    }

    public int getTeam2Points() {
        return pointsOf(getTeam2FirstLegGoals(), getTeam1FirstLegGoals())
                + pointsOf(getTeam2SecondLegGoals(), getTeam1SecondLegGoals());
    }

    // team1 is away in the second leg, so its goals there count double.
// team2 is away in the first leg, so its goals there count double.
    public int getTeam1WeightedGoalDifference() {
        int goalsFor = getTeam1FirstLegGoals() + 2 * getTeam1SecondLegGoals();
        int goalsAgainst = 2 * getTeam2FirstLegGoals() + getTeam2SecondLegGoals();
        return goalsFor - goalsAgainst;
    }

    private int pointsOf(int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) return 3;
        if (goalsFor == goalsAgainst) return 1;
        return 0;
    }

    public int getTeam1AggregateGoals() {
        return firstLeg.getTeam1Goals() + secondLeg.getTeam2Goals();
    }

    public int getTeam2AggregateGoals() {
        return firstLeg.getTeam2Goals() + secondLeg.getTeam1Goals();
    }

    public int getTeam1AwayGoals() {
        return secondLeg.getTeam2Goals();
    }

    public int getTeam2AwayGoals() {
        return firstLeg.getTeam2Goals();
    }

    public int getTeam1FirstLegGoals() {
        return firstLeg.getTeam1Goals();
    }

    public int getTeam2FirstLegGoals() {
        return firstLeg.getTeam2Goals();
    }

    public int getTeam1SecondLegGoals() {
        return secondLeg.getTeam2Goals();
    }

    public int getTeam2SecondLegGoals() {
        return secondLeg.getTeam1Goals();
    }

    public int getFirstLegGoals(Team team) {
        return goalsOf(firstLeg, team);
    }

    public int getSecondLegGoals(Team team) {
        return goalsOf(secondLeg, team);
    }

    public int getTotalGoals(Team team) {
        return getFirstLegGoals(team) + getSecondLegGoals(team);
    }

    private int goalsOf(Match match, Team team) {
        if (!match.isPlayed()) return 0;
        return match.getTeam1().equals(team) ? match.getTeam1Goals() : match.getTeam2Goals();
    }

}