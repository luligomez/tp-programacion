package view.rankings;

import model.reports.fairplay.FairPlayReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FairPlayRankingView extends JPanel {
    private final DefaultTableModel tableModel;
    private final JLabel emptyMessage;

    public FairPlayRankingView() {

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
                "Single Yellow Cards",
                "Double Yellow Expulsions",
                "Direct Red Cards",
                "Penalty Points"
        };

        this.tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };



        StyledTable styledTable = new StyledTable(tableModel);
        styledTable.centerAllRows();

        styledTable.setFillsViewportHeight(true);
        styledTable.setAutoCreateRowSorter(false);

        JScrollPane scrollPane = new JScrollPane(styledTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);


        emptyMessage = new JLabel(
                    "No Fair Play statistics available.",
                    SwingConstants.CENTER
        );

    }

    public void showRanking(List<FairPlayReportItem> ranking){
        tableModel.setRowCount(0);
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
        if (ranking.isEmpty()) {
            add(emptyMessage, BorderLayout.SOUTH);
        } else {
            remove(emptyMessage);
        }
        revalidate();
        repaint();
    }
}