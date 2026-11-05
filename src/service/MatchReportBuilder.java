package service;

import model.team.Team;
import model.match.Formation;
import model.match.Match;
import model.person.player.Player;
import model.reports.match.MatchReport;
import model.reports.match.MatchReport.*;
import model.match.zone.GroupStageMatch;
import model.tournament.zone.TeamStanding;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MatchReportBuilder {

    public static MatchReport build(Match match) {
        Formation formation1 = match.getTeam1Formation();
        Formation formation2 = match.getTeam2Formation();
        boolean confirmed = formation1 != null && formation2 != null;

        return new MatchReport(
                match.getTeam1().getName(), match.getTeam2().getName(),
                match.isPlayed(), match.getTeam1Goals(), match.getTeam2Goals(),
                match.getDateTime() != null ? match.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM · HH:mm")) : "TBD",
                match.getStadium() != null ? match.getStadium().getName() : "TBD",
                match.getReferee() != null ? match.getReferee().getName() : "TBD",
                match.isPlayed() ? buildTimeline(match) : List.of(),
                confirmed ? buildLineup(match, match.getTeam1(), formation1) : null,
                confirmed ? buildLineup(match, match.getTeam2(), formation2) : null,
                match.toShootoutReport().orElse(null),
                buildStandings(match)
        );
    }

    private static StandingsReport buildStandings(Match match) {
        if (match instanceof GroupStageMatch groupMatch && groupMatch.hasStandings()) {
            return new StandingsReport(
                    toRows(groupMatch.getStandingsBefore()),
                    toRows(groupMatch.getStandingsAfter()));
        }
        return null;
    }

    private static List<StandingRow> toRows(List<TeamStanding> standings) {
        List<StandingRow> rows = new ArrayList<>();
        for (int i = 0; i < standings.size(); i++) {
            TeamStanding s = standings.get(i);
            rows.add(new StandingRow(i + 1, s.getTeam().getName(), s.getPoints(), s.getMatchesPlayed(),
                    s.getMatchesWon(), s.getMatchesDrawn(), s.getMatchesLost(),
                    s.getGoalsFor(), s.getGoalsAgainst(), s.getGoalDifference()));
        }
        return rows;
    }

    private static List<TimelineEvent> buildTimeline(Match match) {
        return match.getIncidents().stream()
                .map(incident -> incident.toTimelineEvent(match))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(TimelineEvent::minute)) // stable: same minute keeps the order
                .toList();
    }

    private static TeamLineupReport buildLineup(Match match, Team team, Formation formation) {
        return new TeamLineupReport(
                team.getName(),
                toLineupPlayers(match, formation.getStarters()),
                toLineupPlayers(match, formation.getSubstitutes()));
    }

    private static List<LineupPlayer> toLineupPlayers(Match match, List<Player> players) {
        return players.stream()
                .sorted(Comparator.comparing(Player::getPosition)) // declaration order of the Position enum
                .map(player -> toLineupPlayer(match, player))
                .toList();
    }

    private static LineupPlayer toLineupPlayer(Match match, Player player) {
        List<PlayerMark> marks = match.getIncidents().stream()
                .map(incident -> incident.markFor(player))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(PlayerMark::minute))
                .toList();

        boolean cameOn = marks.stream().anyMatch(mark -> mark.type() == MarkType.SUB_IN);
        return new LineupPlayer(player.getName(), player.getPosition(), cameOn, marks);
    }
}