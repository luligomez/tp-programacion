package controller;

import model.Tournament;
import model.reports.TeamReportData;
import view.TeamsView;

public class TeamsController {

    private final TeamsView view;
    private final Tournament tournament;

    public TeamsController(TeamsView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        loadReportData();
    }

    public void loadReportData() {
        TeamReportData data = tournament.getTeamsReportData();
        view.setTeamsData(data.getItems());
    }
}
