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
import java.util.Comparator;

import static model.MatchSimulator.simulateMatch;

public class Main {
    public static void main(String[] args) throws Exception {
        testUI();
        testTorneo();
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

            windowView.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        tournament.simulateCurrentMatchday(); //1
        tournament.simulateCurrentMatchday(); //2
        tournament.simulateCurrentMatchday(); //3
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

        tournament.simulateKnockoutFirstLeg(KnockoutPhase.QUARTER_FINAL);
        // -------------------------------------------------------------
        // FASE 3: SIMULACIÓN DE IDA Y VUELTA DE CUARTOS
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO PARTIDOS DE IDA <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            FirstLegMatch firstLeg = tie.getFirstLeg();
            printMatchIncidents(firstLeg);
            System.out.println(" Ida: " + firstLeg.getTeam1().getName() + " "
                    + firstLeg.getTeam1Goals() + " - "
                    + firstLeg.getTeam2Goals() + " " + firstLeg.getTeam2().getName());

        }
        tournament.simulateKnockoutSecondLeg(KnockoutPhase.QUARTER_FINAL);

        System.out.println("\n---> SIMULANDO PARTIDOS DE VUELTA Y RESOLUCIÓN <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            SecondLegMatch secondLegMatch = tie.getSecondLeg();
            printMatchIncidents(secondLegMatch);

            // 1. Calcular marcador acumulado (Global)
            int totalGolesTeam1 = tie.getFirstLeg().getTeam1Goals() + secondLegMatch.getTeam2Goals();
            int totalGolesTeam2 = tie.getFirstLeg().getTeam2Goals() + secondLegMatch.getTeam1Goals();

            // 2. Imprimir resultado de la vuelta
            System.out.print(" Vuelta: " + secondLegMatch.getTeam1().getName() + " "
                    + secondLegMatch.getTeam1Goals() + " - "
                    + secondLegMatch.getTeam2Goals() + " " + secondLegMatch.getTeam2().getName());

            // 3. Imprimir marcador global
            System.out.print(" | Global: (" + totalGolesTeam1 + " - " + totalGolesTeam2 + ")");
        }
        if(tournament.hasUnresolvedTies(KnockoutPhase.QUARTER_FINAL)) {
            tournament.resolveTies(KnockoutPhase.QUARTER_FINAL);
        }
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            // 4. Si hubo penales, imprimir el resultado de la tanda
            if (tie.getSecondLeg().getPenalties() != null) {
                PenaltyShootout pen = tie.getSecondLeg().getPenalties();
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

        tournament.simulateKnockoutFirstLeg(KnockoutPhase.SEMI_FINAL);
        // -------------------------------------------------------------
        // FASE 5: SIMULACIÓN DE IDA Y VUELTA DE SEMIS
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO PARTIDOS DE IDA <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if(tie.getPhase().equals(KnockoutPhase.SEMI_FINAL)) {
                FirstLegMatch firstLeg = tie.getFirstLeg();
                printMatchIncidents(firstLeg);
                System.out.println(" Ida: " + firstLeg.getTeam1().getName() + " "
                        + firstLeg.getTeam1Goals() + " - "
                        + firstLeg.getTeam2Goals() + " " + firstLeg.getTeam2().getName());
            }
        }
        tournament.simulateKnockoutSecondLeg(KnockoutPhase.SEMI_FINAL);

        System.out.println("\n---> SIMULANDO PARTIDOS DE VUELTA Y RESOLUCIÓN <---");
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if (tie.getPhase().equals(KnockoutPhase.SEMI_FINAL)) {
                SecondLegMatch secondLegMatch = tie.getSecondLeg();
                printMatchIncidents(secondLegMatch);

                // 1. Calcular marcador acumulado (Global)
                int totalGolesTeam1 = tie.getFirstLeg().getTeam1Goals() + secondLegMatch.getTeam2Goals();
                int totalGolesTeam2 = tie.getFirstLeg().getTeam2Goals() + secondLegMatch.getTeam1Goals();

                // 2. Imprimir resultado de la vuelta
                System.out.print(" Vuelta: " + secondLegMatch.getTeam1().getName() + " "
                        + secondLegMatch.getTeam1Goals() + " - "
                        + secondLegMatch.getTeam2Goals() + " " + secondLegMatch.getTeam2().getName());

                // 3. Imprimir marcador global
                System.out.print(" | Global: (" + totalGolesTeam1 + " - " + totalGolesTeam2 + ")");
            }
        }
        if(tournament.hasUnresolvedTies(KnockoutPhase.SEMI_FINAL)) {
            tournament.resolveTies(KnockoutPhase.SEMI_FINAL);
        }
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            // 4. Si hubo penales, imprimir el resultado de la tanda
            if (tie.getSecondLeg().getPenalties() != null) {
                PenaltyShootout pen = tie.getSecondLeg().getPenalties();
                System.out.print(" | Penales: " + pen.getTeam1Goals() + " - " + pen.getTeam2Goals());
            }
            // 5. Imprimir el ganador de la llave
            System.out.println(" ==> CLASIFICA: " + tie.getWinner().getName().toUpperCase());
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


        tournament.simulateFinal();
        // -------------------------------------------------------------
        // FASE 5: SIMULACIÓN DE FINAL
        // -------------------------------------------------------------
        System.out.println("\n---> SIMULANDO FINAL <---");
        printMatchIncidents(m);
        System.out.println(" Final: " + m.getTeam1().getName() + " "
                        + m.getTeam1Goals() + " - "
                        + m.getTeam2Goals() + " " + m.getTeam2().getName());
        if(tournament.hasUnresolvedFinal()) {
            tournament.resolveFinal();
            PenaltyShootout pen = m.getPenalties();
            System.out.print(" | Penales: " + pen.getTeam1Goals() + " - " + pen.getTeam2Goals());
        }

        // 5. Imprimir el ganador de la llave
        System.out.println(" ==> GANADOR: " + m.getWinner().getName().toUpperCase());

    }

    private static void printMatchIncidents(Match match) {
        ArrayList<Incident> incidents = new ArrayList<>(match.getIncidents());
        incidents.sort(Comparator.comparingInt(Incident::getMinute));

        if (incidents.isEmpty()) {
            System.out.println("   (No incidents)");
            return;
        }

        System.out.println("   --- Incidents (" + incidents.size() + ") ---");
        for (Incident incident : incidents) {
            System.out.println("   " + incident);
        }
    }


}