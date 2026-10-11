package controller;

import model.tournament.Tournament;
import model.reports.TournamentReportsService;
import view.RefereesView;

public class RefereesController {

    private final RefereesView view;
    private final Tournament tournament;

    public RefereesController(RefereesView refereesView, Tournament tournament) {
        this.view = refereesView;
        this.tournament = tournament;
        initController();
    }

    private void initController() {
        //listeners
        loadReportData();
    }

    public void loadReportData() {
        view.setRefereesData(TournamentReportsService.generateRefereesReportData(tournament));
    }
}

