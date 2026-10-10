package view.knockout;

import model.match.Match;
import model.match.knockout.FinalMatch;
import model.match.knockout.KnockoutTie;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MatchCardPanel extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);
    private static final Color GRAY = new Color(130, 130, 130);

    private final JLabel lblStatus = new JLabel();
    private final JLabel lblHeader = new JLabel();
    private final JLabel[] names = {new JLabel(), new JLabel()};
    private final JLabel[] scores = {new JLabel(), new JLabel()};
    private final MatchDetailsPanel details = new MatchDetailsPanel();

    private boolean expanded = false;

    public MatchCardPanel() {
        setLayout(new BorderLayout(0, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStatus.setForeground(GRAY);
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblHeader.setForeground(GRAY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblStatus, BorderLayout.WEST);
        top.add(lblHeader, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridLayout(2, 1, 0, 4));
        rows.setOpaque(false);
        for (int i = 0; i < 2; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.add(names[i], BorderLayout.WEST);
            row.add(scores[i], BorderLayout.EAST);
            rows.add(row);
        }
        add(rows, BorderLayout.CENTER);

        details.setVisible(false);
        add(details, BorderLayout.SOUTH);

        showEmpty();
    }

    //expandir o contraer
    public boolean isExpanded() {
        return expanded;
    }

    public void toggleExpanded() {
        if (!details.hasContent()) return;
        expanded = !expanded;
        details.setVisible(expanded);
    }

    public void collapse() {
        expanded = false;
        details.setVisible(false);
    }

    // Cruce de cuartos o semis. Si todavía no existe (null), queda "por definir".
    public void showTie(KnockoutTie tie) {
        if (tie == null) {
            showEmpty();
            return;
        }

        boolean firstPlayed = tie.getFirstLeg().isPlayed();
        boolean secondPlayed = tie.isSecondLegPlayed();
        boolean resolved = tie.isResolved();

        String score1 = firstPlayed ? String.valueOf(tie.getTotalGoals(tie.getTeam1())) : "-";
        String score2 = firstPlayed ? String.valueOf(tie.getTotalGoals(tie.getTeam2())) : "-";

        int winnerIndex = -1;
        if (resolved) {
            winnerIndex = tie.getWinner().equals(tie.getTeam1()) ? 0 : 1;
        }

        String status = resolved ? "Finished" : (secondPlayed ? "Pending Penalties" : "1st leg played");
        show(status, "Global", tie.getTeam1().getName(), tie.getTeam2().getName(), score1, score2, winnerIndex);

        details.showTie(tie);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // La final es un partido único
    public void showFinal(FinalMatch match) {
        if (match == null) {
            showEmpty();
            return;
        }

        boolean played = match.isPlayed();
        String score1 = played ? String.valueOf(match.getTeam1Goals()) : "-";
        String score2 = played ? String.valueOf(match.getTeam2Goals()) : "-";

        int winnerIndex = -1;
        if (played && match.getWinner() != null) {
            winnerIndex = match.getWinner().equals(match.getTeam1()) ? 0 : 1;
        }

        show(played ? "Finished" : "Pending", "Goals",
                match.getTeam1().getName(), match.getTeam2().getName(), score1, score2, winnerIndex);

        details.showFinal(match);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showEmpty() {
        show("To be defined", "", "TBD", "TBD", "", "", -1);
        details.clear();
        collapse();
        setCursor(Cursor.getDefaultCursor());
    }

    private void show(String status, String header, String name1, String name2,
                      String score1, String score2, int winnerIndex) {
        lblStatus.setText(status);
        lblHeader.setText(header);

        String[] nameTexts = {name1, name2};
        String[] scoreTexts = {score1, score2};
        for (int i = 0; i < 2; i++) {
            boolean winner = (i == winnerIndex);
            boolean loser = winnerIndex >= 0 && !winner;

            names[i].setText(nameTexts[i]);
            names[i].setFont(new Font("SansSerif", winner ? Font.BOLD : Font.PLAIN, 14));
            names[i].setForeground(loser ? GRAY : Color.BLACK);

            scores[i].setText(scoreTexts[i]);
            scores[i].setFont(new Font("SansSerif", Font.BOLD, 15));
            scores[i].setForeground(winner ? BLUE : GRAY);
        }
    }

    public void setOnMatchSelectedListener(Consumer<Match> listener) {
        details.setOnMatchSelectedListener(listener);
    }
}