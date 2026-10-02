package controller;

import model.Tournament;
import model.reports.RefereeReportData;
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
        RefereeReportData data = tournament.getRefereesReportData();
        view.setRefereesData(data.getItems(), data.getAverageYears());
    }
}

