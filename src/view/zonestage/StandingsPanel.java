package view.zonestage;

import model.Tournament;
import model.match.GroupStageMatch;
import model.zone.Zone;
import view.zonestage.shared.TeamStandingGroupCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StandingsPanel extends JPanel {

    private final JPanel groupsGrid;
    private final JButton btnSimulateMatchday;

    // FILTROS
    private final JComboBox<String> comboZoneFilter;
    private final JComboBox<String> comboMatchdayFilter;

    // LISTA DE PARTIDOS (FIXTURE)
    private final DefaultListModel<String> matchesListModel;
    private final JList<String> matchesList;

    public StandingsPanel(Tournament tournament) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ------------------------------------------------------------------
        // 1. BARRA SUPERIOR (Botón de Simulación)
        // ------------------------------------------------------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnSimulateMatchday = new JButton("Simulate Next Matchday ⚽");
        btnSimulateMatchday.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSimulateMatchday.setBackground(new Color(35, 95, 190));
        btnSimulateMatchday.setForeground(Color.WHITE);
        btnSimulateMatchday.setFocusPainted(false);
        topPanel.add(btnSimulateMatchday);
        add(topPanel, BorderLayout.NORTH);

        // ------------------------------------------------------------------
        // 2. PANEL IZQUIERDO/CENTRAL: Tablas de Posiciones (Grilla 2x2)
        // ------------------------------------------------------------------
        groupsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        JScrollPane groupsScroll = new JScrollPane(groupsGrid);
        groupsScroll.setBorder(null);

        // ------------------------------------------------------------------
        // 3. PANEL DERECHO: Fixture con Filtros de Zona y Fecha
        // ------------------------------------------------------------------
        JPanel fixtureContainer = new JPanel(new BorderLayout(8, 8));
        fixtureContainer.setPreferredSize(new Dimension(360, 0));
        fixtureContainer.setBorder(BorderFactory.createTitledBorder("MATCHES & FIXTURE"));

        // Barra de Filtros (Zona + Matchday)
        JPanel filterBar = new JPanel(new GridLayout(2, 2, 6, 6));
        filterBar.add(new JLabel("Zone:"));
        filterBar.add(new JLabel("Matchday:"));

        comboZoneFilter = new JComboBox<>(new String[]{"All", "Zone A", "Zone B", "Zone C", "Zone D"});
        comboMatchdayFilter = new JComboBox<>(new String[]{"All", "Matchday 1", "Matchday 2", "Matchday 3"});

        filterBar.add(comboZoneFilter);
        filterBar.add(comboMatchdayFilter);
        fixtureContainer.add(filterBar, BorderLayout.NORTH);

        // Lista de Partidos
        matchesListModel = new DefaultListModel<>();
        matchesList = new JList<>(matchesListModel);
        matchesList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane matchesScroll = new JScrollPane(matchesList);
        fixtureContainer.add(matchesScroll, BorderLayout.CENTER);

        // Eventos de Filtro
        comboZoneFilter.addActionListener(e -> refreshMatchesList(tournament));
        comboMatchdayFilter.addActionListener(e -> refreshMatchesList(tournament));

        // Divisor entre Tablas de Posiciones y Fixture
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, groupsScroll, fixtureContainer);
        splitPane.setResizeWeight(0.65);
        add(splitPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        updateGroups(tournament);
    }

    /**
     * Refresca tanto las tablas como la lista filtrada de partidos.
     */
    public void updateGroups(Tournament tournament) {
        // 1. Refrescar Tarjetas 2x2
        groupsGrid.removeAll();
        if (tournament != null && tournament.getZones() != null) {
            List<Zone> zones = tournament.getZones();
            for (int i = 0; i < zones.size(); i++) {
                Zone zone = zones.get(i);
                String title = "Zone " + (char) ('A' + i);
                groupsGrid.add(new TeamStandingGroupCard(title, zone.getSortedStandings()));
            }
        }
        groupsGrid.revalidate();
        groupsGrid.repaint();

        // 2. Refrescar Lista de Partidos con los filtros actuales
        refreshMatchesList(tournament);
    }

    /**
     * Aplica los filtros de Zona y Matchday sobre los partidos del torneo.
     */
    public void refreshMatchesList(Tournament tournament) {
        matchesListModel.clear();
        if (tournament == null || tournament.getZones() == null) return;

        int selectedZone = comboZoneFilter.getSelectedIndex();       // 0 = All, 1 = Zone A, ...
        int selectedMatchday = comboMatchdayFilter.getSelectedIndex(); // 0 = All, 1 = M1, ...

        List<Zone> zones = tournament.getZones();

        for (int i = 0; i < zones.size(); i++) {
            // Filtrar por Zona si no es "All"
            if (selectedZone != 0 && (selectedZone - 1) != i) {
                continue;
            }

            Zone zone = zones.get(i);
            String zoneName = "Zone " + (char) ('A' + i);

            for (GroupStageMatch match : zone.getGroupStageMatches()) {
                // Filtrar por Matchday si no es "All"
                if (selectedMatchday != 0 && match.getMATCHDAY() != selectedMatchday) {
                    continue;
                }

                String score = match.isPlayed()
                        ? match.getTeam1Goals() + " - " + match.getTeam2Goals()
                        : " vs ";

                matchesListModel.addElement(String.format(
                        "[%s | M%d] %s %s %s",
                        zoneName,
                        match.getMATCHDAY(),
                        match.getTeam1().getName(),
                        score,
                        match.getTeam2().getName()
                ));
            }
        }
    }

    public JList<String> getMatchesList() {
        return matchesList;
    }

    public void setOnSimulateMatchdayListener(Runnable listener) {
        btnSimulateMatchday.addActionListener(e -> listener.run());
    }

    public void disableSimulateButton() {
        btnSimulateMatchday.setEnabled(false);
        btnSimulateMatchday.setText("Group Stage Completed 🏁");
    }
}
