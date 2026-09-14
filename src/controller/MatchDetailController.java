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
        if (inc instanceof Goal g) {
            String author = (g.getScorer() != null) ? g.getScorer().getName() : "Unknown";
            String gk = (g.getGoalkeeper() != null) ? " (GK: " + g.getGoalkeeper().getName() + ")" : "";
            return "Min " + g.getMinute() + "' | ⚽ GOAL! " + author + gk;
        } else if (inc instanceof YellowCard y) {
            String pName = (y.getPlayer() != null) ? y.getPlayer().getName() : "Player";
            return "Min " + y.getMinute() + "' | 🟨 Yellow Card: " + pName;
        } else if (inc instanceof Expulsion e) {
            String pName = (e.getPlayer() != null) ? e.getPlayer().getName() : "Player";
            String type = e.isDoubleYellow() ? " (2nd Yellow)" : " (Direct Red)";
            return "Min " + e.getMinute() + "' | 🟥 Red Card: " + pName + type;
        } else if (inc instanceof Substitution s) {
            String outName = (s.getPlayerOut() != null) ? s.getPlayerOut().getName() : "Out";
            String inName = (s.getPlayerIn() != null) ? s.getPlayerIn().getName() : "In";
            return "Min " + s.getMinute() + "' | 🔄 Sub: Out " + outName + " ➔ In " + inName;
        }
        return "Min " + inc.getMinute() + "' | Incident registered";
    }

}