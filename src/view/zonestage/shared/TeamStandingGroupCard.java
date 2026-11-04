package view.zonestage.shared;

import model.tournament.zone.TeamStanding;
import view.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeamStandingGroupCard extends JPanel {

    public TeamStandingGroupCard(String title, List<TeamStanding> standings) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));

        // Título de la Zona (ej. "Zone A")
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        add(titleLabel);

        // Encabezado de columnas (Pts, PJ, PG, PE, PP, GF, GC, DG)
        add(createHeaderPanel());
        add(new JSeparator());

        // Filas de equipos ordenadas por puntos/posición
        for (int i = 0; i < standings.size(); i++) {
            add(new TeamStandingRowPanel(i + 1, standings.get(i)));
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(2, 10, 4, 10));

        JLabel teamHeader = new JLabel("Team");
        teamHeader.setFont(teamHeader.getFont().deriveFont(Font.BOLD, 12f));
        teamHeader.setForeground(Color.GRAY);
        header.add(teamHeader, BorderLayout.CENTER);

        JPanel cols = new JPanel(new GridLayout(1, 8, 4, 0));
        cols.setOpaque(false);
        String[] titles = {"PTS", "MP", "W", "D", "L", "GF", "GA", "GD"};
        for (String t : titles) {
            JLabel lbl = new JLabel(t, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
            lbl.setForeground(Color.GRAY);
            lbl.setPreferredSize(new Dimension(28, 16));
            cols.add(lbl);
        }
        header.add(cols, BorderLayout.EAST);

        return header;
    }
}
