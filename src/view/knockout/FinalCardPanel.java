package view.knockout;

import model.Team;
import model.match.knockout.FinalMatch;

import javax.swing.*;
import java.awt.*;

public class FinalCardPanel extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);

    public FinalCardPanel(FinalMatch match, boolean played) {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel title = new JLabel("Final", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(BLUE);
        add(title, BorderLayout.NORTH);

        String goals1 = played ? String.valueOf(match.getTeam1Goals()) : "-";
        String goals2 = played ? String.valueOf(match.getTeam2Goals()) : "-";

        JPanel body = new JPanel(new GridLayout(3, 1, 0, 10));
        body.setOpaque(false);
        body.add(createRow("Team", "Goals", new Font("SansSerif", Font.BOLD, 13), new Color(130, 130, 130)));
        body.add(createRow(match.getTeam1().getName(), goals1, new Font("SansSerif", Font.PLAIN, 15), Color.BLACK));
        body.add(createRow(match.getTeam2().getName(), goals2, new Font("SansSerif", Font.PLAIN, 15), Color.BLACK));
        add(body, BorderLayout.CENTER);

        if (played) {
            // getWinner() va primero: es el que guarda el criterio
            Team winner = match.getWinner();
            if (winner != null) {
                String text = "Winner: " + winner.getName() + " (" + match.getWinningCriteria() + ")";
                if (match.hasPenalties()) {
                    text += " " + match.getPenalties().getTeam1Goals() + "-" + match.getPenalties().getTeam2Goals();
                }
                JLabel winnerLabel = new JLabel(text, SwingConstants.CENTER);
                winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                winnerLabel.setForeground(BLUE);
                add(winnerLabel, BorderLayout.SOUTH);
            }
        }
    }

    private JPanel createRow(String name, String goals, Font font, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(font);
        nameLabel.setForeground(color);
        row.add(nameLabel, BorderLayout.WEST);

        JLabel goalsLabel = new JLabel(goals);
        goalsLabel.setFont(font);
        goalsLabel.setForeground(color);
        row.add(goalsLabel, BorderLayout.EAST);

        return row;
    }
}