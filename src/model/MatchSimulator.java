package model;

import model.match.GroupStageMatch;
import model.match.Match;
import model.zone.Zone;

public class MatchSimulator {

    // simulacion de fase de grupos
    public static void simulateGroupStageMatches(Tournament tournament) {
        for (Zone zone : tournament.getZones()) {
            for (GroupStageMatch match : zone.getGroupStageMatches()) {
                simulateMatch(match);
                zone.registerMatchResult(match);
            }
        }
    }

    public static void simulateQuarterFinals(Tournament tournament) {

    }

    public static void simulateSemifinals(Tournament tournament) {
        // ...
    }

    public static void simulateFinal(Tournament tournament) {

    }

    public static void simulateMatch(Match match) {
        Team team1 = match.getTeam1();
        Team team2 = match.getTeam2();

        //
    }
}
