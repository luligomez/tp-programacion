package test;

import model.FileReader;
import model.MatchSimulator;
import model.Tournament;
import model.match.*;
import model.zone.TeamStanding;
import model.zone.Zone;
import model.Team;


public class GroupStageDemo {

    public static void main(String[] args) throws Exception {

        Tournament tournament = FileReader.fileReader("torneo.json");

        tournament.zoneDraw();
        tournament.generateGroupStageMatches();


        System.out.println("===== PARTIDOS DE FASE DE GRUPOS =====");

        int zoneNumber = 1;

        for (Zone zone : tournament.getZones()) {

            System.out.println("\n--- ZONA " + zoneNumber + " ---");

            for (GroupStageMatch match : zone.getGroupStageMatches()) {

                System.out.println(
                        match.getTeam1().getName()
                                + " vs "
                                + match.getTeam2().getName()
                );
            }

            zoneNumber++;
        }


        System.out.println("\n===== SIMULANDO FASE DE GRUPOS =====");

        MatchSimulator.simulateMatchday(tournament, 1);
        MatchSimulator.simulateMatchday(tournament, 2);
        MatchSimulator.simulateMatchday(tournament, 3);



        System.out.println("\n===== TABLAS =====");

        zoneNumber = 1;

        for (Zone zone : tournament.getZones()) {

            System.out.println("\n--- ZONA " + zoneNumber + " ---");

            for (TeamStanding standing : zone.getSortedStandings()) {

                System.out.println(
                        standing.getTeam().getName()
                                + " | Puntos: "
                                + standing.getPoints()
                );
            }

            zoneNumber++;
        }



        System.out.println("\n===== 8 CLASIFICADOS =====");

        for (Team team : tournament.getQualifiedTeams()) {
            System.out.println(team.getName());
        }

        tournament.generateQuarterFinals();
        tournament.generateQuarterFinalSecondLegs();

        MatchSimulator.simulateQuarterFinals(tournament);



        System.out.println("\n===== GANADORES CUARTOS =====");

        for (Team team : tournament.getQuarterFinalWinners()) {
            System.out.println(team.getName());
        }



        System.out.println("\n===== CUARTOS IDA =====");

        for (FirstLegMatch match : tournament.getQuarterFinalMatches()) {

            System.out.println(
                    match.getTeam1().getName()
                            + " "
                            + match.getTeam1Goals()
                            + " - "
                            + match.getTeam2Goals()
                            + " "
                            + match.getTeam2().getName()
            );
        }



        System.out.println("\n===== CUARTOS VUELTA =====");

        for (SecondLegMatch match : tournament.getQuarterFinalSecondLegMatches()) {

            System.out.println(
                    match.getTeam1().getName()
                            + " "
                            + match.getTeam1Goals()
                            + " - "
                            + match.getTeam2Goals()
                            + " "
                            + match.getTeam2().getName()
            );
        }

        tournament.generateSemiFinals();
        tournament.generateSemiFinalSecondLegs();


        System.out.println("\n===== SEMIFINALES IDA =====");

        for (FirstLegMatch match : tournament.getSemiFinalMatches()) {

            System.out.println(
                    match.getTeam1().getName()
                            + " vs "
                            + match.getTeam2().getName()
            );
        }



        MatchSimulator.simulateSemiFinals(tournament);



        System.out.println("\n===== GANADORES SEMIFINALES =====");

        for (Team team : tournament.getSemiFinalWinners()) {
            System.out.println(team.getName());
        }



        System.out.println("\n===== SEMIFINALES VUELTA =====");

        for (SecondLegMatch match : tournament.getSemiFinalSecondLegMatches()) {

            System.out.println(
                    match.getTeam1().getName()
                            + " "
                            + match.getTeam1Goals()
                            + " - "
                            + match.getTeam2Goals()
                            + " "
                            + match.getTeam2().getName()
            );
        }


        tournament.generateFinal();

        System.out.println("\n===== SIMULANDO FINAL =====");

        for (Match match : tournament.getMatches()) {

            if (match instanceof FinalMatch) {

                MatchSimulator.simulateMatch(match);

                System.out.println(
                        match.getTeam1().getName()
                                + " "
                                + match.getTeam1Goals()
                                + " - "
                                + match.getTeam2Goals()
                                + " "
                                + match.getTeam2().getName()
                );

                System.out.println(
                        "CAMPEON: "
                                + match.getWinner().getName()
                );
            }
        }
        System.out.println("\n===== FINAL =====");

        for (Match match : tournament.getMatches()) {

            if (match instanceof FinalMatch) {

                System.out.println(
                        match.getTeam1().getName()
                                + " vs "
                                + match.getTeam2().getName()
                );
            }
        }

    }

}