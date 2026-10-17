package controller;

import model.tournament.Tournament;
import service.TournamentReportsService;
import view.rankings.FairPlayRankingView;

public class FairPlayRankingController {

    private final FairPlayRankingView view;
    private final Tournament tournament;

    public FairPlayRankingController(Tournament tournament) {
        this.view = new FairPlayRankingView();
        this.tournament = tournament;
        loadReport();
    }

    private void loadReport(){
        view.showRanking(TournamentReportsService.generateFairPlayRanking(tournament));
    }

    public FairPlayRankingView getView() {
        return view;
    }
}
