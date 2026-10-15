package controller;

import model.tournament.Tournament;
import service.TournamentReportsService;
import model.reports.team.TeamReportData;
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
        TeamReportData data = TournamentReportsService.generateTeamsReportData(tournament);
        view.setTeamsData(data.getItems());
    }
}
