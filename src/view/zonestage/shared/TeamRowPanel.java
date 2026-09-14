package view.zonestage.shared;

import javax.swing.*;
import java.awt.*;
import model.Team;

public class TeamRowPanel extends JPanel {
    private static final Color RANK_BADGE_BG = new Color(235, 240, 250);
    private static final Color ACCENT = new Color(35, 95, 190);

    public TeamRowPanel(Team team) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea nameLabel = new JTextArea(team.getName());
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setOpaque(false);
        nameLabel.setLineWrap(true);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setFont(nameLabel.getFont().deriveFont(13f));
        nameLabel.setBorder(null);
        nameLabel.setHighlighter(null);

        GridBagConstraints nameConstraints = new GridBagConstraints();
        nameConstraints.gridx = 0;
        nameConstraints.weightx = 1.0;
        nameConstraints.fill = GridBagConstraints.HORIZONTAL;
        nameConstraints.anchor = GridBagConstraints.NORTHWEST;

        JLabel rankBadge = new JLabel("#" + team.getRankingPosition(), SwingConstants.CENTER);
        rankBadge.setFont(rankBadge.getFont().deriveFont(Font.BOLD, 11f));
        rankBadge.setOpaque(true);
        rankBadge.setBackground(RANK_BADGE_BG);
        rankBadge.setForeground(ACCENT);
        rankBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        GridBagConstraints badgeConstraints = new GridBagConstraints();
        badgeConstraints.gridx = 1;
        badgeConstraints.anchor = GridBagConstraints.NORTHEAST;
        badgeConstraints.insets = new Insets(0, 8, 0, 0);

        add(nameLabel, nameConstraints);
        add(rankBadge, badgeConstraints);
    }
}
