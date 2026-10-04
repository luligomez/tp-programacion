package model.reports;


import model.person.Position;

public class PlayerReportItem {
    private final String playerName;
    private final String teamName;
    private final Position position;
    private final int age;
    private final int matchesPlayed;
    private final int minutesPlayed;
    private final int goalsScored;
    private final int goalsConceded;        // Específico de Arqueros
    private final double goalsConcededPerMatch; // Específico de Arqueros

    public PlayerReportItem(String playerName, String teamName, Position position, int age,
                            int matchesPlayed, int minutesPlayed, int goalsScored,
                            int goalsConceded, double goalsConcededPerMatch) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.position = position;
        this.age = age;
        this.matchesPlayed = matchesPlayed;
        this.minutesPlayed = minutesPlayed;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
        this.goalsConcededPerMatch = goalsConcededPerMatch;
    }

    public String getPlayerName() { return playerName; }
    public String getTeamName() { return teamName; }
    public Position getPosition() { return position; }
    public int getAge() { return age; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getMinutesPlayed() { return minutesPlayed; }
    public int getGoalsScored() { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public double getGoalsConcededPerMatch() { return goalsConcededPerMatch; }
}
