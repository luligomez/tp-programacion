package controller.rankings;

import model.reports.playerMinutes.MostMinutesReportItem;
import model.tournament.Tournament;
import service.TournamentReportsService;
import view.rankings.MostMinutesRankingView;

import java.util.List;

public class MostMinutesRankingController {

    private final MostMinutesRankingView view;
    private final Tournament tournament;

    public MostMinutesRankingController(Tournament tournament) {
        this.tournament = tournament;
        this.view = new MostMinutesRankingView();
        loadRanking();
    }

    private void loadRanking() {
        List<MostMinutesReportItem> ranking = TournamentReportsService.getMostMinutesRanking(tournament);
        view.showRanking(ranking);
    }

    public MostMinutesRankingView getView() {
        return view;
    }
}