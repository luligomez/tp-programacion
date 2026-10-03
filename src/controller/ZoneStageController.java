package controller;

import model.Tournament;
import model.TournamentState;
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
        view.setOnDrawRequestedListener(this::onDrawRequested);
        view.setOnDrawConfirmedListener(this::onDrawConfirmed);
        view.setOnRedrawListener(this::onRedraw);
        view.setOnSimulateMatchdayListener(this::simulateNextMatchday);
        updateView();
    }

    public void updateView() {
        TournamentState state = tournament.getState();

        if (state == TournamentState.NOT_DRAWN) {
            view.showDrawPotsState(tournament.getPots());
            return;
        }

        if (state == TournamentState.DRAWN_UNCONFIRMED) {
            view.showDrawResultState(tournament.getZones());
            return;
        }

        view.showStandingsState(tournament.getZones());
        view.setMatchdayLabel(tournament.getCurrentMatchday());
        if (tournament.getCurrentMatchday() > 3) {
            view.disableSimulateButton();
        }
    }

    private void onDrawRequested() {
        tournament.zoneDraw();
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

    private void simulateNextMatchday() {
        if (tournament.getCurrentMatchday() > 3) {
            return;
        }
        tournament.simulateCurrentMatchday();
        view.updateStandings(tournament.getZones());
        if (tournament.getState() == TournamentState.KNOCKOUT_STAGE) {
            view.disableSimulateButton();
        } else {
            view.setMatchdayLabel(tournament.getCurrentMatchday());
        }
    }
}