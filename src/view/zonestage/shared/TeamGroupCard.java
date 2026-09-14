package view.zonestage.shared;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Team;

public class TeamGroupCard extends JPanel {
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color ACCENT = new Color(35, 95, 190);

    public TeamGroupCard(String title, List<Team> teams) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        titleLabel.setForeground(ACCENT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        add(titleLabel);

        add(new JSeparator());

        for (Team team : teams) {
            add(new TeamRowPanel(team));
        }
    }
}
