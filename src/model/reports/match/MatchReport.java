package model.reports.match;

import model.match.knockout.PenaltyShootout;
import model.person.Position;

import java.util.List;

public record MatchReport(
        String team1Name, String team2Name,
        boolean played, int team1Goals, int team2Goals,
        String date, String stadiumName, String refereeName,
        List<TimelineEvent> timeline,
        TeamLineupReport team1Lineup,   // null if the formations are not confirmed
        TeamLineupReport team2Lineup,
        PenaltyShootoutReport shootout,
        StandingsReport standings) {

    public enum Side { TEAM1, TEAM2 }

    public enum EventType {
        GOAL, PENALTY_GOAL, OWN_GOAL, YELLOW_CARD, RED_CARD, SECOND_YELLOW_RED, SUBSTITUTION
    }

    public enum MarkType { GOAL, OWN_GOAL, YELLOW, RED, SUB_OUT, SUB_IN }

    public record TimelineEvent(int minute, Side side, EventType type,
                                String playerName, String playerPosition, String otherPlayerName, String otherPlayerPosition) {
        public TimelineEvent(int minute, Side side, EventType type,
                             String playerName){
            this(minute,side,type,playerName,null,null,null);
        }
    }

    public record PlayerMark(MarkType type, int minute) {}

    public record LineupPlayer(String name, Position position, boolean cameOn, List<PlayerMark> marks) {}

    public record TeamLineupReport(String teamName, List<LineupPlayer> starters, List<LineupPlayer> substitutes) {}

    public boolean lineupsConfirmed() {
        return team1Lineup != null && team2Lineup != null;
    }

    // one round: team 2's kick may be missing if the shootout was decided earlier
    public record PenaltyRound(int number, PenaltyShootout.PenaltyKick team1Kick, PenaltyShootout.PenaltyKick team2Kick) {}

    public record PenaltyShootoutReport(List<PenaltyRound> rounds, int team1Score, int team2Score, String winnerName) {}

    public record StandingRow(int position, String teamName, int points, int matchesPlayed, int wins,
                              int draws, int losses, int goalsFor, int goalsAgainst, int goalDifference) {}

    public record StandingsReport(List<StandingRow> before, List<StandingRow> after) {}

    public boolean hasStandings() {
        return standings != null;
    }

    public boolean hasShootout() {
        return shootout != null;
    }
}