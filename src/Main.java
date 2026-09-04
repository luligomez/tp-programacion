
import model.MatchSimulator;
import model.Team;
import model.Tournament;
import model.match.Formation;
import model.match.GroupStageMatch;
import model.person.Referee;
import model.place.City;
import model.place.Stadium;
import model.zone.TeamStanding;
import model.zone.Zone;
import model.person.player.Player;


import java.time.LocalDate;
import java.util.ArrayList;

import static model.FileReader.fileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Tournament tournament = fileReader("torneo.json");
        tournament.zoneDraw();
        tournament.generateGroupStageMatches();
        simulateGroupStage(tournament);
        /*
        for(Team t : tournament.getTeams()) {
            System.out.println(t.getName() +" "+ t.getRankingPosition());
            for (Player p : t.getPlayers()) {
                System.out.println(p.getRating());
            }
        }
        for (Referee r : tournament.getReferees()) {
            System.out.println(r.getName());
        }*/

        /*int i=0;
        for (Zone z : tournament.getZones()) {
            i++;
            System.out.print("Zone %d:" + i);
            for (Team t : z.getTeams()) {
                System.out.print(t.getName()+" "+t.getRankingPosition()+" - ");

            }
            System.out.println();
        }

        Zone zone = tournament.getZones().get(0);

        ArrayList<Team> teams = zone.getTeams();
        Team teamA = teams.get(0);
        Team teamB = teams.get(1);
        Team teamC = teams.get(2);
        Team teamD = teams.get(3);

        Referee referee = tournament.getReferees().get(0);
        */

        // OJO: el enunciado dice que el referí no puede tener la misma nacionalidad
        // que ninguno de los 2 equipos (salvo que ambos equipos compartan nacionalidad).
        // Si tu constructor de Match valida esto, elegí un referee que cumpla la condición,
        // o comentá temporalmente la validación para este test puntual.
        /*
        Formation formationA = new Formation( según tu constructor real );
        Formation formationB = new Formation(según tu constructor real );
        Stadium stadium = new Stadium("stadio",200,new City("Mardel", "Arg")); algún estadio ya cargado en el torneo, si tenés lista de estadios

        LocalDate date = LocalDate.of(2026, 3, 1);
        System.out.println("A: "+teamA.getName()+" B:"+teamB.getName()+" C:"+teamC.getName()+" D: "+teamD.getName());
        /*
        GroupStageMatch match1 = new GroupStageMatch(date, teamA, teamB, referee, formationA, formationB, stadium, zone);
        match1.setResult(3, 0); // A 3 - 0 B

        GroupStageMatch match2 = new GroupStageMatch(date, teamC, teamA, referee, formationA, formationB, stadium, zone);
        match2.setResult(1, 0); // C 1 - 0 A

        GroupStageMatch match3 = new GroupStageMatch(date, teamB, teamC, referee, formationA, formationB, stadium, zone);
        match3.setResult(1, 0); // B 1 - 0 C

        GroupStageMatch match4 = new GroupStageMatch(date, teamA, teamD, referee, formationA, formationB, stadium, zone);
        match4.setResult(0, 5); // A 0 - 5 D

        GroupStageMatch match5 = new GroupStageMatch(date, teamB, teamD, referee, formationA, formationB, stadium, zone);
        match5.setResult(2, 3); // B 2 - 3 D

        GroupStageMatch match6 = new GroupStageMatch(date, teamC, teamD, referee, formationA, formationB, stadium, zone);
        match6.setResult(2, 5); // C 2 - 5 D
         */
        /*
        GroupStageMatch match1 = new GroupStageMatch(date, teamA, teamB, referee, formationA, formationB, stadium, zone);
        match1.setResult(2, 0); // A 2 - 0 B

        GroupStageMatch match2 = new GroupStageMatch(date, teamC, teamA, referee, formationA, formationB, stadium, zone);
        match2.setResult(2, 0); // C 2 - 0 A

        GroupStageMatch match3 = new GroupStageMatch(date, teamB, teamC, referee, formationA, formationB, stadium, zone);
        match3.setResult(2, 0); // B 2 - 0 C

        GroupStageMatch match4 = new GroupStageMatch(date, teamA, teamD, referee, formationA, formationB, stadium, zone);
        match4.setResult(0, 3); // A 0 - 3 D

        GroupStageMatch match5 = new GroupStageMatch(date, teamB, teamD, referee, formationA, formationB, stadium, zone);
        match5.setResult(0, 3); // B 0 - 3 D

        GroupStageMatch match6 = new GroupStageMatch(date, teamC, teamD, referee, formationA, formationB, stadium, zone);
        match6.setResult(0, 3); // C 0 - 3 D

        zone.registerMatchResult(match1);
        zone.registerMatchResult(match2);
        zone.registerMatchResult(match3);
        zone.registerMatchResult(match4);
        zone.registerMatchResult(match5);
        zone.registerMatchResult(match6);

        System.out.println("=== Sorted Standings ===\n");
        for (TeamStanding s : zone.getSortedStandings()) {
            System.out.printf("%-15s Pts:%d GD:%d GF:%d%n",
                    s.getTeam().getName(), s.getPoints(), s.getGoalDifference(), s.getGoalsFor());
        }
        */
    }

    private static void simulateGroupStage(Tournament tournament){
        int i=0;
        for (Zone zone : tournament.getZones()) {
            i++;
            System.out.println("\n=== ZONE " + i + " ===");
            /*Para los partidos de la fase inicial (zonas) se deberá mostrar el estado de la tabla de
            posiciones antes y después de registrado el resultado. Para cada equipo,
            mostrar: puntos, partidos jugados, ganados, empatados y perdidos, goles a favor
            y en contra, diferencia de gol. */
            //se muestra antes y despues de cata partido o despues de que hayan terminado todos los de la zona???
            //en la tabla inicial las posiciones como se definen? por el ranking? o es lo mismo en cualquier posicion?

            // Tabla ANTES de los partidos
            System.out.println("=== Previous Sorted Standings ===");
            printTable(zone.getStandings());

            // simular partidos
            System.out.println("\n=== MATCHES ===");
            for (GroupStageMatch match : zone.getGroupStageMatches()) {
                System.out.println(match.getTeam1().getName() + " vs " + match.getTeam2().getName());
                //  registrar resultados!!!!!!
            }
        }
        MatchSimulator.simulateMatchday(tournament,1);
        MatchSimulator.simulateMatchday(tournament,2);
        MatchSimulator.simulateMatchday(tournament,3);
        for (Zone zone : tournament.getZones()) {
            // tabla DESPUÉS de los partidos con todos los datos
            System.out.println("\n=== Sorted Standings ===");
            printTable(zone.getSortedStandings());
        }
        System.out.println("\n=== GOLEADORES ===");

        for (Team team : tournament.getTeams()) {

            for (Player player : team.getPlayers()) {

                int goals = player.getTournamentStats().getGoals();

                if (goals > 0) {
                    System.out.println(
                            player.getName() + " - " + goals + " goles"
                    );
                }
            }
        }
        System.out.println("\n=== MINUTOS JUGADOS ===");

        for (Team team : tournament.getTeams()) {

            for (Player player : team.getPlayers()) {

                int minutes = player.getTournamentStats().getMinutesPlayed();

                if (minutes > 0) {
                    System.out.println(
                            player.getName() + " - " + minutes + " minutos"
                    );
                }
            }
        }
    }
    //es de prueba, despues poner en la interfaz
    private static void printTable(ArrayList<TeamStanding> standings) {
        for (TeamStanding s : standings) {
            System.out.println(s.getTeam().getName() + " - Pts: " + s.getPoints() +
                    " PJ: " + s.getMatchesPlayed() + " PG: " + s.getMatchesWon() +
                    " PE: " + s.getMatchesDrawn() + " PP: " + s.getMatchesLost() +
                    " GF: " + s.getGoalsFor() + " GC: " + s.getGoalsAgainst());
        }
    }
}