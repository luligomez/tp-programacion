package controller;

import model.match.Match;
import service.MatchReportBuilder;
import view.match.MatchDetailView;

public class MatchDetailController {

    private final MatchDetailView view;

    public MatchDetailController(Match match, Runnable onBack) {
        this.view = new MatchDetailView();
        view.setOnBackListener(onBack);
        view.showReport(MatchReportBuilder.build(match));
    }

    public MatchDetailView getView() {
        return view;
    }
}
