package view.rankings;

import model.reports.playerMinutes.MostMinutesReportItem;
import view.components.StyledTable;

public class MostMinutesRankingView extends RankingView<MostMinutesReportItem> {

    public MostMinutesRankingView() {
        super("Most Minutes Played Ranking", "No player statistics available yet.");
    }

    @Override
    protected String[] columns() {
        return new String[]{"Position", "Player", "Team", "Matches Played", "Minutes Played"};
    }

    @Override
    protected Object[] rowOf(MostMinutesReportItem item) {
        return new Object[]{
                item.getPlayerName(),
                item.getTeamName(),
                item.getMatchesPlayed(),
                item.getMinutesPlayed()
        };
    }

    @Override
    protected void configureTable(StyledTable table) {
        table.setColumnWidths(new int[]{80, 200, 200, 130, 130});
        table.setColumnSuffix(4, "'");
    }
}