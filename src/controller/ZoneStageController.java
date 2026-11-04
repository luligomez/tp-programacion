package controller;

import model.tournament.Tournament;
import model.tournament.TournamentState;
import model.match.Match;
import service.DrawService;
import view.zonestage.ZoneStageView;
import model.tournament.zone.TeamStanding;
import model.tournament.zone.Zone;
import java.util.ArrayList;
import java.util.List;
import model.tournament.zone.ZoneStage;

import java.util.function.Consumer;

public class ZoneStageController {
    private final ZoneStageView view;
    private final Tournament tournament;
    private final Consumer<Match> onOpenMatch;
    private final Runnable onProgress;


    public ZoneStageController(Tournament tournament, Consumer<Match> onOpenMatch, Runnable onProgress) {
        this.view = new ZoneStageView();
        this.tournament = tournament;
        this.onOpenMatch = onOpenMatch;
        this.onProgress = onProgress;
        initController();
    }

    private void initController() {
        view.setOnDrawRequestedListener(this::onDrawRequested);
        view.setOnDrawConfirmedListener(this::onDrawConfirmed);
        view.setOnRedrawListener(this::onRedraw);
        view.setOnSimulateMatchdayListener(this::simulateNextMatchday);
        view.setOnMatchSelectedListener(onOpenMatch); // nuevo
        updateView();
    }
    public ZoneStageView getView() {
        return view;
    }

    public void updateView() {
        TournamentState state = tournament.getState();

        if (state == TournamentState.NOT_DRAWN) {
            view.showDrawPotsState(DrawService.getPots(tournament.getTeams()));
            return;
        }

        if (state == TournamentState.DRAWN_UNCONFIRMED) {
            view.showDrawResultState(tournament.getZones());
            return;
        }

        view.showStandingsState(tournament.getZones());
        view.setMatchdayLabel(tournament.getCurrentMatchday());
        if (tournament.getCurrentMatchday() > ZoneStage.MATCHDAYS) {
            view.disableSimulateButton();
        }
    }

    private void onDrawRequested() {
        tournament.zoneDraw();
        updateView();
    }

    private void onDrawConfirmed() {

        if (tournament.getStadiums() == null ||
                tournament.getStadiums().size() < 13) {

            javax.swing.JOptionPane.showMessageDialog(
                    view,
                    "Se necesitan al menos 13 estadios para iniciar el torneo.",
                    "Estadios insuficientes",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        tournament.confirmDraw();
        onProgress.run();
        updateView();
    }

    private void onRedraw() {
        tournament.resetDraw();
        updateView();
    }

    private void simulateNextMatchday() {
        if (tournament.getCurrentMatchday() > ZoneStage.MATCHDAYS) {
            return;
        }
        List<List<TeamStanding>> standingsBefore = new ArrayList<>();

        for (Zone zone : tournament.getZones()) {
            standingsBefore.add(zone.getStandingsSnapshot());
        }
        tournament.simulateCurrentMatchday();
        onProgress.run();
        view.showStandingsComparison(
                tournament.getZones(),
                standingsBefore
        );
        view.getStandingsPanel().showMatchComparisons(
                tournament.getZoneStage().getLastMatchdayComparisons()
        );
        if (tournament.getState() == TournamentState.KNOCKOUT_STAGE) {
            view.disableSimulateButton();
        } else {
            view.setMatchdayLabel(tournament.getCurrentMatchday());
        }
    }
}