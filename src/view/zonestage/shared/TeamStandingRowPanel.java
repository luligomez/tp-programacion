package view.zonestage.shared;

import model.tournament.zone.TeamStanding;

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
        posLabel.setFont(posLabel.getFont().deriveFont(Font.BOLD, 12f));
        posLabel.setPreferredSize(new Dimension(24, 20));

        JLabel nameLabel = new JLabel(standing.getTeam().getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 13f));

        leftPanel.add(posLabel);
        leftPanel.add(nameLabel);
        add(leftPanel, BorderLayout.CENTER);

        // 2. Estadísticas: Pts, PJ, PG, PE, PP, GF, GC, DG (Derecha)
        JPanel statsPanel = new JPanel(new GridLayout(1, 8, 11, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatLabel(String.valueOf(standing.getPoints()), true));       // Pts
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesPlayed()), false)); // MP
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesWon()), false));    // W
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesDrawn()), false));  // D
        statsPanel.add(createStatLabel(String.valueOf(standing.getMatchesLost()), false));   // L
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalsFor()), false));      // GF
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalsAgainst()), false));  // GA
        statsPanel.add(createStatLabel(String.valueOf(standing.getGoalDifference()), false)); // GD

        add(statsPanel, BorderLayout.EAST);
    }

    private JLabel createStatLabel(String text, boolean isPoints) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(22, 20));
        if (isPoints) {
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
            lbl.setForeground(ACCENT);
        } else {
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
            lbl.setForeground(Color.DARK_GRAY);
        }
        return lbl;
    }
}
