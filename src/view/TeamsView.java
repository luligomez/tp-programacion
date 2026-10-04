package view;

import model.reports.TeamReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeamsView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;

    private static final Color PRIMARY_BLUE = new Color(35, 95, 190);
    private static final Color BACKGROUND_GRAY = new Color(245, 247, 250);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public TeamsView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabla y columnas
        String[] columns = {
                "Team", "Avg. Player Age", "Coach Age",
                "Coach Nationality", "GF", "GA", "Effectiveness"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Team Overview");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Team statistics, coaching staff info, and overall effectiveness");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }



    public void setTeamsData(List<TeamReportItem> items) {
        tableModel.setRowCount(0);

        for (TeamReportItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getTeamName(),
                    String.format("%.1f yrs", item.getAveragePlayerAge()),
                    item.getCoachAge() > 0 ? item.getCoachAge() + " yrs" : "N/A",
                    item.getCoachNationality(),
                    item.getGoalsFor(),
                    item.getGoalsAgainst(),
                    String.format("%.1f%%", item.getEffectiveness())
            });
        }
    }
}
