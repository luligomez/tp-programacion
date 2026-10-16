package view;

import model.reports.scorer.ScorerReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScorerRankingView extends JPanel {

    public ScorerRankingView(List<ScorerReportItem> scorers) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Top Scorers Ranking");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(35, 95, 190));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Position",
                "Player",
                "Team",
                "Goals",
                "Penalty Goals"
        };

        DefaultTableModel tableModel =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        int position = 1;

        for (ScorerReportItem scorer : scorers) {
            tableModel.addRow(new Object[]{
                    position++,
                    scorer.getPlayerName(),
                    scorer.getTeamName(),
                    scorer.getGoals(),
                    scorer.getPenaltyGoals()
            });
        }

        StyledTable table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setColumnWidths(new int[]{80, 200, 200, 80, 120});

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        if (scorers.isEmpty()) {
            JLabel message = new JLabel(
                    "No goals have been scored yet.",
                    SwingConstants.CENTER
            );
            message.setFont(new Font("SansSerif", Font.PLAIN, 16));
            add(message, BorderLayout.SOUTH);
        }
    }
}