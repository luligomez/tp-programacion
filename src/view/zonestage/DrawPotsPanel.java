package view.zonestage;

import model.team.Team;
import view.theme.Theme;
import view.zonestage.shared.DrawPanelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawPotsPanel extends JPanel {

    public DrawPotsPanel(List<List<Team>> pots, Runnable onDrawRequested) {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(
                DrawPanelUtils.createHeader(
                        "Zone Draw — Pots",
                        "16 teams grouped by ranking"
                ),
                BorderLayout.NORTH
        );

        JPanel wrapper = DrawPanelUtils.createGridContainer();
        DrawPanelUtils.addGrid(
                wrapper,
                DrawPanelUtils.createTeamGrid(pots)
        );

        add(wrapper, BorderLayout.CENTER);
        add(buildDrawButton(onDrawRequested), BorderLayout.SOUTH);
    }

    private JPanel buildDrawButton(Runnable onDrawRequested) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JButton drawButton = new JButton("Draw");
        drawButton.setFont(drawButton.getFont().deriveFont(Font.BOLD, 14f));
        drawButton.setFocusPainted(false);
        drawButton.setBackground(Theme.PRIMARY);
        drawButton.setForeground(Color.WHITE);
        drawButton.setOpaque(true);
        drawButton.setBorderPainted(false);
        drawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        drawButton.addActionListener(e -> {
            if (onDrawRequested != null) {
                onDrawRequested.run();
            }
        });

        panel.add(drawButton);
        return panel;
    }
}