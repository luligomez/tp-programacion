package controller;
import model.match.Match;
import model.match.incident.Incident;
import model.match.incident.Goal;
import model.match.incident.YellowCard;
import model.match.incident.Expulsion;
import model.match.incident.Substitution;
import view.MatchDetailView;

import java.awt.Frame;

public class MatchDetailController {

    private MatchDetailView view;
    private Match match;

    public MatchDetailController(Frame parent, Match match) {
        this.match = match;
        //this.view = new MatchDetailView(parent);

        initViewData();
    }

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

}