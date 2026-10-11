package view.match;

import model.person.Position;
import model.reports.MatchReport;
import model.reports.MatchReport.*;
import model.match.knockout.PenaltyShootout.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MatchDetailView extends JPanel {
    private static final Color BACKGROUND = new Color(245, 245, 247);
    private static final Color ACCENT = new Color(35, 95, 190);
    private static final Color TAG_BG = new Color(235, 240, 250);
    private static final Color GRAY = new Color(120, 120, 120);

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

    public MatchDetailView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(buildHeader(), BorderLayout.NORTH);

        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lineupsPanel.setBackground(Color.WHITE);

        shootoutPanel.setLayout(new BoxLayout(shootoutPanel, BoxLayout.Y_AXIS));
        shootoutPanel.setBackground(Color.WHITE);
        shootoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        shootoutScroll = scroll(shootoutPanel);

        tabs.addTab("Timeline", scroll(timelinePanel));
        tabs.addTab("Lineups", scroll(lineupsPanel));
        add(tabs, BorderLayout.CENTER);
    }

    // ---------- API para el controller ----------

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
        scoreLabel.setForeground(ACCENT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        infoLabel.setForeground(GRAY);
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

    // Una fila: [evento equipo 1] | minuto | [evento equipo 2]
    private JPanel buildTimelineRow(TimelineEvent event) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel left = new JLabel("", SwingConstants.RIGHT);
        JLabel right = new JLabel("", SwingConstants.LEFT);
        // ancho 0 para que el espacio sobrante se reparta 50/50 y el minuto quede siempre centrado
        left.setPreferredSize(new Dimension(0, 24));
        right.setPreferredSize(new Dimension(0, 24));

        boolean isTeam1 = event.side() == Side.TEAM1;
        JLabel target = isTeam1 ? left : right;
        target.setText(textFor(event));
        target.setIcon(iconFor(event.type()));
        target.setIconTextGap(8);
        if (isTeam1) {
            target.setHorizontalTextPosition(SwingConstants.LEFT); // texto a la izquierda, ícono pegado al minuto
        }

        JLabel minute = tag(event.minute() + "'", 12f, 10);
        minute.setPreferredSize(new Dimension(52, 22)); // ancho fijo: todos los minutos alineados

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1; c.insets = new Insets(0, 0, 0, 12); row.add(left, c);
        c.gridx = 1; c.weightx = 0; c.insets = new Insets(0, 0, 0, 0);  row.add(minute, c);
        c.gridx = 2; c.weightx = 1; c.insets = new Insets(0, 12, 0, 0); row.add(right, c);
        return row;
    }

    private Icon iconFor(EventType type) {
        return switch (type) {
            case GOAL, PENALTY_GOAL -> new EventIcon(EventIcon.Kind.GOAL);
            case OWN_GOAL -> new EventIcon(EventIcon.Kind.OWN_GOAL);
            case YELLOW_CARD -> new EventIcon(EventIcon.Kind.YELLOW_CARD);
            case RED_CARD, SECOND_YELLOW_RED -> new EventIcon(EventIcon.Kind.RED_CARD);
            case SUBSTITUTION -> new EventIcon(EventIcon.Kind.SUBSTITUTION);
        };
    }

    private String textFor(TimelineEvent event) {
        return switch (event.type()) {
            case PENALTY_GOAL -> event.playerName() + " (pen.)";
            case OWN_GOAL -> event.playerName() + " (o.g.)";
            case SECOND_YELLOW_RED -> event.playerName() + " (second yellow)";
            case SUBSTITUTION -> "In: " + event.otherPlayerName() + " · Out: " + event.playerName();
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

        column.add(sectionLabel(lineup.teamName(), 16f, ACCENT));
        column.add(sectionLabel("Starters", 12f, GRAY));
        for (LineupPlayer player : lineup.starters()) {
            column.add(buildPlayerRow(player, false));
        }
        column.add(sectionLabel("Substitutes", 12f, GRAY));
        for (LineupPlayer player : lineup.substitutes()) {
            // un suplente que nunca entró se muestra en gris (solo si el partido ya se jugó)
            column.add(buildPlayerRow(player, played && !player.cameOn()));
        }
        return column;
    }

    private JPanel buildPlayerRow(LineupPlayer player, boolean dimmed) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel name = new JLabel(player.name());
        if (dimmed) {
            name.setForeground(GRAY);
        }

        row.add(positionTag(player.position()), BorderLayout.WEST);
        row.add(name, BorderLayout.CENTER);
        row.add(marksPanel(player.marks()), BorderLayout.EAST);
        return row;
    }

    // Pastilla de posición, sin borde interno para que "GK" entre sin truncarse
    private JLabel positionTag(Position position) {
        JLabel label = new JLabel(positionLabel(position), SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        label.setOpaque(true);
        label.setBackground(positionColor(position));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(34, 20));
        return label;
    }

    private Color positionColor(Position position) {
        return switch (position) {
            case GOALKEEPER -> new Color(249, 168, 37);
            case DEFENDER -> new Color(35, 95, 190);
            case MIDFIELDER -> new Color(46, 125, 50);
            case FORWARD -> new Color(198, 40, 40);
        };
    }

    private JPanel marksPanel(List<PlayerMark> marks) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        for (PlayerMark mark : marks) {
            JLabel label = new JLabel(mark.minute() + "'", markIcon(mark.type()), SwingConstants.LEFT);
            label.setIconTextGap(3);
            label.setFont(label.getFont().deriveFont(11f));
            panel.add(label);
        }
        return panel;
    }

    private Icon markIcon(MarkType type) {
        return switch (type) {
            case GOAL -> new EventIcon(EventIcon.Kind.GOAL);
            case OWN_GOAL -> new EventIcon(EventIcon.Kind.OWN_GOAL);
            case YELLOW -> new EventIcon(EventIcon.Kind.YELLOW_CARD);
            case RED -> new EventIcon(EventIcon.Kind.RED_CARD);
            case SUB_OUT -> new EventIcon(EventIcon.Kind.SUB_OUT);
            case SUB_IN -> new EventIcon(EventIcon.Kind.SUB_IN);
        };
    }

    private String positionLabel(Position position) {
        return switch (position) {
            case GOALKEEPER -> "GK";
            case DEFENDER -> "DF";
            case MIDFIELDER -> "MF";
            case FORWARD -> "FW";
        };
    }

    // ---------- Helpers ----------

    private JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel message(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(GRAY);
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

    // Etiqueta tipo "píldora" (minuto o posición)
    private JLabel tag(String text, float size, int horizontalPadding) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        label.setOpaque(true);
        label.setBackground(TAG_BG);
        label.setForeground(ACCENT);
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
        summary.setFont(summary.getFont().deriveFont(Font.BOLD, 15f));
        summary.setForeground(ACCENT);
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));
        shootoutPanel.add(summary);

        for (PenaltyRound round : shootout.rounds()) {
            shootoutPanel.add(buildShootoutRow(round));
        }
        shootoutPanel.revalidate();
        shootoutPanel.repaint();
        tabs.addTab("Penalty shootout", shootoutScroll);
    }

    // [patada equipo 1] | ronda | [patada equipo 2]
    private JPanel buildShootoutRow(PenaltyRound round) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel left = kickLabel(round.team1Kick(), true);
        JLabel right = kickLabel(round.team2Kick(), false);

        JLabel number = tag(String.valueOf(round.number()), 12f, 10);
        number.setPreferredSize(new Dimension(52, 22));

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1; c.insets = new Insets(0, 0, 0, 12); row.add(left, c);
        c.gridx = 1; c.weightx = 0; c.insets = new Insets(0, 0, 0, 0);  row.add(number, c);
        c.gridx = 2; c.weightx = 1; c.insets = new Insets(0, 12, 0, 0); row.add(right, c);
        return row;
    }

    private JLabel kickLabel(PenaltyKick kick, boolean rightAligned) {
        JLabel label = new JLabel("", rightAligned ? SwingConstants.RIGHT : SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(0, 24)); // ancho 0: reparto 50/50, igual que en el timeline
        if (kick == null) return label;               // el equipo 2 no llegó a patear esta ronda

        label.setText(kick.getKicker().getName());
        label.setIcon(new EventIcon(kick.hasScored() ? EventIcon.Kind.PENALTY_SCORED : EventIcon.Kind.PENALTY_MISSED));
        label.setIconTextGap(8);
        if (rightAligned) label.setHorizontalTextPosition(SwingConstants.LEFT);
        return label;
    }

}