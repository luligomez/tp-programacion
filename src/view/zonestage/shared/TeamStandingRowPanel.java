package view.zonestage.shared;

import model.zone.TeamStanding;

import javax.swing.*;
import java.awt.*;

public class TeamStandingRowPanel extends JPanel {

    private static final Color RANK_BADGE_BG = new Color(235, 240, 250);
    private static final Color ACCENT = new Color(35, 95, 190);

    public TeamStandingRowPanel(int position, TeamStanding standing) {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // 1. Posición y Nombre del Equipo (Izquierda)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftPanel.setOpaque(false);

        JLabel posLabel = new JLabel(position + "º", SwingConstants.CENTER);
        posLabel.setFont(posLabel.getFont().deriveFont(Font.BOLD, 11f));
        posLabel.setPreferredSize(new Dimension(24, 20));

        JLabel nameLabel = new JLabel(standing.getTeam().getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 12f));

        leftPanel.add(posLabel);
        leftPanel.add(nameLabel);
        add(leftPanel, BorderLayout.CENTER);

        // 2. Estadísticas: Pts, PJ, PG, PE, PP, GF, GC, DG (Derecha)
        JPanel statsPanel = new JPanel(new GridLayout(1, 8, 4, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatLabel(String.valueOf(standing.getPoints()), true));       // Pts
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesPlayed()), false)); // PJ
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesWon()), false));    // PG
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesDrawn()), false));  // PE
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesLost()), false));   // PP
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalsFor()), false));      // GF
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalsAgainst()), false));  // GC
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalDifference()), false)); // DG

        add(statsPanel, BorderLayout.EAST);
    }

    private JLabel createStatLabel(String text, boolean isPoints) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(22, 20));
        if (isPoints) {
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
            lbl.setForeground(ACCENT);
        } else {
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 11f));
            lbl.setForeground(Color.DARK_GRAY);
        }
        return lbl;
    }
}
