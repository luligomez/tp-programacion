package controller;

import model.team.Team;
import model.tournament.Tournament;
import model.reports.player.PlayerReportData;
import service.TournamentReportsService;
import view.PlayersView;

import java.util.List;

public class PlayersController {

    private final PlayersView view;
    private final Tournament tournament;

    public PlayersController(Tournament tournament) {
        this.view = new PlayersView();
        this.tournament = tournament;

        // 1. The controller listens to the filter events in the view
        this.view.addFilterListener(e -> this.view.applyFilters());

        // 2. Load data from the model
        loadReportData();
    }

    public void loadReportData() {
        PlayerReportData data = TournamentReportsService.generatePlayersReport(tournament);

        List<String> teamNames = tournament.getTeams().stream()
                .map(Team::getName)
                .sorted()
                .toList();

        view.setPlayersData(data.getItems(), teamNames);
    }

    public PlayersView getView() {
        return view;
    }
}