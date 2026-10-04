package model.reports;

public class TeamReportItem {
    private final String teamName;
    private final double averagePlayerAge;
    private final int coachAge;
    private final String coachNationality;
    private final int goalsFor;
    private final int goalsAgainst;
    private final double effectiveness; // Porcentaje de 0.0 a 100.0

    public TeamReportItem(String teamName, double averagePlayerAge, int coachAge,
                          String coachNationality, int goalsFor, int goalsAgainst, double effectiveness) {
        this.teamName = teamName;
        this.averagePlayerAge = averagePlayerAge;
        this.coachAge = coachAge;
        this.coachNationality = coachNationality;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.effectiveness = effectiveness;
    }

    public String getTeamName() { return teamName; }
    public double getAveragePlayerAge() { return averagePlayerAge; }
    public int getCoachAge() { return coachAge; }
    public String getCoachNationality() { return coachNationality; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public double getEffectiveness() { return effectiveness; }
}
