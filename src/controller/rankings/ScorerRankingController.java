package controller.rankings;

import model.tournament.Tournament;
import service.TournamentReportsService;
import view.rankings.ScorerRankingView;

public class ScorerRankingController {
    private final ScorerRankingView view;
    private final Tournament tournament;

    public ScorerRankingController(Tournament tournament) {
        this.view = new ScorerRankingView();
        this.tournament = tournament;
        showScorers();
    }

    private void showScorers(){
        view.showRanking(TournamentReportsService.generateScorersRanking(tournament));
    }

    public ScorerRankingView getView() {
        return view;
    }
}
