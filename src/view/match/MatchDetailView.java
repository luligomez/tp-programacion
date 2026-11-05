package view.match;

import model.person.Position;
import model.reports.match.MatchReport;
import model.reports.match.MatchReport.*;
import model.match.knockout.PenaltyShootout.*;
import view.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class MatchDetailView extends JPanel {

    private final JLabel team1Label = new JLabel("", SwingConstants.RIGHT);
    private final JLabel team2Label = new JLabel("", SwingConstants.LEFT);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton backButton = new JButton("← Back");
    private final JPanel timelinePanel = new JPanel();
    private final JPanel lineupsPanel = new JPanel(new BorderLayout());

    private final JTabbedPane tabs = new JTabbedPane();
    private final JPanel shootoutPanel = new JPanel();
    private JScrollPane shootoutScroll;

    private static final Color SEPARATOR = Theme.SEPARATOR;
    private static final int CONTENT_MAX_WIDTH = 1000;
    private static final int TIMELINE_ICON = 22;
    private static final int MARK_ICON = 18;

    private Icon icon(EventIcon.Kind kind, int size) {
        return new EventIcon(kind, size);
    }

    public MatchDetailView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);

        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Theme.BACKGROUND);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lineupsPanel.setBackground(Theme.BACKGROUND);

        shootoutPanel.setLayout(new BoxLayout(shootoutPanel, BoxLayout.Y_AXIS));
        shootoutPanel.setBackground(Theme.BACKGROUND);
        shootoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        shootoutScroll = scroll(shootoutPanel);

        tabs.addTab("Timeline", scroll(timelinePanel));
        tabs.addTab("Lineups", scroll(lineupsPanel));
        add(tabs, BorderLayout.CENTER);
    }

    // ---------- API for the controller ----------

    public void setOnBackListener(Runnable listener) {
        backButton.addActionListener(e -> listener.run());
    }

    public void showReport(MatchReport report) {
        showHeader(report);
        showTimeline(report);
        showLineups(report);
        showShootout(report);
    }


    // ---------- Header ----------

    private void showHeader(MatchReport report) {
        team1Label.setText(report.team1Name());
        team2Label.setText(report.team2Name());
        scoreLabel.setText(report.played() ? report.team1Goals() + " - " + report.team2Goals() : "vs");
        infoLabel.setText(report.date() + "  ·  " + report.stadiumName() + "  ·  Referee: " + report.refereeName());
    }

    private JPanel buildHeader() {
        backButton.setFocusPainted(false);
        JPanel backWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backWrapper.setOpaque(false);
        backWrapper.add(backButton);

        team1Label.setFont(team1Label.getFont().deriveFont(Font.BOLD, 20f));
        team2Label.setFont(team2Label.getFont().deriveFont(Font.BOLD, 20f));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 28f));
        scoreLabel.setForeground(Theme.PRIMARY);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        infoLabel.setForeground(Theme.GRAY);
        infoLabel.setFont(infoLabel.getFont().deriveFont(12f));

        JPanel scoreRow = new JPanel(new GridBagLayout());
        scoreRow.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0; c.weightx = 1; scoreRow.add(team1Label, c);
        c.gridx = 1; c.weightx = 0; scoreRow.add(scoreLabel, c);
        c.gridx = 2; c.weightx = 1; scoreRow.add(team2Label, c);

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setOpaque(false);
        center.add(scoreRow, BorderLayout.CENTER);
        center.add(infoLabel, BorderLayout.SOUTH);

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        header.add(backWrapper, BorderLayout.NORTH);
        header.add(center, BorderLayout.CENTER);
        return header;
    }

    // ---------- Timeline ----------

    private void showTimeline(MatchReport report) {
        timelinePanel.removeAll();

        if (!report.played()) {
            timelinePanel.add(message("This match has not been played yet."));
        } else if (report.timeline().isEmpty()) {
            timelinePanel.add(message("No incidents were recorded in this match."));
        } else {
            for (TimelineEvent event : report.timeline()) {
                timelinePanel.add(buildTimelineRow(event));
            }
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    // One row: [team 1 event] | minute | [team 2 event]
    private JPanel buildTimelineRow(TimelineEvent event) {
        JLabel left = sideLabel(true);
        JLabel right = sideLabel(false);

        boolean isTeam1 = event.side() == Side.TEAM1;
        JLabel target = isTeam1 ? left : right;
        target.setText(textFor(event));
        target.setIcon(iconFor(event.type()));
        if (isTeam1) {
            target.setHorizontalTextPosition(SwingConstants.LEFT); // text on the left, icon next to the minute
        }
        return threeColumnRow(left, centerTag(event.minute() + "'"), right);
    }

    private Icon iconFor(EventType type) {
        return switch (type) {
            case GOAL, PENALTY_GOAL -> icon(EventIcon.Kind.GOAL, TIMELINE_ICON);
            case OWN_GOAL -> icon(EventIcon.Kind.OWN_GOAL, TIMELINE_ICON);
            case YELLOW_CARD -> icon(EventIcon.Kind.YELLOW_CARD, TIMELINE_ICON);
            case RED_CARD, SECOND_YELLOW_RED -> icon(EventIcon.Kind.RED_CARD, TIMELINE_ICON);
            case SUBSTITUTION -> icon(EventIcon.Kind.SUBSTITUTION, TIMELINE_ICON);
        };
    }

    private String textFor(TimelineEvent event) {
        return switch (event.type()) {
            case PENALTY_GOAL -> event.playerName() + " (pen.)";
            case OWN_GOAL -> event.playerName() + " (o.g.)";
            case SECOND_YELLOW_RED -> event.playerName() + " (second yellow)";
            case SUBSTITUTION -> "In: " + event.otherPlayerName() +" ("+event.otherPlayerPosition()+")" +" · Out: " + event.playerName() +" ("+event.otherPlayerPosition()+")";
            default -> event.playerName();
        };
    }

    // ---------- Lineups ----------

    private void showLineups(MatchReport report) {
        lineupsPanel.removeAll();

        if (!report.lineupsConfirmed()) {
            lineupsPanel.add(message("Lineups have not been confirmed yet."), BorderLayout.CENTER);
        } else {
            JLabel title = new JLabel(report.played() ? "Final lineups" : "Starting lineups");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
            title.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));

            JPanel columns = new JPanel(new GridLayout(1, 2, 30, 0));
            columns.setOpaque(false);
            columns.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
            columns.add(buildTeamColumn(report.team1Lineup(), report.played()));
            columns.add(buildTeamColumn(report.team2Lineup(), report.played()));

            lineupsPanel.add(title, BorderLayout.NORTH);
            lineupsPanel.add(columns, BorderLayout.CENTER);
        }
        lineupsPanel.revalidate();
        lineupsPanel.repaint();
    }

    private JPanel buildTeamColumn(TeamLineupReport lineup, boolean played) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);

        column.add(sectionLabel(lineup.teamName(), 16f, Theme.PRIMARY));
        column.add(sectionLabel("Starters", 12f, Theme.GRAY));
        for (LineupPlayer player : lineup.starters()) {
            column.add(buildPlayerRow(player, false));
        }
        column.add(sectionLabel("Substitutes", 12f, Theme.GRAY));
        for (LineupPlayer player : lineup.substitutes()) {
            // a substitute who never came on is shown in gray (only if the match was already played)
            column.add(buildPlayerRow(player, played && !player.cameOn()));
        }
        return column;
    }

    private JPanel buildPlayerRow(LineupPlayer player, boolean dimmed) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, SEPARATOR),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel name = new JLabel(player.name());
        name.setFont(name.getFont().deriveFont(14f));
        if (dimmed) {
            name.setForeground(Theme.GRAY);
        }

        // name and symbols, one after the other and left-aligned
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.X_AXIS));
        info.setOpaque(false);
        info.add(name);
        for (PlayerMark mark : player.marks()) {
            info.add(Box.createHorizontalStrut(14));
            info.add(markLabel(mark));
        }
        info.add(Box.createHorizontalGlue());

        row.add(positionTag(player.position()), BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        return row;
    }

    private JLabel markLabel(PlayerMark mark) {
        JLabel label = new JLabel(mark.minute() + "'", markIcon(mark.type()), SwingConstants.LEFT);
        label.setIconTextGap(4);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    // Position pill, without inner border so that "GK" fits without being truncated
    private JLabel positionTag(Position position) {
        JLabel label = new JLabel(position.getShortName(), SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        label.setOpaque(true);
        label.setBackground(positionColor(position));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(34, 20));
        return label;
    }

    private Color positionColor(Position position) {
        return switch (position) {
            case GOALKEEPER -> Theme.POS_GOALKEEPER;
            case DEFENDER -> Theme.POS_DEFENDER;
            case MIDFIELDER -> Theme.POS_MIDFIELDER;
            case FORWARD -> Theme.POS_FORWARD;
        };
    }


    private Icon markIcon(MarkType type) {
        return switch (type) {
            case GOAL -> icon(EventIcon.Kind.GOAL, MARK_ICON);
            case OWN_GOAL -> icon(EventIcon.Kind.OWN_GOAL, MARK_ICON);
            case YELLOW -> icon(EventIcon.Kind.YELLOW_CARD, MARK_ICON);
            case RED -> icon(EventIcon.Kind.RED_CARD, MARK_ICON);
            case SUB_OUT -> icon(EventIcon.Kind.SUB_OUT, MARK_ICON);
            case SUB_IN -> icon(EventIcon.Kind.SUB_IN, MARK_ICON);
        };
    }

    // ---------- Helpers ----------

    private JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel message(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Theme.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel sectionLabel(String text, float size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        return label;
    }

    // Pill-style label (minute or position)
    private JLabel tag(String text, float size, int horizontalPadding) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        label.setOpaque(true);
        label.setBackground(Theme.PRIMARY_LIGHT);
        label.setForeground(Theme.PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(3, horizontalPadding, 3, horizontalPadding));
        return label;
    }

    private void showShootout(MatchReport report) {
        int index = tabs.indexOfComponent(shootoutScroll);
        if (index >= 0) tabs.removeTabAt(index);
        if (!report.hasShootout()) return;

        PenaltyShootoutReport shootout = report.shootout();
        shootoutPanel.removeAll();

        JLabel summary = new JLabel("Penalties " + shootout.team1Score() + " - " + shootout.team2Score()
                + "   ·   Winner: " + shootout.winnerName());
        summary.setFont(summary.getFont().deriveFont(Font.BOLD, 18f));
        summary.setForeground(Theme.PRIMARY);
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.setBorder(BorderFactory.createEmptyBorder(10, 0, 18, 0));
        shootoutPanel.add(summary);

        for (PenaltyRound round : shootout.rounds()) {
            shootoutPanel.add(buildShootoutRow(round));
        }
        shootoutPanel.revalidate();
        shootoutPanel.repaint();
        tabs.addTab("Penalty shootout", shootoutScroll);
    }

    private JPanel buildShootoutRow(PenaltyRound round) {
        JLabel left = sideLabel(true);
        JLabel right = sideLabel(false);
        fillKick(left, round.team1Kick(), true);
        fillKick(right, round.team2Kick(), false);
        return threeColumnRow(left, centerTag(String.valueOf(round.number())), right);
    }

    private void fillKick(JLabel label, PenaltyKick kick, boolean rightAligned) {
        if (kick == null) return;   // team 2 did not get to kick this round
        label.setText(kick.getKicker().getName());
        label.setIcon(icon(kick.hasScored() ? EventIcon.Kind.PENALTY_SCORED : EventIcon.Kind.PENALTY_MISSED,
                TIMELINE_ICON));
        if (rightAligned) label.setHorizontalTextPosition(SwingConstants.LEFT);
    }

    // [left] | center | [right], with a separator below and centered maximum width
    private JPanel threeColumnRow(JComponent left, JComponent center, JComponent right) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, SEPARATOR),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        row.setMaximumSize(new Dimension(CONTENT_MAX_WIDTH, 52));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Left column (45% of the width)
        gbc.gridx = 0;
        gbc.weightx = 0.45;
        row.add(left, gbc);
        // Center column - Minute (10% of the width)
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 8, 0, 8);
        row.add(center, gbc);
        // Right column (45% of the width)
        gbc.gridx = 2;
        gbc.weightx = 0.45;
        row.add(right, gbc);
        return row;
    }

    // Side label: width 0 so the split is 50/50 and the center always stays centered
    private JLabel sideLabel(boolean rightAligned) {
        JLabel label = new JLabel("", rightAligned ? SwingConstants.RIGHT : SwingConstants.LEFT);
        label.setFont(label.getFont().deriveFont(15f));
        label.setPreferredSize(new Dimension(0, 28));
        label.setIconTextGap(10);
        return label;
    }

    private JLabel centerTag(String text) {
        JLabel tag = tag(text, 14f, 10);
        tag.setPreferredSize(new Dimension(64, 28));
        return tag;
    }

}