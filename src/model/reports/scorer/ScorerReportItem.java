package model.reports.scorer;

public class ScorerReportItem {

    private final String playerName;
    private final String teamName;
    private final int goals;
    private final int penaltyGoals;

    public ScorerReportItem(String playerName, String teamName,
                            int goals, int penaltyGoals) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.goals = goals;
        this.penaltyGoals = penaltyGoals;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getGoals() {
        return goals;
    }

    public int getPenaltyGoals() {
        return penaltyGoals;
    }
}