package controller;

import model.tournament.Tournament;
import service.TournamentReportsService;
import model.reports.team.TeamReportData;
import view.TeamsView;

public class TeamsController {

    private final TeamsView view;
    private final Tournament tournament;

    public TeamsController(Tournament tournament) {
        this.view = new TeamsView();
        this.tournament = tournament;

        loadReportData();
    }

    public TeamsView getView() {
        return view;
    }

    public void loadReportData() {
        TeamReportData data = TournamentReportsService.generateTeamsReportData(tournament);
        view.setTeamsData(data.getItems());
    }
}
