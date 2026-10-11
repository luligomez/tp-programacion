package model.reports;

import model.Team;
import model.match.Formation;
import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.*;

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
                match.toShootoutReport().orElse(null)
        );
    }

    private static List<TimelineEvent> buildTimeline(Match match) {
        return match.getIncidents().stream()
                .map(incident -> incident.toTimelineEvent(match))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(TimelineEvent::minute)) // estable: mismo minuto conserva el orden
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
                .sorted(Comparator.comparing(Player::getPosition)) // orden de declaración del enum Position
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