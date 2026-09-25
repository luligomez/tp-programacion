package controller;

import model.Tournament;
import view.zonestage.ZoneStageView;

public class ZoneStageController {

    private final ZoneStageView view;
    private final Tournament tournament;

    public ZoneStageController(ZoneStageView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        initController();
    }

    private void initController() {
        // Conectamos los callbacks/listeners con la vista
        view.setOnDrawCompletedListener(this::onDrawCompleted);
        view.setOnDrawConfirmedListener(this::onDrawConfirmed);
        view.setOnRedrawListener(this::onRedraw);

        // Renderizamos la pantalla según el estado inicial del torneo
        updateView();
    }

    public void updateView() {
        if (!tournament.hasZonesDrawn()) {
            view.showDrawPotsState(tournament);
        } else if (!tournament.isDrawConfirmed()) {
            view.showDrawResultState(tournament);
        } else {
            view.showStandingsState(tournament);
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