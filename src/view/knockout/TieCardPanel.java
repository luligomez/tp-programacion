package view.knockout;

import model.match.knockout.KnockoutTie;

import javax.swing.*;
import java.awt.*;

public class TieCardPanel extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);

    public TieCardPanel(KnockoutTie tie, int number, boolean firstLegPlayed) {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel title = new JLabel("Tie " + number, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(BLUE);
        add(title, BorderLayout.NORTH);

        boolean secondLegPlayed = tie.isResolved();

        // En la vuelta los equipos están invertidos
        String leg1Team1 = firstLegPlayed ? String.valueOf(tie.getFirstLeg().getTeam1Goals()) : "-";
        String leg1Team2 = firstLegPlayed ? String.valueOf(tie.getFirstLeg().getTeam2Goals()) : "-";
        String leg2Team1 = secondLegPlayed ? String.valueOf(tie.getSecondLeg().getTeam2Goals()) : "-";
        String leg2Team2 = secondLegPlayed ? String.valueOf(tie.getSecondLeg().getTeam1Goals()) : "-";

        String global1 = "-";
        String global2 = "-";
        if (secondLegPlayed) {
            global1 = String.valueOf(tie.getFirstLeg().getTeam1Goals() + tie.getSecondLeg().getTeam2Goals());
            global2 = String.valueOf(tie.getFirstLeg().getTeam2Goals() + tie.getSecondLeg().getTeam1Goals());
        }

        Color gray = new Color(130, 130, 130);
        JPanel body = new JPanel(new GridLayout(3, 1, 0, 10));
        body.setOpaque(false);
        body.add(createRow("Team", "1st Leg", "2nd Leg", "Global",
                new Font("SansSerif", Font.BOLD, 13), gray));
        body.add(createRow(tie.getTeam1().getName(), leg1Team1, leg2Team1, global1,
                new Font("SansSerif", Font.PLAIN, 15), Color.BLACK));
        body.add(createRow(tie.getTeam2().getName(), leg1Team2, leg2Team2, global2,
                new Font("SansSerif", Font.PLAIN, 15), Color.BLACK));
        add(body, BorderLayout.CENTER);

        JPanel footer = getJPanel(tie, secondLegPlayed, gray);

        add(footer, BorderLayout.SOUTH);
    }

    private static JPanel getJPanel(KnockoutTie tie, boolean secondLegPlayed, Color gray) {
        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 4));
        footer.setOpaque(false);

        if (secondLegPlayed && tie.getWinner() != null) {
            JLabel winner = new JLabel(
                    "Winner: " + tie.getWinner().getName() + " (" + tie.getWinningCriteria() + ")",
                    SwingConstants.CENTER);
            winner.setFont(new Font("SansSerif", Font.BOLD, 14));
            winner.setForeground(BLUE);
            footer.add(winner);
        }

        JLabel homeNote = new JLabel(
                "Home: " + tie.getTeam1().getName() + " (1st leg) · " + tie.getTeam2().getName() + " (2nd leg)",
                SwingConstants.CENTER);
        homeNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        homeNote.setForeground(gray);
        footer.add(homeNote);
        return footer;
    }

    private JPanel createRow(String name, String a, String b, String c, Font font, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(font);
        nameLabel.setForeground(color);
        row.add(nameLabel, BorderLayout.WEST);

        JPanel cells = new JPanel(new GridLayout(1, 3, 5, 0));
        cells.setOpaque(false);
        cells.add(createCell(a, font, color));
        cells.add(createCell(b, font, color));
        cells.add(createCell(c, font, color));
        row.add(cells, BorderLayout.EAST);

        return row;
    }

    private JLabel createCell(String text, Font font, Color color) {
        JLabel cell = new JLabel(text, SwingConstants.CENTER);
        cell.setFont(font);
        cell.setForeground(color);
        cell.setPreferredSize(new Dimension(60, 20));
        return cell;
    }
}