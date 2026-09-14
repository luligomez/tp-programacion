package view.zonestage;

import model.Tournament;
import model.zone.Zone;
import view.zonestage.shared.TeamGroupCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawResultPanel extends JPanel {
    private static final Color BACKGROUND = new Color(245, 245, 247);
    private static final Color ACCENT = new Color(35, 95, 190);

    private Tournament tournament;
    private Runnable onConfirmed;
    private Runnable onRedraw;

    public DrawResultPanel(Tournament tournament, Runnable onConfirmed, Runnable onRedraw) {
        this.tournament = tournament;
        this.onConfirmed = onConfirmed;
        this.onRedraw = onRedraw;
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildZonesPanel(), BorderLayout.CENTER);
        add(buildActionButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Zone Draw Result");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("16 teams — 4 balanced zones");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel buildZonesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        List<Zone> zones = tournament.getZones();

        for (int i = 0; i < zones.size(); i++) {
            panel.add(new TeamGroupCard("Zone " + (i + 1), zones.get(i).getTeams()));
        }
        return panel;
    }

    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);

        JButton redrawButton = new JButton("Redraw");
        redrawButton.setFocusPainted(false);
        redrawButton.addActionListener(e -> onRedraw.run());

        JButton confirmButton = new JButton("Confirm Draw");
        confirmButton.setFont(confirmButton.getFont().deriveFont(Font.BOLD, 14f));
        confirmButton.setFocusPainted(false);
        confirmButton.setBackground(ACCENT);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.addActionListener(e -> onConfirmed.run());

        panel.add(redrawButton);
        panel.add(confirmButton);
        return panel;
    }
}