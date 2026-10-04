package view;

import model.person.Position;
import model.reports.PlayerReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PlayersView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> cbPosition;
    private final JComboBox<String> cbTeam;

    private List<PlayerReportItem> allPlayerItems = new ArrayList<>();

    private static final Color BACKGROUND_GRAY = new Color(245, 247, 250);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public PlayersView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 1. NORTH PANEL: Header + Filters
        JPanel northPanel = new JPanel(new BorderLayout(0, 15));
        northPanel.setOpaque(false);

        northPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Load positions automatically using Position.toString()
        List<String> posOptions = new ArrayList<>();
        posOptions.add("All");
        for (Position pos : Position.values()) {
            posOptions.add(pos.toString()); // Returns "Goalkeeper", "Defender", etc.
        }

        cbPosition = new JComboBox<>(posOptions.toArray(new String[0]));
        cbTeam = new JComboBox<>(new String[]{"All"});

        northPanel.add(createFilterPanel(), BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 2. TABLE
        String[] columns = {
                "Player", "Team", "Position", "Age",
                "Matches Played", "Minutes", "Goals", "Goals Conceded (GK)", "Avg. Goals/Match (GK)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Le indica a Swing el tipo real (Integer, Double, String) de la celda
                if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
                    return getValueAt(0, columnIndex).getClass();
                }
                return Object.class;
            }
        };

        table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setColumnWidths(new int[]{180, 150, 110, 40, 100, 50, 50, 120, 120});
        table.setAutoCreateRowSorter(true);
        table.setColumnSuffix(3," yrs");
        table.setColumnSuffix(5,"'");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Players and Statistics");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Search and filter by position, team, and performance");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);

        panel.add(lblTitle);
        panel.add(lblSubtitle);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPos = new JLabel("Position:");
        lblPos.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel lblTeam = new JLabel("Team:");
        lblTeam.setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(lblPos);
        panel.add(cbPosition);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(lblTeam);
        panel.add(cbTeam);

        return panel;
    }

    public void addFilterListener(ActionListener listener) {
        cbPosition.addActionListener(listener);
        cbTeam.addActionListener(listener);
    }

    public void setPlayersData(List<PlayerReportItem> items, List<String> teamNames) {
        this.allPlayerItems = items;

        cbTeam.removeAllItems();
        cbTeam.addItem("All");
        for (String team : teamNames) {
            cbTeam.addItem(team);
        }

        applyFilters();
    }

    public void applyFilters() {
        if (allPlayerItems == null) return;

        String selectedPos = (String) cbPosition.getSelectedItem();
        String selectedTeam = (String) cbTeam.getSelectedItem();

        tableModel.setRowCount(0);

        for (PlayerReportItem item : allPlayerItems) {
            String posString = item.getPosition() != null ? item.getPosition().toString() : "Unknown";

            // Position Filter
            boolean matchesPos = "All".equals(selectedPos) || posString.equalsIgnoreCase(selectedPos);

            // Team Filter
            boolean matchesTeam = "All".equals(selectedTeam) || item.getTeamName().equalsIgnoreCase(selectedTeam);

            if (matchesPos && matchesTeam) {
                boolean isGoalkeeper = item.getPosition() == Position.GOALKEEPER;

                tableModel.addRow(new Object[]{
                        item.getPlayerName(),
                        item.getTeamName(),
                        posString,
                        item.getAge(),
                        item.getMatchesPlayed(),
                        item.getMinutesPlayed(),
                        item.getGoalsScored(),
                        isGoalkeeper ? item.getGoalsConceded() : "-",
                        isGoalkeeper ? String.format("%.2f", item.getGoalsConcededPerMatch()) : "-"
                });
            }
        }
    }
}