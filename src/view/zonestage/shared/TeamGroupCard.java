package view.zonestage.shared;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.team.Team;
import view.theme.Theme;

public class TeamGroupCard extends JPanel {

    public TeamGroupCard(String title, List<Team> teams) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 6, 14));
        add(titleLabel);

        add(new JSeparator());

        for (Team team : teams) {
            add(new TeamRowPanel(team));
        }
    }
}
