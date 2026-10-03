package view.zonestage;

import model.match.GroupStageMatch;
import model.zone.Zone;
import view.zonestage.shared.TeamStandingGroupCard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StandingsPanel extends JPanel {

    private static final Color PRIMARY = new Color(35, 95, 190);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BG_ALT = new Color(246, 248, 252);
    private static final Color SELECTED_BG = new Color(222, 234, 252);
    private static final Color MUTED = new Color(120, 125, 135);

    private List<Zone> zones;

    private final JPanel groupsGrid;
    private final JButton btnSimulateMatchday;

    // FILTROS
    private final JComboBox<String> comboZoneFilter;
    private final JComboBox<String> comboMatchdayFilter;

    // LISTA DE PARTIDOS (FIXTURE)
    private final DefaultListModel<MatchRow> matchesListModel;
    private final JList<MatchRow> matchesList;

    /** Fila del fixture: partido + nombre de la zona a la que pertenece. */
    public static class MatchRow {
        final String zoneName;
        final GroupStageMatch match;

        MatchRow(String zoneName, GroupStageMatch match) {
            this.zoneName = zoneName;
            this.match = match;
        }

        public GroupStageMatch getMatch() { return match; }
        public String getZoneName() { return zoneName; }
    }

    public StandingsPanel(List<Zone> zones) {
        this.zones = zones;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

// ------------------------------------------------------------------
// 1. BARRA SUPERIOR (Botón de Simulación)
// ------------------------------------------------------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnSimulateMatchday = new JButton("Simulate Next Matchday 1 ⚽");
        btnSimulateMatchday.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSimulateMatchday.setBackground(PRIMARY);
        btnSimulateMatchday.setForeground(Color.BLACK);
        btnSimulateMatchday.setFocusPainted(false);
        btnSimulateMatchday.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 2, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btnSimulateMatchday.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimulateMatchday.setOpaque(true);

        topPanel.add(btnSimulateMatchday);
        add(topPanel, BorderLayout.NORTH);

// ------------------------------------------------------------------
// 2. PANEL CONTENEDOR PRINCIPAL (Agrupa verticalmente)
// ------------------------------------------------------------------
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

// A) Tablas de Posiciones (Grilla 2x2)
        groupsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        groupsGrid.setPreferredSize(new Dimension(0, 600));
        contentPanel.add(groupsGrid);

// Espaciador entre las tablas y el fixture
        contentPanel.add(Box.createVerticalStrut(20));

// B) Fixture con Filtros de Zona y Fecha
        JPanel fixtureContainer = new JPanel(new BorderLayout(0, 8));
        fixtureContainer.setBorder(BorderFactory.createTitledBorder("MATCHES & FIXTURE"));

// Barra de filtros horizontal
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        comboZoneFilter = new JComboBox<>(new String[]{"All", "Zone A", "Zone B", "Zone C", "Zone D"});
        comboMatchdayFilter = new JComboBox<>(new String[]{"All", "Matchday 1", "Matchday 2", "Matchday 3"});

        filterBar.add(new JLabel("Zone:"));
        filterBar.add(comboZoneFilter);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(new JLabel("Matchday:"));
        filterBar.add(comboMatchdayFilter);
        fixtureContainer.add(filterBar, BorderLayout.NORTH);

// Lista de partidos (Sin JScrollPane propio para que expanda su altura)
        matchesListModel = new DefaultListModel<>();
        matchesList = new JList<>(matchesListModel);
        matchesList.setCellRenderer(new MatchCellRenderer());
        matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchesList.setFixedCellHeight(40);
        matchesList.setLayoutOrientation(JList.VERTICAL);
        matchesList.setVisibleRowCount(-1); // Permite que el JList crezca a lo largo según la cantidad de partidos

        fixtureContainer.add(matchesList, BorderLayout.CENTER);

        comboZoneFilter.addActionListener(e -> refreshMatchesList());
        comboMatchdayFilter.addActionListener(e -> refreshMatchesList());

        contentPanel.add(fixtureContainer);

// ------------------------------------------------------------------
// 3. UN SOLO JSCROLLPANE PARA TODA LA PANTALLA
// ------------------------------------------------------------------
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll fluido con la rueda del mouse
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(mainScrollPane, BorderLayout.CENTER);

        updateGroups(zones);
    }

    /**
     * Refresca tanto las tablas como la lista filtrada de partidos.
     */
    public void updateGroups(List<Zone> zones) {
        this.zones = zones;
        groupsGrid.removeAll();
        if (zones != null) {
            for (int i = 0; i < zones.size(); i++) {
                Zone zone = zones.get(i);
                String title = "Zone " + (char) ('A' + i);
                groupsGrid.add(new TeamStandingGroupCard(title, zone.getSortedStandings()));
            }
        }
        groupsGrid.revalidate();
        groupsGrid.repaint();

        refreshMatchesList();
    }

    /**
     * Aplica los filtros de Zona y Matchday sobre los partidos de las zonas.
     */
    public void refreshMatchesList() {
        matchesListModel.clear();
        if (zones == null) return;

        int selectedZone = comboZoneFilter.getSelectedIndex();         // 0 = All
        int selectedMatchday = comboMatchdayFilter.getSelectedIndex(); // 0 = All

        for (int i = 0; i < zones.size(); i++) {
            boolean matchesZone = (selectedZone == 0 || (selectedZone - 1) == i);

            if (matchesZone) {
                Zone zone = zones.get(i);
                String zoneName = "Zone " + (char) ('A' + i);

                for (GroupStageMatch match : zone.getGroupStageMatches()) {
                    boolean matchesMatchday = (selectedMatchday == 0 || match.getMATCHDAY() == selectedMatchday);

                    if (matchesMatchday) {
                        matchesListModel.addElement(new MatchRow(zoneName, match));
                    }
                }
            }
        }
    }

    private static String formatKickoff(GroupStageMatch match) {
        LocalDateTime dt = match.getDate();
        if (dt == null) return "TBD";
        return dt.format(DateTimeFormatter.ofPattern("dd/MM · HH:mm"));
    }

    // ------------------------------------------------------------------
    // RENDERER: cada partido como una tarjeta
    // ------------------------------------------------------------------
    private static class MatchCellRenderer extends JPanel implements ListCellRenderer<MatchRow> {

        private final JLabel lblBadge = new JLabel("", SwingConstants.CENTER);
        private final JLabel lblTeam1 = new JLabel("", SwingConstants.RIGHT);
        private final JLabel lblScore = new JLabel("", SwingConstants.CENTER);
        private final JLabel lblTeam2 = new JLabel("", SwingConstants.LEFT);
        private final JLabel lblStatus = new JLabel("", SwingConstants.RIGHT);

        MatchCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(232, 235, 241));
            setBorder(BorderFactory.createCompoundBorder(line,
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            lblBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
            lblBadge.setForeground(Color.WHITE);
            lblBadge.setOpaque(true);
            lblBadge.setBackground(PRIMARY);
            lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            lblBadge.setPreferredSize(new Dimension(90, 22));

            JPanel badgeWrap = new JPanel(new GridBagLayout());
            badgeWrap.setOpaque(false);
            badgeWrap.add(lblBadge);

            lblTeam1.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblTeam2.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblScore.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblStatus.setPreferredSize(new Dimension(110, 20));

            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.gridy = 0;
            gc.gridx = 0; gc.weightx = 1; center.add(lblTeam1, gc);
            gc.gridx = 1; gc.weightx = 0; gc.insets = new Insets(0, 12, 0, 12);
            lblScore.setPreferredSize(new Dimension(60, 24));
            center.add(lblScore, gc);
            gc.gridx = 2; gc.weightx = 1; gc.insets = new Insets(0, 0, 0, 0);
            center.add(lblTeam2, gc);

            add(badgeWrap, BorderLayout.WEST);
            add(center, BorderLayout.CENTER);
            add(lblStatus, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends MatchRow> list, MatchRow row,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            GroupStageMatch m = row.getMatch();

            lblBadge.setText(row.getZoneName().replace("Zone ", "Z") + " · M" + m.getMATCHDAY());
            lblTeam1.setText(m.getTeam1().getName());
            lblTeam2.setText(m.getTeam2().getName());

            if (m.isPlayed()) {
                lblScore.setText(m.getTeam1Goals() + " - " + m.getTeam2Goals());
                lblScore.setForeground(Color.BLACK);
                lblStatus.setText("Final");
                lblStatus.setForeground(MUTED);
            } else {
                lblScore.setText("vs");
                lblScore.setForeground(MUTED);
                lblStatus.setText("🕒 " + formatKickoff(m));
                lblStatus.setForeground(PRIMARY);
            }

            setBackground(isSelected ? SELECTED_BG : (index % 2 == 0 ? CARD_BG : CARD_BG_ALT));
            return this;
        }
    }

    public JList<MatchRow> getMatchesList() {
        return matchesList;
    }

    public void setOnSimulateMatchdayListener(Runnable listener) {
        btnSimulateMatchday.addActionListener(e -> listener.run());
    }

    public void disableSimulateButton() {
        btnSimulateMatchday.setEnabled(false);
        btnSimulateMatchday.setText("Group Stage Completed 🏁");
    }

    public void setMatchdayLabel(int matchday) {
        btnSimulateMatchday.setText("Simulate Matchday " + matchday + " ⚽");
    }
}