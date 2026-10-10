package model.reports;

import model.person.Position;

import java.util.List;

public record MatchReport(
        String team1Name, String team2Name,
        boolean played, int team1Goals, int team2Goals,
        String date, String stadiumName, String refereeName,
        List<TimelineEvent> timeline,
        TeamLineupReport team1Lineup,   // null si las formaciones no están confirmadas
        TeamLineupReport team2Lineup) {

    public enum Side { TEAM1, TEAM2 }

    public enum EventType {
        GOAL, PENALTY_GOAL, OWN_GOAL, YELLOW_CARD, RED_CARD, SECOND_YELLOW_RED, SUBSTITUTION
    }

    public enum MarkType { GOAL, OWN_GOAL, YELLOW, RED, SUB_OUT, SUB_IN }

    // otherPlayerName solo se usa en SUBSTITUTION (el jugador que entra)
    public record TimelineEvent(int minute, Side side, EventType type,
                                String playerName, String otherPlayerName) {}

    public record PlayerMark(MarkType type, int minute) {}

    public record LineupPlayer(String name, Position position, boolean cameOn, List<PlayerMark> marks) {}

    public record TeamLineupReport(String teamName, List<LineupPlayer> starters, List<LineupPlayer> substitutes) {}

    public boolean lineupsConfirmed() {
        return team1Lineup != null && team2Lineup != null;
    }
}