package controller;

import model.MatchSimulator;
import model.Tournament;
import view.zonestage.ZoneStageView;
import view.zonestage.StandingsPanel;

import javax.swing.*;

public class ZoneStageController {

    private final ZoneStageView view;
    private final Tournament tournament;

    public ZoneStageController(ZoneStageView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        initController();
    }

    private void initController() {
        view.setOnDrawCompletedListener(this::onDrawCompleted);
        view.setOnDrawConfirmedListener(this::onDrawConfirmed);
        view.setOnRedrawListener(this::onRedraw);

        updateView();
    }

    public void updateView() {
        if (!tournament.hasZonesDrawn()) {
            view.showDrawPotsState(tournament);
        } else if (!tournament.isDrawConfirmed()) {
            view.showDrawResultState(tournament);
        } else {
            view.showStandingsState(tournament);
            setupStandingsListeners();

            // 💡 SI YA SE JUGARON LAS 3 FECHAS, MANTENEMOS EL BOTÓN DESHABILITADO
            // aunque el usuario vuelva a hacer clic en "Groups" desde el Sidebar
            if (tournament.getCurrentMatchday() > 3) {
                StandingsPanel standingsPanel = view.getStandingsPanel();
                if (standingsPanel != null) {
                    standingsPanel.disableSimulateButton();
                }
            }
        }
    }

    private void setupStandingsListeners() {
        StandingsPanel standingsPanel = view.getStandingsPanel();
        if (standingsPanel != null) {
            standingsPanel.setOnSimulateMatchdayListener(this::simulateNextMatchday);
        }
    }

    private void simulateNextMatchday() {
        int currentDay = tournament.getCurrentMatchday();

        if (currentDay > 3) {
            return;
        }

        // 1. Simular la fecha actual obtenida del torneo
        MatchSimulator.simulateMatchday(tournament, currentDay);

        // 2. Avanzar la fecha en el Modelo
        tournament.setCurrentMatchday(currentDay + 1);

        // 3. Refrescar la UI
        StandingsPanel standingsPanel = view.getStandingsPanel();
        if (standingsPanel != null) {
            standingsPanel.updateGroups(tournament);
        }

        JOptionPane.showMessageDialog(
                view,
                "Matchday " + currentDay + " successfully simulated!",
                "Matchday Complete",
                JOptionPane.INFORMATION_MESSAGE
        );

        // 4. Si acabamos de completar la fecha 3, deshabilitamos el botón
        if (tournament.getCurrentMatchday() > 3 && standingsPanel != null) {
            standingsPanel.disableSimulateButton();
        }
    }

    private void onDrawCompleted() {
        updateView();
    }

    private void onDrawConfirmed() {
        tournament.confirmDraw();
        updateView();
    }

    private void onRedraw() {
        tournament.resetDraw();
        updateView();
    }
}
