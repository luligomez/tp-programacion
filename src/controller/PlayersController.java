package controller;

import model.Team;
import model.Tournament;
import model.reports.PlayerReportData;
import view.PlayersView;

import java.util.List;

public class PlayersController {

    private final PlayersView view;
    private final Tournament tournament;

    public PlayersController(PlayersView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        // 1. El Controlador escucha los eventos de los filtros en la Vista
        this.view.addFilterListener(e -> this.view.applyFilters());

        // 2. Cargar datos del modelo
        loadReportData();
    }

    public void loadReportData() {
        PlayerReportData data = tournament.getPlayersReportData();

        List<String> teamNames = tournament.getTeams().stream()
                .map(Team::getName)
                .sorted()
                .toList();

        view.setPlayersData(data.getItems(), teamNames);
    }
}