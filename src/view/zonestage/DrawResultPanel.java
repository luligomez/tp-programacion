package view.zonestage;

import model.Team;
import model.zone.Zone;
import view.zonestage.shared.TeamGroupCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawResultPanel extends JPanel {
    private static final Color BACKGROUND = new Color(245, 245, 247);
    private static final Color ACCENT = new Color(35, 95, 190);

    public DrawResultPanel(List<Zone> zones, Runnable onConfirmed, Runnable onRedraw) {
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildZonesWrapper(zones), BorderLayout.CENTER);
        add(buildActionButtons(onConfirmed, onRedraw), BorderLayout.SOUTH);
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

    private JPanel buildZonesWrapper(List<Zone> zones) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Se expande a lo ancho de la pantalla
        gbc.weighty = 0.0; // No absorbe espacio vertical sobrante
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Mantiene los bombos pegados arriba

        wrapper.add(buildZonesPanel(zones), gbc);

        return wrapper;
    }

    private JPanel buildZonesPanel(List<Zone> zones) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);

        for (int i = 0; i < zones.size(); i++) {
            panel.add(new TeamGroupCard("Zone " + (char) ('A' + i), zones.get(i).getTeams()));
        }
        return panel;
    }

    private JPanel buildActionButtons(Runnable onConfirmed, Runnable onRedraw) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);

        JButton redrawButton = new JButton("Redraw");
        redrawButton.setFocusPainted(false);
        redrawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        redrawButton.addActionListener(e -> {
            if (onRedraw != null) onRedraw.run();
        });

        JButton confirmButton = new JButton("Confirm Draw");
        confirmButton.setFont(confirmButton.getFont().deriveFont(Font.BOLD, 14f));
        confirmButton.setFocusPainted(false);
        confirmButton.setBackground(ACCENT);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            if (onConfirmed != null) onConfirmed.run();
        });

        panel.add(redrawButton);
        panel.add(confirmButton);
        return panel;
    }
}