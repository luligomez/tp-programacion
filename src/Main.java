
import model.Team;
import model.Tournament;
import model.match.Formation;
import model.match.GroupStageMatch;
import model.person.Referee;
import model.place.City;
import model.place.Stadium;
import model.zone.TeamStanding;
import model.zone.Zone;

import java.time.LocalDate;
import java.util.ArrayList;

import static model.FileReader.fileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Tournament tournament = fileReader("torneo.json");
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
        tournament.zoneDraw();
        for (Zone z : tournament.getZones()) {
            System.out.print("Zona 1: ");
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
        // OJO: el enunciado dice que el referí no puede tener la misma nacionalidad
        // que ninguno de los 2 equipos (salvo que ambos equipos compartan nacionalidad).
        // Si tu constructor de Match valida esto, elegí un referee que cumpla la condición,
        // o comentá temporalmente la validación para este test puntual.

        Formation formationA = new Formation(/* según tu constructor real */);
        Formation formationB = new Formation(/* según tu constructor real */);
        Stadium stadium = new Stadium("stadio",200,new City("Mardel", "Arg"));/* algún estadio ya cargado en el torneo, si tenés lista de estadios */

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

        System.out.println("=== Tabla ordenada ===");
        for (TeamStanding s : zone.getSortedStandings()) {
            System.out.printf("%-15s Pts:%d DG:%d GF:%d%n",
                    s.getTeam().getName(), s.getPoints(), s.getGoalDifference(), s.getGoalsFor());
        }
    }


}