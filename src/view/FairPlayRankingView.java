package view;

import model.reports.fairplay.FairPlayReportItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FairPlayRankingView extends JPanel {

    public FairPlayRankingView(List<FairPlayReportItem> ranking) {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Fair Play Ranking");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(35, 95, 190));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Position",
                "Team",
                "Yellow Cards",
                "Double Yellow",
                "Red Cards",
                "Penalty Points"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int position = 1;

        for (FairPlayReportItem item : ranking) {

            tableModel.addRow(new Object[]{
                    position++,
                    item.getTeamName(),
                    item.getYellowCards(),
                    item.getDoubleYellowExpulsions(),
                    item.getDirectRedCards(),
                    item.getPenaltyPoints()
            });
        }

        JTable table = new JTable(tableModel);

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        if (ranking.isEmpty()) {
            JLabel message = new JLabel(
                    "No Fair Play statistics available.",
                    SwingConstants.CENTER
            );
            add(message, BorderLayout.SOUTH);
        }
    }
}