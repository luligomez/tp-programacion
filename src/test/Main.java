package test;

import model.Tournament;
// Importá tus clases de modelo necesarias para inicializar el torneo...

import model.MatchSimulator;
import model.Team;
import model.match.GroupStageMatch;
import model.zone.TeamStanding;
import model.zone.Zone;
import model.person.player.Player;
import model.match.incident.*;
import model.match.*;
import view.mainwindow.MainWindowView;


import javax.swing.*;
import java.util.ArrayList;

import static model.MatchSimulator.simulateMatch;

public class Main {
    public static void main(String[] args) throws Exception {
        testUI();
/*
        Tournament tournament = fileReader("torneo.json");
        StadiumLoader loader = new StadiumLoader();
        tournament.addAllStadiums(loader.loadStadiums());
        tournament.zoneDraw();
        tournament.generateGroupStageMatches();
        //simulateGroupStage(tournament);
        // ⚽ Probar un único partido y ver sus incidencias detalladas
        //testSingleMatch(tournament);
        testGroupMatch(tournament,0);
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
    private static void testSingleMatch(Tournament tournament) {
        // 1. Obtener el primer partido de la primera zona
        Zone zone = tournament.getZones().get(0);
        GroupStageMatch match = zone.getGroupStageMatches().get(0);

        System.out.println("==================================================");
        System.out.println("  TEST DE SIMULACIÓN DE UN PARTIDO INDIVIDUAL");
        System.out.println("==================================================");
        System.out.println("Encuentro: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
        if (match.getReferee() != null) {
            System.out.println("Árbitro: " + match.getReferee().getName());
        }
        if (match.getStadium() != null) {
            System.out.println("Estadio: " + match.getStadium().getName());
        }
        System.out.println("--------------------------------------------------");

        // 2. Ejecutar la simulación del partido
        simulateMatch(match);

        // 3. Resultado final
        System.out.println("\n[RESULTADO FINAL]");
        System.out.println("(" + match.getTeam1().getRankingPosition() + ")"+match.getTeam1().getName() + " " + match.getTeam1Goals() + " - "
                + match.getTeam2Goals() + " " + match.getTeam2().getName()+ "(" + match.getTeam2().getRankingPosition()+")");

        // 4. Ordenar y listar todas las incidencias por minuto cronológico
        ArrayList<Incident> incidents = new ArrayList<>(match.getIncidents());
        incidents.sort((i1, i2) -> Integer.compare(i1.getMinute(), i2.getMinute()));

        System.out.println("\n=== INCIDENCIAS DEL PARTIDO (" + incidents.size() + " en total) ===");

        for (Incident incident : incidents) {
            int min = incident.getMinute();

            if (incident instanceof Goal) {
                Goal g = (Goal) incident;
                String detail = g.isOwnGoal() ? " (En Contra)" : (g.isPenalty() ? " (Penal)" : "");
                String gkInfo = (g.getGoalkeeper() != null) ? " [Arquero rival: " + g.getGoalkeeper().getName() + "]" : "";
                System.out.printf("Min %2d' | ⚽ GOL de %s %s%s%n",
                        min, g.getScorer().getName(), detail, gkInfo);

            } else if (incident instanceof Substitution) {
                Substitution sub = (Substitution) incident;
                System.out.printf("Min %2d' | 🔄 CAMBIO: Sale %s ➔ Entra %s %n",
                        min, sub.getPlayerOut().getName(),
                        sub.getPlayerIn().getName());

            } else if (incident instanceof Expulsion) {
                Expulsion exp = (Expulsion) incident;
                System.out.printf("Min %2d' | 🟥 EXPULSIÓN: %s %n",
                        min, exp.getPlayer().getName());

            } else if (incident instanceof YellowCard) {
                YellowCard yc = (YellowCard) incident;
                System.out.printf("Min %2d' | 🟨 AMARILLA: %s %n",
                        min, yc.getPlayer().getName());

            } else if (incident instanceof PenaltyTaken) {
                PenaltyTaken pt = (PenaltyTaken) incident;
                String estado = pt.isScored() ? "Convertido" : "Errado/Atajado";
                System.out.printf("Min %2d' | 🥅 PENAL EJECUTADO por %s: %s%n",
                        min, pt.getPlayer().getName(), estado);
            }
        }
        // 5. Revisar las participaciones y minutos jugados
        System.out.println("\n=== PARTICIPACIONES (PlayerParticipations) ===");
        for (PlayerParticipation pp : match.getPlayerParticipations()) {
            if (pp.getMinuteIn() != -1) { // Filtra solo a los que jugaron
                int played = pp.getMinuteOut() - pp.getMinuteIn();
                System.out.printf("- %-20s | Titular: %-5b | Entró: %2d' | Salió: %2d' | Jugó: %2d min%n",
                        pp.getPlayer().getName(),
                        pp.isStarter(), pp.getMinuteIn(), pp.getMinuteOut(), played);
            }
        }


    }
    private static void testGroupMatch(Tournament tournament, int group) {
        for(int i=0; i<6;i++) {

            // 1. Obtener el primer partido de la primera zona
            Zone zone = tournament.getZones().get(group);
            GroupStageMatch match = zone.getGroupStageMatches().get(i);

            System.out.println("==================================================");
            System.out.println("  TEST DE SIMULACIÓN DE UN PARTIDO INDIVIDUAL "+i);
            System.out.println("==================================================");
            System.out.println("Encuentro: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
            if (match.getReferee() != null) {
                System.out.println("Árbitro: " + match.getReferee().getName());
            }
            if (match.getStadium() != null) {
                System.out.println("Estadio: " + match.getStadium().getName());
            }
            System.out.println("--------------------------------------------------");

            // 2. Ejecutar la simulación del partido
            simulateMatch(match);

            // 3. Resultado final
            System.out.println("\n[RESULTADO FINAL]");
            System.out.println("(" + match.getTeam1().getRankingPosition() + ")" + match.getTeam1().getName() + " " + match.getTeam1Goals() + " - "
                    + match.getTeam2Goals() + " " + match.getTeam2().getName() + "(" + match.getTeam2().getRankingPosition() + ")");

            // 4. Ordenar y listar todas las incidencias por minuto cronológico
            ArrayList<Incident> incidents = new ArrayList<>(match.getIncidents());
            incidents.sort((i1, i2) -> Integer.compare(i1.getMinute(), i2.getMinute()));

            System.out.println("\n=== INCIDENCIAS DEL PARTIDO (" + incidents.size() + " en total) ===");

            for (Incident incident : incidents) {
                int min = incident.getMinute();

                if (incident instanceof Goal) {
                    Goal g = (Goal) incident;
                    String detail = g.isOwnGoal() ? " (En Contra)" : (g.isPenalty() ? " (Penal)" : "");
                    String gkInfo = (g.getGoalkeeper() != null) ? " [Arquero rival: " + g.getGoalkeeper().getName() + "]" : "";
                    System.out.printf("Min %2d' | ⚽ GOL de %s %s%s%n",
                            min, g.getScorer().getName(), detail, gkInfo);

                } else if (incident instanceof Substitution) {
                    Substitution sub = (Substitution) incident;
                    System.out.printf("Min %2d' | 🔄 CAMBIO: Sale %s (%s)➔ Entra %s (%s) %n",
                            min, sub.getPlayerOut().getName(), sub.getPlayerOut().getPosition(),
                            sub.getPlayerIn().getName(), sub.getPlayerIn().getPosition());

                } else if (incident instanceof Expulsion) {
                    Expulsion exp = (Expulsion) incident;
                    System.out.printf("Min %2d' | 🟥 EXPULSIÓN: %s %n",
                            min, exp.getPlayer().getName());

                } else if (incident instanceof YellowCard) {
                    YellowCard yc = (YellowCard) incident;
                    System.out.printf("Min %2d' | 🟨 AMARILLA: %s %n",
                            min, yc.getPlayer().getName());

                } else if (incident instanceof PenaltyTaken) {
                    PenaltyTaken pt = (PenaltyTaken) incident;
                    String estado = pt.isScored() ? "Convertido" : "Errado/Atajado";
                    System.out.printf("Min %2d' | 🥅 PENAL EJECUTADO por %s: %s%n",
                            min, pt.getPlayer().getName(), estado);
                }
            }
        }
    }

    private static void testUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainWindowView window = new MainWindowView(new Tournament());
            window.setVisible(true);
        });
        /*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            Tournament tournament = null;
            try {
                tournament = fileReader("torneo.json");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StadiumLoader loader = new StadiumLoader();
            initDatabase();
            tournament.addAllStadiums(loader.loadStadiums());

            // 2. Instanciar la vista principal Dashboard
            MainWindowView dashboardView = new MainWindowView();

            // 3. Conectar la vista y el modelo con el controlador principal
            MainWindowController mainController = new MainWindowController(dashboardView, tournament);

            // 4. Hacer visible la aplicación
            dashboardView.setVisible(true);
        });
        */

        /*
        // Establecer el Look &amp; Feel del sistema operativo para que se vea moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Iniciar la interfaz en el hilo de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {

            Tournament tournament = null;
            try {
                tournament = fileReader("torneo.json");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StadiumLoader loader = new StadiumLoader();
            initDatabase();
            tournament.addAllStadiums(loader.loadStadiums());

            // 2. Crear la ventana principal Dashboard
            MainDashboardView dashboardView = new MainDashboardView();

            // 3. Crear la vista y el controlador de la Fase de Grupos
            //GroupStageView groupStageView = new GroupStageView();
            //GroupStageController groupStageController = new GroupStageController(groupStageView, tournament);

            // 4. Cargar la vista de grupos en el panel central del Dashboard
            dashboardView.setContentPanel(groupStageView);

            // 5. Configurar los botones de navegación superiores
            dashboardView.getBtnGroupStage().addActionListener(e ->dashboardView.setContentPanel(groupStageView));

            // 6. Hacer visible la ventana
            dashboardView.setVisible(true);
        });
        */
    }
}
