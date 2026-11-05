package view.rankings;

import model.reports.scorer.ScorerReportItem;
import view.components.StyledTable;

public class ScorerRankingView extends RankingView<ScorerReportItem> {

    public ScorerRankingView() {
        super("Top Scorers Ranking", "No goals have been scored yet.");
    }

    @Override
    protected String[] columns() {
        return new String[]{"Position", "Player", "Team", "Goals", "Penalty Goals"};
    }

    @Override
    protected Object[] rowOf(ScorerReportItem scorer) {
        return new Object[]{
                scorer.getPlayerName(),
                scorer.getTeamName(),
                scorer.getGoals(),
                scorer.getPenaltyGoals()
        };
    }

    @Override
    protected void configureTable(StyledTable table) {
        table.setColumnWidths(new int[]{80, 200, 200, 80, 120});
    }
}