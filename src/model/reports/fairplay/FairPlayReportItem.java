package model.reports.fairplay;

public class FairPlayReportItem {

    private final String teamName;
    private final int yellowCards;
    private final int doubleYellowExpulsions;
    private final int directRedCards;
    private final int penaltyPoints;

    public FairPlayReportItem(String teamName,
                              int yellowCards,
                              int doubleYellowExpulsions,
                              int directRedCards) {

        this.teamName = teamName;
        this.yellowCards = yellowCards;
        this.doubleYellowExpulsions = doubleYellowExpulsions;
        this.directRedCards = directRedCards;

        this.penaltyPoints = yellowCards
                + (doubleYellowExpulsions * 3)
                + (directRedCards * 4);
    }

    public String getTeamName() {
        return teamName;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getDoubleYellowExpulsions() {
        return doubleYellowExpulsions;
    }

    public int getDirectRedCards() {
        return directRedCards;
    }

    public int getPenaltyPoints() {
        return penaltyPoints;
    }
}