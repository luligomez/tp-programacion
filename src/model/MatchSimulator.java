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

    //simular X fecha de la fase de grupos, de cada zona (fecha 1, 2, o 3)
    public static void simulateMatchday(Tournament tournament, int matchday) {
        for (Zone zone : tournament.getZones()) {
            GroupStageMatch match1;
            GroupStageMatch match2;

            switch (matchday) {
                case 1:
                    match1 = zone.getGroupStageMatches().get(0);
                    match2 = zone.getGroupStageMatches().get(5);
                    break;
                case 2:
                    match1 = zone.getGroupStageMatches().get(1);
                    match2 = zone.getGroupStageMatches().get(4);
                    break;
                case 3:
                    match1 = zone.getGroupStageMatches().get(2);
                    match2 = zone.getGroupStageMatches().get(3);
                    break;
                default:
                    throw new RuntimeException("Invalid matchday.");
            }

            // Simulamos y registramos
            simulateMatch(match1);
            zone.registerMatchResult(match1);

            simulateMatch(match2);
            zone.registerMatchResult(match2);
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
        match.setResult(2,0); // TODO *datos de prueba

    }
}
