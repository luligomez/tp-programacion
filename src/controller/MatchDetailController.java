package controller;

import model.match.Match;
import model.reports.MatchReportBuilder;
import view.match.MatchDetailView;

public class MatchDetailController {

    public MatchDetailController(MatchDetailView view, Match match, Runnable onBack) {
        view.setOnBackListener(onBack);
        view.showReport(MatchReportBuilder.build(match));
    }
}

    /*
    private void initViewData() {
        if (match == null) return;

        // 1. Encabezado con resultado
        String scoreText;
        if (match.isPlayed()) {
            scoreText = match.getTeam1().getName() + "  " + match.getTeam1Goals() +
                    " - " + match.getTeam2Goals() + "  " + match.getTeam2().getName();
        } else {
            scoreText = match.getTeam1().getName() + "  vs  " + match.getTeam2().getName();
        }

        String stadiumName = (match.getStadium() != null) ? match.getStadium().getName() : "TBD";
        String refereeName = (match.getReferee() != null) ? match.getReferee().getName() : "TBD";
        String detailsText = "Stadium: " + stadiumName + " | Referee: " + refereeName;

        //view.setMatchHeader(scoreText, detailsText);

        // 2. Cronología de Incidencias
        StringBuilder sb = new StringBuilder();
        if (!match.isPlayed()) {
            sb.append("This match has not been simulated yet.\n");
        } else if (match.getIncidents() == null || match.getIncidents().isEmpty()) {
            sb.append("No incidents occurred in this match.\n");
        } else {
            for (Incident inc : match.getIncidents()) {
                sb.append(formatIncident(inc)).append("\n");
            }
        }

        //view.setIncidentsText(sb.toString());
    }

    private String formatIncident(Incident inc) {
        return inc.toString();
    }
    */