package view.zonestage;

import model.tournament.zone.Zone;
import view.theme.Theme;
import view.zonestage.shared.DrawPanelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawResultPanel extends JPanel {

    public DrawResultPanel(
            List<Zone> zones,
            Runnable onConfirmed,
            Runnable onRedraw
    ) {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(
                DrawPanelUtils.createHeader(
                        "Zone Draw Result",
                        "16 teams — 4 balanced zones"
                ),
                BorderLayout.NORTH
        );

        JPanel wrapper = DrawPanelUtils.createGridContainer();
        DrawPanelUtils.addGrid(
                wrapper,
                DrawPanelUtils.createZoneGrid(zones)
        );

        add(wrapper, BorderLayout.CENTER);
        add(buildActionButtons(onConfirmed, onRedraw), BorderLayout.SOUTH);
    }

    private JPanel buildActionButtons(
            Runnable onConfirmed,
            Runnable onRedraw
    ) {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 0)
        );
        panel.setOpaque(false);

        JButton redrawButton = new JButton("Redraw");
        redrawButton.setFocusPainted(false);
        redrawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        redrawButton.addActionListener(e -> {
            if (onRedraw != null) {
                onRedraw.run();
            }
        });

        JButton confirmButton = new JButton("Confirm Draw");
        confirmButton.setFont(
                confirmButton.getFont().deriveFont(Font.BOLD, 14f)
        );
        confirmButton.setFocusPainted(false);
        confirmButton.setBackground(Theme.PRIMARY);
        confirmButton.setForeground(Theme.PRIMARY_LIGHT);
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        confirmButton.addActionListener(e -> {
            if (onConfirmed != null) {
                onConfirmed.run();
            }
        });

        panel.add(redrawButton);
        panel.add(confirmButton);

        return panel;
    }
}