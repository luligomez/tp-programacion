package test;

import controller.MainWindowController;
import model.*;

import model.match.GroupStageMatch;
import model.match.knockout.*;

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
        testTorneo();
        //testGroupMatch();
        //testSingleMatch();
/*
        Tournament tournament = model.FileReader.fileReader("torneo.json");

        tournament.zoneDraw();

        tournament.generateGroupStageMatches();

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
            // 1\. Cargar el torneo inicial
            Tournament tournament = null;
            try {
                tournament = FileReader.fileReader("torneo.json");
                StadiumLoader.initDatabase();
                tournament.setStadiums(StadiumLoader.loadStadiums());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 2\. Crear la vista principal (ya no necesita recibir el Tournament directamente)
            MainWindowView windowView = new MainWindowView();
            // 3\. Crear el controlador que conecta la vista con el modelo
            MainWindowController mainController = new MainWindowController(windowView, tournament);
            // 4\. Mostrar la aplicación
            windowView.setVisible(true);
        });
    }
    
    private static void testTorneo(){
        System.out.println("==================================================");
        System.out.println("   INICIANDO PRUEBA DEL TORNEO - HASTA CUARTOS   ");
        System.out.println("==================================================\n");

        Tournament tournament = null;
        try {
            tournament = FileReader.fileReader("torneo.json");
            StadiumLoader.initDatabase();
            tournament.setStadiums(StadiumLoader.loadStadiums());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // -------------------------------------------------------------
        // FASE 1: SIMULACIÓN DE LA FASE DE GRUPOS (3 FECHAS)
        // -------------------------------------------------------------

        tournament.zoneDraw();
        tournament.confirmDraw();
        MatchSimulator.simulateMatchday(tournament,1);
        tournament.assignFormationsForMatchday(2);
        MatchSimulator.simulateMatchday(tournament,2);
        tournament.assignFormationsForMatchday(3);
        MatchSimulator.simulateMatchday(tournament,3);
        // -------------------------------------------------------------
        // FASE 2: GENERACIÓN DE CUARTOS DE FINAL
        // -------------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println("   GENERANDO CRUCES DE CUARTOS DE FINAL   ");
        System.out.println("==================================================");
        tournament.generateQuarterFinals();
        System.out.println("Llaves de Cuartos armadas:");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            System.out.println(" * " + tie.getTeam1().getName() + " vs " + tie.getTeam2().getName());
        }

        MatchSimulator.simulateFirstLeg(tournament, KnockoutPhase.QUARTER_FINAL);
        // -------------------------------------------------------------
        // FASE 3: SIMULACIÓN DE IDA Y VUELTA DE CUARTOS
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO PARTIDOS DE IDA <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            FirstLegMatch firstLeg = tie.getFirstLeg();
            System.out.println(" Ida: " + firstLeg.getTeam1().getName() + " "
                    + firstLeg.getTeam1Goals() + " - "
                    + firstLeg.getTeam2Goals() + " " + firstLeg.getTeam2().getName());
        }
        tournament.assignFormationsForSecondLeg(KnockoutPhase.QUARTER_FINAL);
        MatchSimulator.simulateSecondLeg(tournament, KnockoutPhase.QUARTER_FINAL);

        System.out.println("\n---> SIMULANDO PARTIDOS DE VUELTA Y RESOLUCIÓN <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            SecondLegMatch secondLegMatch = tie.getSecondLeg();

            // 1. Calcular marcador acumulado (Global)
            int totalGolesTeam1 = tie.getFirstLeg().getTeam1Goals() + secondLegMatch.getTeam2Goals();
            int totalGolesTeam2 = tie.getFirstLeg().getTeam2Goals() + secondLegMatch.getTeam1Goals();

            // 2. Imprimir resultado de la vuelta
            System.out.print(" Vuelta: " + secondLegMatch.getTeam1().getName() + " "
                    + secondLegMatch.getTeam1Goals() + " - "
                    + secondLegMatch.getTeam2Goals() + " " + secondLegMatch.getTeam2().getName());

            // 3. Imprimir marcador global
            System.out.print(" | Global: (" + totalGolesTeam1 + " - " + totalGolesTeam2 + ")");

            // 4. Si hubo penales, imprimir el resultado de la tanda
            if (secondLegMatch.getPenalties() != null) {
                PenaltyShootout pen = secondLegMatch.getPenalties();
                System.out.print(" | Penales: " + pen.getTeam1Goals() + " - " + pen.getTeam2Goals());
            }

            // 5. Imprimir el ganador de la llave
            System.out.println(" ==> CLASIFICA: " + tie.getWinner().getName().toUpperCase());
        }
        // -------------------------------------------------------------
        // FASE 4: GENERACIÓN DE SEMIS
        // -------------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println("   GENERANDO CRUCES DE SEMIFINALES   ");
        System.out.println("==================================================");
        tournament.generateSemiFinals();
        System.out.println("Llaves de Semis armadas:");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if(tie.getPhase().equals(KnockoutPhase.SEMI_FINAL))
                System.out.println(" * " + tie.getTeam1().getName() + " vs " + tie.getTeam2().getName());
        }

        MatchSimulator.simulateFirstLeg(tournament, KnockoutPhase.SEMI_FINAL);
        // -------------------------------------------------------------
        // FASE 5: SIMULACIÓN DE IDA Y VUELTA DE SEMIS
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO PARTIDOS DE IDA <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if(tie.getPhase().equals(KnockoutPhase.SEMI_FINAL)) {
                FirstLegMatch firstLeg = tie.getFirstLeg();
                System.out.println(" Ida: " + firstLeg.getTeam1().getName() + " "
                        + firstLeg.getTeam1Goals() + " - "
                        + firstLeg.getTeam2Goals() + " " + firstLeg.getTeam2().getName());
            }
        }
        tournament.assignFormationsForSecondLeg(KnockoutPhase.SEMI_FINAL);
        MatchSimulator.simulateSecondLeg(tournament, KnockoutPhase.SEMI_FINAL);

        System.out.println("\n---> SIMULANDO PARTIDOS DE VUELTA Y RESOLUCIÓN <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if(tie.getPhase().equals(KnockoutPhase.SEMI_FINAL)) {
                SecondLegMatch secondLegMatch = tie.getSecondLeg();

                // 1. Calcular marcador acumulado (Global)
                int totalGolesTeam1 = tie.getFirstLeg().getTeam1Goals() + secondLegMatch.getTeam2Goals();
                int totalGolesTeam2 = tie.getFirstLeg().getTeam2Goals() + secondLegMatch.getTeam1Goals();

                // 2. Imprimir resultado de la vuelta
                System.out.print(" Vuelta: " + secondLegMatch.getTeam1().getName() + " "
                        + secondLegMatch.getTeam1Goals() + " - "
                        + secondLegMatch.getTeam2Goals() + " " + secondLegMatch.getTeam2().getName());

                // 3. Imprimir marcador global
                System.out.print(" | Global: (" + totalGolesTeam1 + " - " + totalGolesTeam2 + ")");

                // 4. Si hubo penales, imprimir el resultado de la tanda
                if (secondLegMatch.getPenalties() != null) {
                    PenaltyShootout pen = secondLegMatch.getPenalties();
                    System.out.print(" | Penales: " + pen.getTeam1Goals() + " - " + pen.getTeam2Goals());
                }

                // 5. Imprimir el ganador de la llave
                System.out.println(" ==> CLASIFICA: " + tie.getWinner().getName().toUpperCase());
            }
        }
        // -------------------------------------------------------------
        // FASE 6: GENERACIÓN DE Final
        // -------------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println("   GENERANDO FINAL   ");
        System.out.println("==================================================");
        tournament.generateFinal();
        System.out.println("Final armada:");
        KnockoutMatch m = tournament.getFinalMatch();
        System.out.println(" * " + m.getTeam1().getName() + " vs " + m.getTeam2().getName());


        MatchSimulator.simulateFinal(tournament);

        // -------------------------------------------------------------
        // FASE 5: SIMULACIÓN DE FINAL
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO FINAL <---");
        System.out.println(" Final: " + m.getTeam1().getName() + " "
                        + m.getTeam1Goals() + " - "
                        + m.getTeam2Goals() + " " + m.getTeam2().getName());

        // 4. Si hubo penales, imprimir el resultado de la tanda
        if (m.getPenalties() != null) {
            PenaltyShootout pen = m.getPenalties();
            System.out.print(" | Penales: " + pen.getTeam1Goals() + " - " + pen.getTeam2Goals());
        }

        // 5. Imprimir el ganador de la llave
        System.out.println(" ==> GANADOR: " + m.getWinner().getName().toUpperCase());



    }

}