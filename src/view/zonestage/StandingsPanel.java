package view.zonestage;

import model.Tournament;
import model.zone.Zone;
import view.zonestage.shared.TeamStandingGroupCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StandingsPanel extends JPanel {

    private final JPanel groupsGrid;
    private final JButton btnSimulateMatchday;
    private final JButton btnViewMatchDetails;

    private Runnable onSimulateListener;
    private Runnable onViewMatchDetailsListener;

    public StandingsPanel(Tournament tournament) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Barra Superior de Acciones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        btnSimulateMatchday = new JButton("Simulate Next Matchday ⚽");
        btnSimulateMatchday.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSimulateMatchday.setBackground(new Color(35, 95, 190));
        btnSimulateMatchday.setForeground(Color.WHITE);
        btnSimulateMatchday.setFocusPainted(false);
        btnSimulateMatchday.addActionListener(e -> {
            if (onSimulateListener != null) onSimulateListener.run();
        });

        btnViewMatchDetails = new JButton("View Match Details 🔍");
        btnViewMatchDetails.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnViewMatchDetails.setFocusPainted(false);
        btnViewMatchDetails.addActionListener(e -> {
            if (onViewMatchDetailsListener != null) onViewMatchDetailsListener.run();
        });

        topPanel.add(btnSimulateMatchday);
        topPanel.add(btnViewMatchDetails);
        add(topPanel, BorderLayout.NORTH);

        // Grilla 2x2 con las tarjetas de posiciones
        groupsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        JScrollPane scrollPane = new JScrollPane(groupsGrid);
        scrollPane.setBorder(null);

        updateGroups(tournament);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateGroups(Tournament tournament) {
        groupsGrid.removeAll();

        if (tournament != null && tournament.getZones() != null) {
            List<Zone> zones = tournament.getZones();
            for (int i = 0; i < zones.size(); i++) {
                Zone zone = zones.get(i);
                String title = "Zone " + (char) ('A' + i);

                // Pasamos las posiciones ordenadas de cada zona (TeamStanding)
                groupsGrid.add(new TeamStandingGroupCard(title, zone.getSortedStandings()));
            }
        }

        groupsGrid.revalidate();
        groupsGrid.repaint();
    }

    public void setOnSimulateMatchdayListener(Runnable listener) {
        this.onSimulateListener = listener;
    }

    public void setOnViewMatchDetailsListener(Runnable listener) {
        this.onViewMatchDetailsListener = listener;
    }

    public JButton getBtnSimulateMatchday() {
        return btnSimulateMatchday;
    }
}
