package view.rankings;

import model.reports.playerMinutes.MostMinutesReportItem;
import view.components.StyledTable;
import view.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MostMinutesRankingView extends JPanel {

    private final DefaultTableModel tableModel;
    private final JLabel emptyMessage;

    public MostMinutesRankingView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Most Minutes Played Ranking");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Position",
                "Player",
                "Team",
                "Matches Played",
                "Minutes Played"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        StyledTable table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setColumnWidths(new int[]{80, 200, 200, 130, 130});
        table.setColumnSuffix(4,"'");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        emptyMessage = new JLabel("No player statistics available yet.", SwingConstants.CENTER);
        emptyMessage.setFont(Theme.BIG_BOLD16);
    }

    public void showRanking(List<MostMinutesReportItem> ranking) {
        tableModel.setRowCount(0);

        int position = 1;
        for (MostMinutesReportItem item : ranking) {
            tableModel.addRow(new Object[]{
                    position++,
                    item.getPlayerName(),
                    item.getTeamName(),
                    item.getMatchesPlayed(),
                    item.getMinutesPlayed()
            });
        }

        if (ranking.isEmpty()) {
            add(emptyMessage, BorderLayout.SOUTH);
        } else {
            remove(emptyMessage);
        }
        revalidate();
        repaint();
    }
}