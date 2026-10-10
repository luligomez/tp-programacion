package view.knockout;

import model.Team;
import model.match.Match;
import model.match.knockout.FinalMatch;
import model.match.knockout.KnockoutTie;
import model.person.Referee;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class MatchDetailsPanel extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);
    private static final Color GRAY = new Color(130, 130, 130);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font ROW_FONT = new Font("SansSerif", Font.PLAIN, 13);

    private boolean hasContent = false;

    private Consumer<Match> matchSelectedListener;

    public MatchDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }

    public boolean hasContent() {
        return hasContent;
    }

    public void clear() {
        removeAll();
        hasContent = false;
    }

    // Cuartos y semis: tabla con ida, vuelta y global
    public void showTie(KnockoutTie tie) {
        begin();

        boolean firstPlayed = tie.getFirstLeg().isPlayed();
        boolean secondPlayed = tie.getSecondLeg().isPlayed();
        boolean pens = secondPlayed && tie.getSecondLeg().hasPenalties();
        Team t1 = tie.getTeam1();
        Team t2 = tie.getTeam2();

        String t1Leg1 = firstPlayed ? String.valueOf(tie.getFirstLegGoals(t1)) : "-";
        String t2Leg1 = firstPlayed ? String.valueOf(tie.getFirstLegGoals(t2)) : "-";
        String t1Leg2 = "-", t2Leg2 = "-", t1Total = "-", t2Total = "-";
        if (secondPlayed) {
            int p1 = tie.getSecondLeg().getPenaltiesScored(t1);
            int p2 = tie.getSecondLeg().getPenaltiesScored(t2);
            t1Leg2 = String.valueOf(tie.getSecondLegGoals(t1));
            t2Leg2 = String.valueOf(tie.getSecondLegGoals(t2));
            t1Total = cell(tie.getTotalGoals(t1), p1, pens);
            t2Total = cell(tie.getTotalGoals(t2), p2, pens);
        }

        add(left(tableRow("Team", HEADER_FONT, GRAY, "1st Leg", "2nd Leg", "Total")));
        add(left(tableRow(t1.getName(), ROW_FONT, Color.BLACK, t1Leg1, t1Leg2, t1Total)));
        add(left(tableRow(t2.getName(), ROW_FONT, Color.BLACK, t2Leg1, t2Leg2, t2Total)));

        if (tie.isResolved()) {
            add(left(criterionLabel(tie.getWinningCriteria())));
        }
        add(left(infoBlock("1st leg", tie.getFirstLeg())));
        add(left(infoBlock("2nd leg", tie.getSecondLeg())));

        end();
    }

    // Final: un solo partido
    public void showFinal(FinalMatch match) {
        begin();

        boolean played = match.isPlayed();
        boolean pens = played && match.hasPenalties();
        Team t1 = match.getTeam1();
        Team t2 = match.getTeam2();

        String g1 = played ? cell(match.getTeam1Goals(), match.getPenaltiesScored(t1), pens) : "-";
        String g2 = played ? cell(match.getTeam2Goals(), match.getPenaltiesScored(t2), pens) : "-";

        add(left(tableRow("Team", HEADER_FONT, GRAY, "Goals")));
        add(left(tableRow(t1.getName(), ROW_FONT, Color.BLACK, g1)));
        add(left(tableRow(t2.getName(), ROW_FONT, Color.BLACK, g2)));

        // getWinner() va primero porque es el que guarda el criterio
        if (played && match.getWinner() != null) {
            add(left(criterionLabel(match.getWinningCriteria())));
        }
        add(left(finalInfoLabel(match)));
        add(left(detailsButton(match)));
        end();

        end();
    }

    private void begin() {
        removeAll();
        add(Box.createVerticalStrut(6));
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        add(left(separator));
        add(Box.createVerticalStrut(6));
        hasContent = true;
    }

    private void end() {
        revalidate();
    }

    // Usar html para simplificar el codigo y poder poner los penales en un tamaño mas chico
    private String cell(int goals, int penalties, boolean showPenalties) {
        if (!showPenalties) return String.valueOf(goals);
        return "<html>" + goals + " <font size='-2'>(" + penalties + ")</font></html>";
    }

    private JPanel tableRow(String name, Font font, Color color, String... cells) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(font);
        nameLabel.setForeground(color);
        row.add(nameLabel, BorderLayout.CENTER);

        JPanel cellsPanel = new JPanel(new GridLayout(1, cells.length));
        cellsPanel.setOpaque(false);
        for (String text : cells) {
            JLabel cell = new JLabel(text, SwingConstants.CENTER);
            cell.setFont(font);
            cell.setForeground(color);
            cell.setPreferredSize(new Dimension(45, 20));
            cellsPanel.add(cell);
        }
        row.add(cellsPanel, BorderLayout.EAST);
        return row;
    }

    private JLabel criterionLabel(String criteria) {
        JLabel label = new JLabel("Decided by: " + criteria);
        label.setFont(HEADER_FONT);
        label.setForeground(BLUE);
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        return label;
    }


    private String refereeName(Referee referee) {
        return referee == null ? "-" : referee.getName();
    }

    private <T extends JComponent> T left(T component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }
    // Dos líneas por partido: 1ra cuál es y quién es local, 2da fecha y hora, estadio, árbitro
    private JPanel infoBlock(String legName, Match match) {
        String stadium = match.getStadium() != null ? match.getStadium().getName() : "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String formattedDate = match.getDateTime().format(formatter);


        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        block.add(left(smallLabel(legName + " · Home: " + match.getTeam1().getName(), true)));
        block.add(left(smallLabel(formattedDate + " · " + stadium,false)));
        block.add(left(smallLabel("Ref: " + refereeName(match.getReferee()), false)));
        block.add(Box.createVerticalStrut(3));
        block.add(left(detailsButton(match)));
        return block;
    }

    private JLabel smallLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 11));
        label.setForeground(GRAY);
        return label;
    }

    private JLabel finalInfoLabel(Match match) {
        String stadium = match.getStadium() != null ? match.getStadium().getName() : "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String formattedDate = match.getDateTime().format(formatter);
        JLabel label = smallLabel(formattedDate + " · " + stadium + " · Ref: " + refereeName(match.getReferee()), false);
        label.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        return label;
    }

    private JButton detailsButton(Match match) {
        JButton button = new JButton("Details");
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (matchSelectedListener != null) matchSelectedListener.accept(match);
        });
        return button;
    }

    public void setOnMatchSelectedListener(Consumer<Match> listener) {
        this.matchSelectedListener = listener;
    }

}