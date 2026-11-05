package view.rankings;

import model.reports.fairplay.FairPlayReportItem;

public class FairPlayRankingView extends RankingView<FairPlayReportItem> {

    public FairPlayRankingView() {
        super("Fair Play Ranking", "No Fair Play statistics available.");
    }

    @Override
    protected String[] columns() {
        return new String[]{"Position", "Team", "Single Yellow Cards", "Double Yellow Expulsions",
                "Direct Red Cards", "Penalty Points"};
    }

    @Override
    protected Object[] rowOf(FairPlayReportItem item) {
        return new Object[]{
                item.getTeamName(),
                item.getYellowCards(),
                item.getDoubleYellowExpulsions(),
                item.getDirectRedCards(),
                item.getPenaltyPoints()
        };
    }
}