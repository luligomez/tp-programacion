package model;

import java.util.ArrayList;
import java.util.Comparator;

public class Zone {
    private ArrayList<Team> teams;
    private ArrayList<TeamStanding> standings;
    private ArrayList<GroupStageMatch> matches;
    private ArrayList<ZoneMatch> matches;

    public Zone() {

        this.teams = new ArrayList<>();
        this.standings = new  ArrayList<>();
        this.matches = new ArrayList<>();
        this.matches = new ArrayList<>(6);
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<ZoneMatch> getMatches() {
        return matches;
    }

    public void addTeam(Team team) {
        if (teams.size() < 4) {
            teams.add(team);
            standings.add(new TeamStanding(team));
        }
    }

    public void registerMatchResult(GroupStageMatch match) {

        TeamStanding team1Standing = findStanding(match.getTeam1());
        TeamStanding team2Standing = findStanding(match.getTeam2());

        if (team1Standing == null || team2Standing == null) {
            return;


        }
        matches.add(match);
        int team1Goals = match.getTeam1Goals();
        int team2Goals = match.getTeam2Goals();

        if (team1Goals > team2Goals) {

            team1Standing.registerWin(team1Goals, team2Goals);
            team2Standing.registerLoss(team2Goals, team1Goals);

        } else if (team2Goals > team1Goals) {

            team2Standing.registerWin(team2Goals, team1Goals);
            team1Standing.registerLoss(team1Goals, team2Goals);

        } else {

            team1Standing.registerDraw(team1Goals, team2Goals);
            team2Standing.registerDraw(team2Goals, team1Goals);
        }
    }

    private TeamStanding findStanding(Team team) {

        for (TeamStanding standing : standings) {

            if (standing.getTeam() == team) {
                return standing;
            }
        }

        return null;
    }

    public ArrayList<TeamStanding> getStandings() {
        return standings;
    }
    public ArrayList<TeamStanding> getSortedStandings() {

        ArrayList<TeamStanding> sortedStandings = new ArrayList<>(standings);

        sortedStandings.sort((standing1, standing2) -> {

            int comparison = Integer.compare(
                    standing2.getPoints(),
                    standing1.getPoints()
            );

            if (comparison != 0) {
                return comparison;
            }

            comparison = Integer.compare(
                    standing2.getGoalDifference(),
                    standing1.getGoalDifference()
            );

            if (comparison != 0) {
                return comparison;
            }

            comparison = Integer.compare(
                    standing2.getGoalsFor(),
                    standing1.getGoalsFor()
            );

            if (comparison != 0) {
                return comparison;
            }

            return compareHeadToHead(
                    standing1.getTeam(),
                    standing2.getTeam()
            );
        });

        return sortedStandings;
    }
    private int compareHeadToHead(Team firstTeam, Team secondTeam) {

        for (GroupStageMatch match : matches) {

            if (match.getTeam1() == firstTeam &&
                    match.getTeam2() == secondTeam) {

                if (match.getTeam1Goals() > match.getTeam2Goals()) {
                    return -1;
                }

                if (match.getTeam1Goals() < match.getTeam2Goals()) {
                    return 1;
                }

                return 0;
            }

            if (match.getTeam1() == secondTeam &&
                    match.getTeam2() == firstTeam) {

                if (match.getTeam2Goals() > match.getTeam1Goals()) {
                    return -1;
                }

                if (match.getTeam2Goals() < match.getTeam1Goals()) {
                    return 1;
                }

                return 0;
            }
        }

        return 0;
    }

    public void addMatch (ZoneMatch match){
        if (matches.size()<6)
            matches.add(match);
    }
}
