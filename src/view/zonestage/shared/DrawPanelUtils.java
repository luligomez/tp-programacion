package view.zonestage.shared;

import model.team.Team;
import model.tournament.zone.Zone;
import view.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class DrawPanelUtils {

    private DrawPanelUtils() {
    }

    public static JPanel createHeader(String titleText, String subtitleText) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(
                BorderFactory.createEmptyBorder(4, 0, 0, 0)
        );

        header.add(title);
        header.add(subtitle);

        return header;
    }

    public static JPanel createGridContainer() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        return wrapper;
    }

    public static void addGrid(
            JPanel wrapper,
            JPanel grid
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        wrapper.add(grid, gbc);
    }

    public static JPanel createTeamGrid(List<List<Team>> pots) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);

        for (int i = 0; i < pots.size(); i++) {
            panel.add(new TeamGroupCard("Pot " + (i + 1), pots.get(i)));
        }

        return panel;
    }

    public static JPanel createZoneGrid(List<Zone> zones) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);

        for (int i = 0; i < zones.size(); i++) {
            panel.add(new TeamGroupCard(
                    "Zone " + (char) ('A' + i),
                    zones.get(i).getTeams()
            ));
        }

        return panel;
    }
}