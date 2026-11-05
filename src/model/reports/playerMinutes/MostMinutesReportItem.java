package model.reports.playerMinutes;

public class MostMinutesReportItem {
    private final String playerName;
    private final String teamName;
    private final int matchesPlayed;
    private final int minutesPlayed;

    public MostMinutesReportItem(String playerName, String teamName, int matchesPlayed, int minutesPlayed) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.matchesPlayed = matchesPlayed;
        this.minutesPlayed = minutesPlayed;
    }

    public String getPlayerName() { return playerName; }
    public String getTeamName() { return teamName; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getMinutesPlayed() { return minutesPlayed; }
}