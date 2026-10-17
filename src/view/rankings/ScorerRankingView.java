package view.rankings;

import model.reports.scorer.ScorerReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScorerRankingView extends JPanel {
    private final DefaultTableModel tableModel;
    private final JLabel emptyMessage;

    public ScorerRankingView() {
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

        tableModel =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };


        StyledTable table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setColumnWidths(new int[]{80, 200, 200, 80, 120});

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        emptyMessage = new JLabel(
                    "No goals have been scored yet.",
                    SwingConstants.CENTER
        );
        emptyMessage.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }

    public void showScorers(List<ScorerReportItem> scorers) {
        tableModel.setRowCount(0);

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

        if (scorers.isEmpty()) {
            add(emptyMessage, BorderLayout.SOUTH);
        } else {
            remove(emptyMessage);
        }
        revalidate();
        repaint();
    }
}