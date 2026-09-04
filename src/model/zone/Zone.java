package model.zone;

import model.Team;
import model.match.Formation;
import model.match.GroupStageMatch;
import model.person.Referee;
import model.place.Stadium;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.Tournament.TEAMS_PER_GROUP;

public class Zone {
    public static int TOTAL_MATCHES_PER_GROUP = 6;
    private ArrayList<TeamStanding> standings;
    private ArrayList<GroupStageMatch> groupStageMatches;

    public Zone() {
        this.standings = new  ArrayList<>();
        this.groupStageMatches = new ArrayList<>();
    }

    public ArrayList<Team> getTeams() {
        ArrayList<Team> teams = new ArrayList<>();
        for (TeamStanding s : standings) {
            teams.add(s.getTeam());
        }
        return teams;
    }

    public ArrayList<GroupStageMatch> getGroupStageMatches() {
        return groupStageMatches;
    }

    public void addTeam(Team team) {
        if (standings.size() < TEAMS_PER_GROUP) {
            standings.add(new TeamStanding(team));
        }
    }

    public void registerMatchResult(GroupStageMatch match) {

        TeamStanding team1Standing = findStanding(match.getTeam1());
        TeamStanding team2Standing = findStanding(match.getTeam2());

        if (team1Standing == null || team2Standing == null) {
            return;


        }
        groupStageMatches.add(match);
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
            return comparison;



        });
        resolveHeadToHeadTies(sortedStandings);


        return sortedStandings;
    }

    private void resolveHeadToHeadTies(ArrayList<TeamStanding> sorted) {
        int i = 0;
        while (i < sorted.size() - 1) {
            int j = i;
            while (j + 1 < sorted.size() && isFullyTied(sorted.get(i), sorted.get(j + 1))) {
                j++;
            }
            if (j > i) {
                List<TeamStanding> tiedGroup = sorted.subList(i, j + 1);
                    applyMultiWayHeadToHead(tiedGroup); // mini-tabla
            }
            i = j + 1;
        }
    }

    private boolean isFullyTied(TeamStanding s1, TeamStanding s2) {
        return s1.getPoints() == s2.getPoints()
                && s1.getGoalDifference() == s2.getGoalDifference()
                && s1.getGoalsFor() == s2.getGoalsFor();
    }

    private void applyMultiWayHeadToHead(List<TeamStanding> tiedGroup) {
        // Extraigo los equipos del bloque empatado, para filtrar los partidos entre ellos
        List<Team> tiedTeams = new ArrayList<>();
        for (TeamStanding standing : tiedGroup) {
            tiedTeams.add(standing.getTeam());
        }

        // Armo una mini-tabla, solo con puntos/goles de los partidos JUGADOS ENTRE ESTOS EQUIPOS
        Map<Team, Integer> miniPoints = new HashMap<>();
        Map<Team, Integer> miniGoalsFor = new HashMap<>();
        Map<Team, Integer> miniGoalsAgainst = new HashMap<>();

        for (Team team : tiedTeams) {
            miniPoints.put(team, 0);
            miniGoalsFor.put(team, 0);
            miniGoalsAgainst.put(team, 0);
        }

        for (GroupStageMatch match : groupStageMatches) {
            Team team1 = match.getTeam1();
            Team team2 = match.getTeam2();

            // solo cuenta si AMBOS equipos del partido están en el bloque empatado
            if (tiedTeams.contains(team1) && tiedTeams.contains(team2)) {

                int goals1 = match.getTeam1Goals();
                int goals2 = match.getTeam2Goals();

                miniGoalsFor.put(team1, miniGoalsFor.get(team1) + goals1);
                miniGoalsAgainst.put(team1, miniGoalsAgainst.get(team1) + goals2);
                miniGoalsFor.put(team2, miniGoalsFor.get(team2) + goals2);
                miniGoalsAgainst.put(team2, miniGoalsAgainst.get(team2) + goals1);

                if (goals1 > goals2) {
                    miniPoints.put(team1, miniPoints.get(team1) + 3);
                } else if (goals2 > goals1) {
                    miniPoints.put(team2, miniPoints.get(team2) + 3);
                } else {
                    miniPoints.put(team1, miniPoints.get(team1) + 1);
                    miniPoints.put(team2, miniPoints.get(team2) + 1);
                }
            }
        }

        // Ordeno el bloque empatado según la mini-tabla: puntos, luego diferencia de gol, luego goles a favor
        tiedGroup.sort((s1, s2) -> {
            Team t1 = s1.getTeam();
            Team t2 = s2.getTeam();

            int comparison = Integer.compare(miniPoints.get(t2), miniPoints.get(t1));
            if (comparison != 0) return comparison;

            int miniDiff1 = miniGoalsFor.get(t1) - miniGoalsAgainst.get(t1);
            int miniDiff2 = miniGoalsFor.get(t2) - miniGoalsAgainst.get(t2);
            comparison = Integer.compare(miniDiff2, miniDiff1);
            if (comparison != 0) return comparison;

            return Integer.compare(miniGoalsFor.get(t2), miniGoalsFor.get(t1));
        });
    }

    public void addMatch (GroupStageMatch match){
        if (groupStageMatches.size()<TOTAL_MATCHES_PER_GROUP)
            groupStageMatches.add(match);
    }

    public void generateMatches(ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        ArrayList<Team> teamsInZone = getTeams();

        for (int i = 0; i < teamsInZone.size(); i++) {
            for (int j = i + 1; j < teamsInZone.size(); j++) {
                Team team1 = teamsInZone.get(i);
                Team team2 = teamsInZone.get(j);
                Referee referee = selectValidReferee(team1, team2, referees);
                //asignar estadio una vez hecha la BD
                Stadium stadium = stadiums.isEmpty() ? null : stadiums.get(0);
                //la fecha como se le asigna???
                GroupStageMatch match = new GroupStageMatch(LocalDate.now(), team1, team2, referee, new Formation(), new Formation(), stadium, this);

                addMatch(match);
            }
        }
    }

    private Referee selectValidReferee(Team team1, Team team2, ArrayList<Referee> referees) {
        for (Referee ref : referees) {
            String refCountry = ref.getNationality().getName();
            String team1Country = team1.getCountry().getName();
            String team2Country = team2.getCountry().getName();
            if (!refCountry.equals(team1Country) && !refCountry.equals(team2Country)) {
                return ref;
            }
        }
        //se asume que siempre va a haber un referee valido??
        return referees.get(0);
    }

}
