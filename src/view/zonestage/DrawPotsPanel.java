package view.zonestage;

import model.Team;
import view.zonestage.shared.TeamGroupCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawPotsPanel extends JPanel {
    private static final Color BACKGROUND = new Color(245, 245, 247);

    public DrawPotsPanel(List<List<Team>> pots, Runnable onDrawRequested) {
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);
        // Usamos el wrapper para que las columnas no se estiren verticalmente
        add(buildPotsWrapper(pots), BorderLayout.CENTER);
        add(buildDrawButton(onDrawRequested), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Zone Draw — Pots");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("16 teams grouped by ranking");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel buildPotsWrapper(List<List<Team>> pots) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Se expande a lo ancho de la pantalla
        gbc.weighty = 0.0; // No absorbe espacio vertical sobrante
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Mantiene los bombos pegados arriba

        wrapper.add(buildPotsPanel(pots), gbc);

        return wrapper;
    }

    private JPanel buildPotsPanel(List<List<Team>> pots) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);

        for (int i = 0; i < pots.size(); i++) {
            panel.add(new TeamGroupCard("Pot " + (i + 1), pots.get(i)));
        }
        return panel;
    }

    private JPanel buildDrawButton(Runnable onDrawRequested) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JButton drawButton = new JButton("Draw");
        drawButton.setFont(drawButton.getFont().deriveFont(Font.BOLD, 14f));
        drawButton.setFocusPainted(false);
        drawButton.setBackground(new Color(35, 95, 190));
        drawButton.setForeground(Color.WHITE);
        drawButton.setOpaque(true);
        drawButton.setBorderPainted(false);
        drawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        drawButton.addActionListener(e -> {
            if (onDrawRequested != null) onDrawRequested.run();
        });
        panel.add(drawButton);
        return panel;
    }
}