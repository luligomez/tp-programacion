package controller;

import model.tournament.Tournament;
import service.TournamentReportsService;
import view.RefereesView;

public class RefereesController {

    private final RefereesView view;
    private final Tournament tournament;

    public RefereesController(Tournament tournament) {
        this.view = new RefereesView();
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

    public RefereesView getView() {
        return view;
    }
}

